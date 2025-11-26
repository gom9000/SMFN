package net.gommagomma.smfn.graphics.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Gestore interattivo per Viewport che calcola nuove Viewport in base all'input utente.
 * Supporta Panning (trascinamento libero) e Zoom Selettivo (rettangolo con Shift/Ctrl).
 */
public class ViewportController
extends MouseAdapter
implements KeyListener // Manteniamo KeyListener solo per conformità, i metodi sono vuoti
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

    // --- Gestione Eventi Mouse ---

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            startX = e.getX();
            startY = e.getY();
            // Controlla lo stato del modificatore (Shift o Ctrl) direttamente dall'evento
            selecting = e.isShiftDown() || e.isControlDown(); 
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selecting) {
            // Modalità Zoom Selettivo (con Shift/Ctrl premuto)
            int endX = e.getX();
            int endY = e.getY();
            int x = Math.min(startX, endX);
            int y = Math.min(startY, endY);
            int w = Math.abs(startX - endX);
            int h = Math.abs(startY - endY);
            updateHandler.onTemporaryDraw(x, y, w, h);

        } else if (e.getButton() == MouseEvent.BUTTON1) {
            // Modalità Panning (trascinamento libero)

            // Calcola lo spostamento in pixel dall'inizio del drag o dall'ultima chiamata a dragged
            int dx = e.getX() - startX;
            int dy = e.getY() - startY;

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
            
            // Reimposta startX/Y per la prossima iterazione di mouseDragged
            startX = e.getX();
            startY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selecting && e.getButton() == MouseEvent.BUTTON1) {
            selecting = false;
            // Ridisegna una volta per cancellare l'ultimo rettangolo temporaneo
            updateHandler.onTemporaryDraw(0, 0, 0, 0); 
            
            // Calcola la nuova viewport dall'area selezionata in coordinate matematiche
            double newMinX = currentViewport.convertPixelXToMathX(Math.min(startX, e.getX()));
            double newMaxX = currentViewport.convertPixelXToMathX(Math.max(startX, e.getX()));
            // Inversione Y
            double newMinY = currentViewport.convertPixelYToMathY(Math.max(startY, e.getY())); 
            double newMaxY = currentViewport.convertPixelYToMathY(Math.min(startY, e.getY()));

            updateViewport(newMinX, newMaxX, newMinY, newMaxY);
        }
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) { 
        double zoomFactor = 1.1; // 10% di zoom per passo della rotellina
        if (e.getWheelRotation() > 0) {
            zoomFactor = 1.0 / zoomFactor; // Zoom out
        }
        
        // Mantieni il centro dello zoom fisso nel punto del cursore
        double mouseMathX = currentViewport.convertPixelXToMathX(e.getX());
        double mouseMathY = currentViewport.convertPixelYToMathY(e.getY());
        
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

    // --- Implementazione KeyListener (Metodi vuoti, usiamo i modificatori del MouseEvent) ---
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {} 
    

    /**
     * Crea e notifica una nuova Viewport valida.
     */
    private void updateViewport(double minX, double maxX, double minY, double maxY) {
        // Aggiungi qui la logica di validazione (es. non permettere range negativi o troppo piccoli)
        if (maxX - minX > 1e-9 && maxY - minY > 1e-9) { 
             this.currentViewport = new Viewport(minX, maxX, minY, maxY, 
                                                currentViewport.pixelWidth, 
                                                currentViewport.pixelHeight);
            updateHandler.onViewportUpdated(this.currentViewport);
        }
    }
    
    public Viewport getCurrentViewport() { return currentViewport; }
}
