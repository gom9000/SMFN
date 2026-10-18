package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import net.gommagomma.smfn.graphics.core.Viewport3DController;

/**
 * Adapter AWT/Swing per {@link Viewport3DController}: traduce gli eventi del mouse nelle chiamate
 * del controller.
 */
public class SwingViewport3DInputAdapter
extends MouseAdapter
{
    private final Viewport3DController controller;

    public SwingViewport3DInputAdapter(Viewport3DController controller) {
        this.controller = controller;
    }

    // Si ruota con il pulsante primario e si sposta con il secondario (o con il primario e Maiusc,
    // per chi non ha un secondo pulsante). Il modo si decide alla pressione e dura fino al rilascio.
    private boolean panning = false;

    @Override
    public void mousePressed(MouseEvent e) {
        if (isPanButton(e)) {
            panning = true;
            controller.onPanStart(e.getX(), e.getY());
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            panning = false;
            controller.onDragStart(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Durante mouseDragged getButton() restituisce sempre NOBUTTON per specifica AWT: il
        // pulsante premuto si legge dai modificatori.
        if (panning) {
            boolean held = (e.getModifiersEx() & (MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK)) != 0;
            controller.onPan(e.getX(), e.getY(), held);
        } else {
            boolean primaryButtonHeld = (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
            controller.onDrag(e.getX(), e.getY(), primaryButtonHeld);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (panning) {
            panning = false;
            controller.onPanEnd();
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            controller.onDragEnd();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        controller.onWheelZoom(e.getX(), e.getY(), e.getWheelRotation() > 0);
    }

    private static boolean isPanButton(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON3
            || (e.getButton() == MouseEvent.BUTTON1 && e.isShiftDown());
    }
}
