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
implements KeyListener
{
    private Viewport currentViewport;
    private final ViewportUpdateHandler updateHandler;

    private int lastMouseX, lastMouseY;
    private int startX, startY, endX, endY;
    private boolean selecting = false;
    private boolean useSelectionMode = false; // Flag che indica se Shift/Ctrl è premuto

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
            startX = endX = e.getX();
            startY = endY = e.getY();
            selecting = useSelectionMode; // Avvia la selezione solo se il tasto modificatore è premuto
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selecting) {
            // Modalità Zoom Selettivo (con Shift/Ctrl premuto)
            endX = e.getX();
            endY = e.getY();
            int x = Math.min(startX, endX);
            int y = Math.min(startY, endY);
            int w = Math.abs(startX - endX);
            int h = Math.abs(startY - endY);
            updateHandler.onTemporaryDraw(x, y, w, h);

        } else if (e.getButton() == MouseEvent.BUTTON1) {
            // Modalità Panning (trascinamento libero)
            int dx = e.getX() - lastMouseX;
            int dy = e.getY() - lastMouseY;

            double mathDx = currentViewport.convertPixelXToMathX(dx) - currentViewport.convertPixelXToMathX(0);
            double mathDy = currentViewport.convertPixelYToMathY(dy) - currentViewport.convertPixelYToMathY(0);

            double newMinX = currentViewport.minX - mathDx;
            double newMaxX = currentViewport.maxX - mathDx;
            double newMinY = currentViewport.minY + mathDy; // Nota l'inversione Y
            double newMaxY = currentViewport.maxY + mathDy;

            updateViewport(newMinX, newMaxX, newMinY, newMaxY);
        }

        this.lastMouseX = e.getX();
        this.lastMouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selecting && e.getButton() == MouseEvent.BUTTON1) {
            selecting = false;
            // Calcola la nuova viewport dall'area selezionata
            double newMinX = currentViewport.convertPixelXToMathX(Math.min(startX, endX));
            double newMaxX = currentViewport.convertPixelXToMathX(Math.max(startX, endX));
            double newMinY = currentViewport.convertPixelYToMathY(Math.max(startY, endY)); 
            double newMaxY = currentViewport.convertPixelYToMathY(Math.min(startY, endY));

            updateViewport(newMinX, newMaxX, newMinY, newMaxY);
        }
    }

    // --- Gestione Eventi Tastiera (KeyListener) ---

    @Override
    public void keyPressed(KeyEvent e) {
        // Attiva la modalità selezione quando Shift o Ctrl sono premuti
        if (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_CONTROL) {
            useSelectionMode = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Disattiva la modalità selezione quando Shift o Ctrl vengono rilasciati
        if (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_CONTROL) {
            useSelectionMode = false;
            // Ridisegna la scena per cancellare l'ultimo rettangolo temporaneo
            updateHandler.onViewportUpdated(currentViewport); 
        }
    }

    @Override public void mouseWheelMoved(MouseWheelEvent e) { /* ... logica zoom rotellina ... */ }
    @Override public void keyTyped(KeyEvent e) {} // Non usato

    private void updateViewport(double minX, double maxX, double minY, double maxY) {
        this.currentViewport = new Viewport(minX, maxX, minY, maxY, 
                                            currentViewport.pixelWidth, 
                                            currentViewport.pixelHeight);
        updateHandler.onViewportUpdated(this.currentViewport);
    }
    
    public Viewport getCurrentViewport() { return currentViewport; }
}
