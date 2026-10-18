package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import net.gommagomma.smfn.graphics.core.Viewport2DController;

/**
 * Adapter AWT/Swing per {@link Viewport2DController}: traduce gli eventi del mouse nelle chiamate
 * del controller (pulsante primario: panning, con Maiusc o Ctrl zoom su rettangolo;
 * rotellina: zoom sul cursore).
 */
public class SwingViewport2DInputAdapter
extends MouseAdapter
implements KeyListener
{
    private final Viewport2DController controller;

    public SwingViewport2DInputAdapter(Viewport2DController controller) {
        this.controller = controller;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            boolean multiSelectModifier = e.isShiftDown() || e.isControlDown();
            controller.onDragStart(e.getX(), e.getY(), multiSelectModifier);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        boolean primaryButtonHeld = (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
        controller.onDrag(e.getX(), e.getY(), primaryButtonHeld);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            controller.onDragEnd(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        boolean zoomOut = e.getWheelRotation() > 0;
        controller.onWheelZoom(e.getX(), e.getY(), zoomOut);
    }

    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
