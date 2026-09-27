package net.gommagomma.smfn.graphics.core;

/**
 * Gestore interattivo per Viewport che calcola nuove Viewport in base all'input utente.
 * Supporta Panning (trascinamento libero) e Zoom Selettivo (rettangolo con Shift/Ctrl), e Zoom
 * con la rotellina.
 * <p>
 * Espone un'API di eventi puramente semantica (coordinate pixel e flag booleani), senza alcuna
 * dipendenza da un toolkit di input concreto (AWT/Swing, JavaFX, ...). Il compito di tradurre gli
 * eventi reali del toolkit (es. {@code java.awt.event.MouseEvent}) in chiamate a questi metodi
 * spetta a un adapter nel driver concreto (es. {@code SwingViewportInputAdapter} in
 * {@code graphics.drivers.swing}).
 */
public class ViewportController
{
    private Viewport currentViewport;
    private final ViewportUpdateHandler updateHandler;

    private int startX, startY; // Usato per tracciare l'inizio del drag o della selezione
    private boolean selecting = false; // Flag che indica se stiamo disegnando un rettangolo di selezione

    /**
     * Interfaccia di callback per notificare l'host (es. un pannello grafico)
     * che la viewport è cambiata o che deve disegnare un rettangolo temporaneo.
     */
    public interface ViewportUpdateHandler {
        void onViewportUpdated(Viewport newViewport);
        // Notifica per il disegno temporaneo del rettangolo di selezione
        void onTemporaryDraw(int x, int y, int width, int height);
    }

    public ViewportController(Viewport initialViewport, ViewportUpdateHandler handler) {
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

            // Calcola quanto "vale" un pixel nel mondo matematico
            double mathUnitsPerPixelX = currentViewport.rangeX / currentViewport.pixelWidth;
            double mathUnitsPerPixelY = currentViewport.rangeY / currentViewport.pixelHeight;

            // Calcola lo spostamento matematico effettivo
            double mathDx = dx * mathUnitsPerPixelX;
            double mathDy = dy * mathUnitsPerPixelY;

            // Applica il panning
            double newMinX = currentViewport.minX - mathDx;
            double newMaxX = currentViewport.maxX - mathDx;
            // L'asse Y nei pixel è invertito rispetto al mondo matematico, quindi invertiamo qui l'effetto
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
     * Crea e notifica una nuova Viewport valida.
     */
    private void updateViewport(double minX, double maxX, double minY, double maxY) {
        if (maxX - minX > 1e-9 && maxY - minY > 1e-9) {
             this.currentViewport = new Viewport(minX, maxX, minY, maxY, currentViewport.pixelWidth, currentViewport.pixelHeight);
            updateHandler.onViewportUpdated(this.currentViewport);
        }
    }

    public Viewport getCurrentViewport() { return currentViewport; }
}
