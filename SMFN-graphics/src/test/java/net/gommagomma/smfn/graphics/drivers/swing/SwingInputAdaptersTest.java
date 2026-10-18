package net.gommagomma.smfn.graphics.drivers.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.graphics.core.Viewport2DController;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.core.Viewport3DController;

/**
 * Test per gli adapter AWT: eventi del mouse sintetici (si possono costruire senza schermo) e
 * controller reali, di cui si controlla l'effetto sulla viewport.
 */
@DisplayName("Adapter Swing: dagli eventi del mouse ai controller")
class SwingInputAdaptersTest
{
	private static final Component SOURCE = new Canvas();

	private static MouseEvent mouse(int id, int x, int y, int button, int modifiersEx) {
		return new MouseEvent(SOURCE, id, 0L, modifiersEx, x, y, 0, false, button);
	}

	private static MouseWheelEvent wheel(int x, int y, int rotation) {
		return new MouseWheelEvent(SOURCE, MouseEvent.MOUSE_WHEEL, 0L, 0, x, y, 0, false,
			MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, rotation);
	}

	// ---- 3D ----

	private final List<Viewport3D> updates3d = new ArrayList<>();

	private Viewport3DController controller3d() {
		updates3d.clear();
		return new Viewport3DController(new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, 0), updates3d::add);
	}

	@Test
	@DisplayName("3D: trascinare con il pulsante primario ruota la scena")
	void rotateWithPrimaryButton() {
		Viewport3DController c = controller3d();
		SwingViewport3DInputAdapter a = new SwingViewport3DInputAdapter(c);
		a.mousePressed(mouse(MouseEvent.MOUSE_PRESSED, 10, 10, MouseEvent.BUTTON1, InputEvent.BUTTON1_DOWN_MASK));
		a.mouseDragged(mouse(MouseEvent.MOUSE_DRAGGED, 30, 10, MouseEvent.NOBUTTON, InputEvent.BUTTON1_DOWN_MASK));
		a.mouseReleased(mouse(MouseEvent.MOUSE_RELEASED, 30, 10, MouseEvent.BUTTON1, 0));
		assertEquals(10.0, c.getCurrentViewport().azimuth, 1e-9);
		assertEquals(0.0, c.getCurrentViewport().offsetX, 0.0);

		// dopo il rilascio i movimenti non ruotano piu'
		a.mouseDragged(mouse(MouseEvent.MOUSE_DRAGGED, 90, 10, MouseEvent.NOBUTTON, InputEvent.BUTTON1_DOWN_MASK));
		assertEquals(10.0, c.getCurrentViewport().azimuth, 1e-9);
	}

	@Test
	@DisplayName("3D: trascinare con il pulsante secondario, o con il primario e Maiusc, sposta la scena")
	void panWithSecondaryOrShift() {
		Viewport3DController c = controller3d();
		SwingViewport3DInputAdapter a = new SwingViewport3DInputAdapter(c);
		a.mousePressed(mouse(MouseEvent.MOUSE_PRESSED, 10, 10, MouseEvent.BUTTON3, InputEvent.BUTTON3_DOWN_MASK));
		a.mouseDragged(mouse(MouseEvent.MOUSE_DRAGGED, 25, 5, MouseEvent.NOBUTTON, InputEvent.BUTTON3_DOWN_MASK));
		a.mouseReleased(mouse(MouseEvent.MOUSE_RELEASED, 25, 5, MouseEvent.BUTTON3, 0));
		assertEquals(15.0, c.getCurrentViewport().offsetX, 1e-9);
		assertEquals(-5.0, c.getCurrentViewport().offsetY, 1e-9);
		assertEquals(0.0, c.getCurrentViewport().azimuth, 0.0);

		a.mousePressed(mouse(MouseEvent.MOUSE_PRESSED, 0, 0, MouseEvent.BUTTON1, InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		a.mouseDragged(mouse(MouseEvent.MOUSE_DRAGGED, 4, 4, MouseEvent.NOBUTTON, InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		a.mouseReleased(mouse(MouseEvent.MOUSE_RELEASED, 4, 4, MouseEvent.BUTTON1, InputEvent.SHIFT_DOWN_MASK));
		assertEquals(19.0, c.getCurrentViewport().offsetX, 1e-9);
		assertEquals(0.0, c.getCurrentViewport().azimuth, 0.0);
	}

	@Test
	@DisplayName("3D: la rotellina zooma sul cursore: indietro (rotazione positiva) allontana, avanti avvicina")
	void wheelZooms() {
		Viewport3DController c = controller3d();
		SwingViewport3DInputAdapter a = new SwingViewport3DInputAdapter(c);
		a.mouseWheelMoved(wheel(100, 100, -1));
		assertEquals(Viewport3DController.ZOOM_FACTOR, c.getCurrentViewport().zoom, 1e-9);
		a.mouseWheelMoved(wheel(100, 100, 1));
		assertEquals(1.0, c.getCurrentViewport().zoom, 1e-9);
	}

	// ---- 2D ----

	private final List<Viewport2D> updates2d = new ArrayList<>();

	private Viewport2DController controller2d() {
		updates2d.clear();
		return new Viewport2DController(new Viewport2D(0, 10, 0, 10, 100, 100), new Viewport2DController.ViewportUpdateHandler() {
			@Override public void onViewportUpdated(Viewport2D v) { updates2d.add(v); }
			@Override public void onTemporaryDraw(int x, int y, int w, int h) {}
		});
	}

	@Test
	@DisplayName("2D: trascinare con il pulsante primario fa il panning; con Maiusc seleziona un rettangolo da ingrandire")
	void panAndSelect() {
		Viewport2DController c = controller2d();
		SwingViewport2DInputAdapter a = new SwingViewport2DInputAdapter(c);
		a.mousePressed(mouse(MouseEvent.MOUSE_PRESSED, 50, 50, MouseEvent.BUTTON1, InputEvent.BUTTON1_DOWN_MASK));
		a.mouseDragged(mouse(MouseEvent.MOUSE_DRAGGED, 60, 50, MouseEvent.NOBUTTON, InputEvent.BUTTON1_DOWN_MASK));
		a.mouseReleased(mouse(MouseEvent.MOUSE_RELEASED, 60, 50, MouseEvent.BUTTON1, 0));
		assertEquals(-1.0, c.getCurrentViewport().minX, 1e-9);

		a.mousePressed(mouse(MouseEvent.MOUSE_PRESSED, 10, 10, MouseEvent.BUTTON1, InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		a.mouseDragged(mouse(MouseEvent.MOUSE_DRAGGED, 30, 30, MouseEvent.NOBUTTON, InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		a.mouseReleased(mouse(MouseEvent.MOUSE_RELEASED, 30, 30, MouseEvent.BUTTON1, InputEvent.SHIFT_DOWN_MASK));
		assertEquals(2.0, c.getCurrentViewport().rangeX, 1e-9);
	}

	@Test
	@DisplayName("2D: la rotellina ingrandisce o riduce; gli altri pulsanti non fanno nulla")
	void wheelAndOtherButtons() {
		Viewport2DController c = controller2d();
		SwingViewport2DInputAdapter a = new SwingViewport2DInputAdapter(c);
		a.mouseWheelMoved(wheel(50, 50, -1));
		assertEquals(10.0 / 1.1, c.getCurrentViewport().rangeX, 1e-9);
		int before = updates2d.size();
		a.mousePressed(mouse(MouseEvent.MOUSE_PRESSED, 50, 50, MouseEvent.BUTTON3, InputEvent.BUTTON3_DOWN_MASK));
		a.mouseDragged(mouse(MouseEvent.MOUSE_DRAGGED, 70, 50, MouseEvent.NOBUTTON, InputEvent.BUTTON3_DOWN_MASK));
		assertEquals(before, updates2d.size());
	}
}
