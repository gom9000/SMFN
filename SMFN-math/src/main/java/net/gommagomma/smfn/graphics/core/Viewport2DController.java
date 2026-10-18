package net.gommagomma.smfn.graphics.core;

/**
 * Gestore interattivo per Viewport2D che calcola nuove Viewport in base all'input utente.
 * Supporta Panning (trascinamento libero) e Zoom Selettivo (rettangolo con Shift/Ctrl), e Zoom
 * con la rotellina.
 */
public class Viewport2DController
{
    private Viewport2D currentViewport;
    private final ViewportUpdateHandler updateHandler;

    private int startX, startY; // Usato per tracciare l'inizio del drag o della selezione
    private boolean selecting = false; // Flag che indica se stiamo disegnando un rettangolo di selezione

    /**
     * Interfaccia di callback per notificare l'host (es. un pannello grafico)
     * che la viewport è cambiata o che deve disegnare un rettangolo temporaneo.
     */
    public interface ViewportUpdateHandler {
        void onViewportUpdated(Viewport2D newViewport);
        // Notifica per il disegno temporaneo del rettangolo di selezione
        void onTemporaryDraw(int x, int y, int width, int height);
    }

    /**
     * @param initialViewport il punto di partenza
     * @param handler riceve le nuove viewport e il rettangolo di selezione temporaneo
     * @throws IllegalArgumentException se uno dei due e' nullo
     */
    public Viewport2DController(Viewport2D initialViewport, ViewportUpdateHandler handler) {
        if (initialViewport == null || handler == null) {
            throw new IllegalArgumentException("La viewport iniziale e il gestore non possono essere nulli.");
        }
        this.currentViewport = initialViewport;
        this.updateHandler = handler;
    }

    // --- Eventi di input, in forma neutra rispetto al toolkit ---

    /**
     * Da chiamare quando l'utente preme il pulsante primario del puntatore.
     *
     * @param x coordinata pixel X di partenza
     * @param y coordinata pixel Y di partenza
     * @param multiSelectModifier true se al momento della pressione era attivo un modificatore
     *        di selezione (es. Shift o Ctrl), che avvia lo zoom selettivo invece del panning
     */
    public void onDragStart(int x, int y, boolean multiSelectModifier) {
        startX = x;
        startY = y;
        selecting = multiSelectModifier;
    }

    /**
     * Da chiamare a ogni movimento del puntatore mentre il pulsante primario resta premuto.
     *
     * @param x coordinata pixel X corrente
     * @param y coordinata pixel Y corrente
     * @param primaryButtonHeld true se il pulsante primario e' ancora premuto durante il
     *        trascinamento (alcuni toolkit, come AWT, non riportano il pulsante nell'evento di
     *        drag stesso: la determinazione va fatta dall'adapter concreto sui modificatori)
     */
    public void onDrag(int x, int y, boolean primaryButtonHeld) {
        if (selecting) {
            // Modalità Zoom Selettivo (con Shift/Ctrl premuto)
            int selX = Math.min(startX, x);
            int selY = Math.min(startY, y);
            int w = Math.abs(startX - x);
            int h = Math.abs(startY - y);
            updateHandler.onTemporaryDraw(selX, selY, w, h);

        } else if (primaryButtonHeld) {
            // Modalità Panning (trascinamento libero)

            // Calcola lo spostamento in pixel dall'inizio del drag o dall'ultima chiamata a dragged
            int dx = x - startX;
            int dy = y - startY;

            // Calcola quanto "vale" un pixel nel mondo matematico. La scala e' la stessa sui due assi
            double mathUnitsPerPixelX = currentViewport.convertPixelXToMathX(1) - currentViewport.convertPixelXToMathX(0);
            double mathUnitsPerPixelY = currentViewport.convertPixelYToMathY(0) - currentViewport.convertPixelYToMathY(1);

            // Calcola lo spostamento matematico effettivo
            double mathDx = dx * mathUnitsPerPixelX;
            double mathDy = dy * mathUnitsPerPixelY;

            // Applica il panning
            double newMinX = currentViewport.minX - mathDx;
            double newMaxX = currentViewport.maxX - mathDx;
            // L'asse Y nei pixel è invertito rispetto al mondo matematico
            double newMinY = currentViewport.minY + mathDy;
            double newMaxY = currentViewport.maxY + mathDy;

            updateViewport(newMinX, newMaxX, newMinY, newMaxY);

            // Reimposta startX/Y per la prossima iterazione di onDrag
            startX = x;
            startY = y;
        }
    }

    /**
     * Da chiamare quando l'utente rilascia il pulsante primario del puntatore.
     *
     * @param x coordinata pixel X di rilascio
     * @param y coordinata pixel Y di rilascio
     */
    public void onDragEnd(int x, int y) {
        if (selecting) {
            selecting = false;
            // Ridisegna una volta per cancellare l'ultimo rettangolo temporaneo
            updateHandler.onTemporaryDraw(0, 0, 0, 0);

            // Calcola la nuova viewport dall'area selezionata in coordinate matematiche
            double newMinX = currentViewport.convertPixelXToMathX(Math.min(startX, x));
            double newMaxX = currentViewport.convertPixelXToMathX(Math.max(startX, x));
            // Inversione Y
            double newMinY = currentViewport.convertPixelYToMathY(Math.max(startY, y));
            double newMaxY = currentViewport.convertPixelYToMathY(Math.min(startY, y));

            updateViewport(newMinX, newMaxX, newMinY, newMaxY);
        }
    }

    /**
     * Da chiamare a ogni scatto della rotellina, per lo zoom centrato sul cursore.
     *
     * @param x coordinata pixel X del cursore al momento dello scatto
     * @param y coordinata pixel Y del cursore al momento dello scatto
     * @param zoomOut true se lo scatto richiede uno zoom out (es. rotazione verso il basso), false per zoom in
     */
    public void onWheelZoom(int x, int y, boolean zoomOut) {
        double zoomFactor = 1.1; // 10% di zoom per scatto
        if (zoomOut) {
            zoomFactor = 1.0 / zoomFactor;
        }

        // Mantieni il centro dello zoom fisso nel punto del cursore
        double mouseMathX = currentViewport.convertPixelXToMathX(x);
        double mouseMathY = currentViewport.convertPixelYToMathY(y);

        // Calcola i nuovi range
        double newRangeX = currentViewport.rangeX / zoomFactor;
        double newRangeY = currentViewport.rangeY / zoomFactor;

        // Calcola i nuovi min/max mantenendo il punto del mouse proporzionale nel nuovo range
        double newMinX = mouseMathX - (mouseMathX - currentViewport.minX) / zoomFactor;
        double newMaxX = newMinX + newRangeX;
        double newMinY = mouseMathY - (mouseMathY - currentViewport.minY) / zoomFactor;
        double newMaxY = newMinY + newRangeY;

        updateViewport(newMinX, newMaxX, newMinY, newMaxY);
    }

    /**
     * Crea e notifica una nuova Viewport valida. Si rifiuta (restando alla viewport corrente) un
     * riquadro che non e' finito o che e' piu' piccolo della precisione dei numeri: il limite e'
     * relativo alla posizione (1e-12 volte la coordinata piu' grande), perche' a coordinate grandi i
     * double non distinguono piu' due valori vicini.
     */
    private void updateViewport(double minX, double maxX, double minY, double maxY) {
        if (isUsableRange(minX, maxX) && isUsableRange(minY, maxY)) {
            this.currentViewport = new Viewport2D(minX, maxX, minY, maxY, currentViewport.pixelWidth, currentViewport.pixelHeight);
            updateHandler.onViewportUpdated(this.currentViewport);
        }
    }

    private static boolean isUsableRange(double min, double max) {
        double range = max - min;
        double magnitude = Math.max(Math.abs(min), Math.abs(max));
        return Double.isFinite(range) && range > 0.0 && range > magnitude * MIN_RELATIVE_RANGE;
    }

    /** Un intervallo piu' stretto di cosi' (rispetto alle sue coordinate) non e' rappresentabile con i double. */
    private static final double MIN_RELATIVE_RANGE = 1e-12;

    public Viewport2D getCurrentViewport() { return currentViewport; }
}
