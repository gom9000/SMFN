package net.gommagomma.smfn.graphics.core;

import java.util.function.Consumer;

/**
 * Gestore interattivo per {@link Viewport3D}: trascinando il puntatore la scena ruota, come
 * afferrando l'oggetto con la mano; con la rotellina la si ingrandisce e con un altro
 * trascinamento la si sposta.
 * <p>
 * Trascinare verso destra fa ruotare la scena verso destra (aumenta l'azimut) e trascinare verso
 * il basso porta verso l'osservatore la parte alta della scena (aumenta l'elevazione, che si
 * ferma a +-90: oltre, la scena si capovolgerebbe). Ogni pixel di trascinamento vale
 * {@link #DEGREES_PER_PIXEL} gradi. La rotellina ingrandisce o riduce la scena attorno al cursore
 * ({@link #onWheelZoom}) e un secondo tipo di trascinamento la sposta ({@link #onPanStart}). L'azimut resta nell'intervallo [-180, 180).
 * <p>
 * Come {@link Viewport2DController}, espone un'API di eventi puramente semantica (coordinate pixel
 * e flag booleani), senza alcuna dipendenza da un toolkit di input concreto: tradurre gli eventi
 * reali spetta a un adapter nel driver (es. {@code SwingViewport3DInputAdapter}).
 * <p>
 * Il gestore non ridisegna nulla: a ogni rotazione notifica la nuova viewport all'host, che la usa
 * per ridisegnare.
 */
public class Viewport3DController
{
    /** Gradi di rotazione per ogni pixel di trascinamento. */
    public static final double DEGREES_PER_PIXEL = 0.5;

    /** Fattore di ingrandimento per ogni scatto della rotellina. */
    public static final double ZOOM_FACTOR = 1.1;
    /** Limiti dell'ingrandimento: oltre, la scena sparirebbe o diventerebbe illeggibile. */
    public static final double MIN_ZOOM = 0.1;
    public static final double MAX_ZOOM = 20.0;

    private Viewport3D currentViewport;
    private final Consumer<Viewport3D> handler;

    private int lastX;
    private int lastY;
    private boolean dragging = false;
    private int lastPanX;
    private int lastPanY;
    private boolean panning = false;


    /**
     * @param initialViewport il punto di partenza
     * @param handler chiamato con la nuova viewport a ogni rotazione effettiva
     * @throws IllegalArgumentException se uno dei due e' nullo
     */
    public Viewport3DController(Viewport3D initialViewport, Consumer<Viewport3D> handler)
    {
        if (initialViewport == null || handler == null) {
            throw new IllegalArgumentException("La viewport iniziale e il gestore non possono essere nulli.");
        }
        this.currentViewport = initialViewport;
        this.handler = handler;
    }


    // --- Eventi di input, in forma neutra rispetto al toolkit ---

    /**
     * Da chiamare quando l'utente preme il pulsante primario del puntatore.
     *
     * @param x coordinata pixel X di partenza
     * @param y coordinata pixel Y di partenza
     */
    public void onDragStart(int x, int y)
    {
        lastX = x;
        lastY = y;
        dragging = true;
    }

    /**
     * Da chiamare a ogni movimento del puntatore mentre il pulsante primario resta premuto. Non fa
     * nulla se il trascinamento non e' iniziato con {@link #onDragStart}, se il pulsante non e'
     * piu' premuto, o se la rotazione risultante non cambia il punto di vista.
     *
     * @param x coordinata pixel X corrente
     * @param y coordinata pixel Y corrente
     * @param primaryButtonHeld true se il pulsante primario e' ancora premuto (alcuni toolkit,
     *        come AWT, non lo riportano nell'evento di drag: lo determina l'adapter concreto)
     */
    public void onDrag(int x, int y, boolean primaryButtonHeld)
    {
        if (!dragging || !primaryButtonHeld) return;

        double azimuth = wrapAzimuth(currentViewport.azimuth + (x - lastX) * DEGREES_PER_PIXEL);
        double elevation = Math.max(-90.0, Math.min(90.0, currentViewport.elevation + (y - lastY) * DEGREES_PER_PIXEL));

        // Il punto di riferimento avanza sempre, anche se l'elevazione e' ferma al limite: cosi'
        // tornare indietro riparte subito dalla posizione del puntatore.
        lastX = x;
        lastY = y;

        if (azimuth == currentViewport.azimuth && elevation == currentViewport.elevation) return;

        currentViewport = currentViewport.withView(azimuth, elevation);
        handler.accept(currentViewport);
    }

    /** Da chiamare quando l'utente rilascia il pulsante primario del puntatore. */
    public void onDragEnd()
    {
        dragging = false;
    }


    /**
     * Da chiamare quando l'utente preme il pulsante con cui si sposta la scena (di solito il
     * secondario, o il primario con Maiusc).
     */
    public void onPanStart(int x, int y)
    {
        lastPanX = x;
        lastPanY = y;
        panning = true;
    }

    /**
     * Da chiamare a ogni movimento del puntatore mentre si sposta la scena: la scena segue il
     * puntatore pixel per pixel. Non fa nulla se lo spostamento non e' iniziato con
     * {@link #onPanStart}, se il pulsante non e' piu' premuto o se il puntatore non si e' mosso.
     *
     * @param panButtonHeld true se il pulsante di spostamento e' ancora premuto
     */
    public void onPan(int x, int y, boolean panButtonHeld)
    {
        if (!panning || !panButtonHeld) return;

        int dx = x - lastPanX;
        int dy = y - lastPanY;
        lastPanX = x;
        lastPanY = y;
        if (dx == 0 && dy == 0) return;

        currentViewport = currentViewport.withOffset(currentViewport.offsetX + dx, currentViewport.offsetY + dy);
        handler.accept(currentViewport);
    }

    /** Da chiamare quando l'utente rilascia il pulsante di spostamento. */
    public void onPanEnd()
    {
        panning = false;
    }

    /**
     * Da chiamare a ogni scatto della rotellina, per lo zoom centrato sul cursore: il punto della
     * scena sotto il cursore resta dov'e'. Non fa nulla se lo zoom e' gia' al limite.
     *
     * @param x coordinata pixel X del cursore al momento dello scatto
     * @param y coordinata pixel Y del cursore al momento dello scatto
     * @param zoomOut true per allontanare (es. rotazione verso il basso), false per avvicinare
     */
    public void onWheelZoom(int x, int y, boolean zoomOut)
    {
        double zoom = currentViewport.zoom * (zoomOut ? 1.0 / ZOOM_FACTOR : ZOOM_FACTOR);
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        if (zoom == currentViewport.zoom) return;

        // Il centro della scena sta a (w/2 + offsetX, h/2 + offsetY); ingrandendo di un fattore f
        // attorno al cursore, la sua distanza dal cursore si moltiplica per f.
        double factor = zoom / currentViewport.zoom;
        double centerX = currentViewport.pixelWidth / 2.0 + currentViewport.offsetX;
        double centerY = currentViewport.pixelHeight / 2.0 + currentViewport.offsetY;
        double newOffsetX = x + (centerX - x) * factor - currentViewport.pixelWidth / 2.0;
        double newOffsetY = y + (centerY - y) * factor - currentViewport.pixelHeight / 2.0;

        currentViewport = new Viewport3D(
            currentViewport.minX, currentViewport.maxX, currentViewport.minY, currentViewport.maxY,
            currentViewport.minZ, currentViewport.maxZ, currentViewport.pixelWidth, currentViewport.pixelHeight,
            currentViewport.azimuth, currentViewport.elevation, zoom, newOffsetX, newOffsetY);
        handler.accept(currentViewport);
    }


    public Viewport3D getCurrentViewport() { return currentViewport; }


    /** Riporta un azimut qualunque nell'intervallo [-180, 180). */
    private static double wrapAzimuth(double azimuth)
    {
        return ((azimuth + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
    }
}
