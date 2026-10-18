package net.gommagomma.smfn.graphics.drivers.swing;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.graphics.core.Viewport3D;

/**
 * La finestra vera non si puo' aprire senza schermo: qui si verifica solo che gli argomenti
 * sbagliati siano respinti prima di creare qualsiasi cosa (quindi anche in ambiente headless).
 */
@DisplayName("SwingWindow.showInteractive: argomenti nulli respinti")
class SwingWindowTest
{
	private final Viewport3D view3d = new Viewport3D(-1, 1, -1, 1, -1, 1, 100, 100, 0, 0);
	private final Viewport2D view2d = new Viewport2D(-1, 1, -1, 1, 100, 100);

	@Test
	@DisplayName("3D: titolo, viewport, scena o sfondo nulli -> IllegalArgumentException")
	void nullArguments3d() {
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive(null, view3d, (r, v) -> {}));
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive("t", (Viewport3D) null, (r, v) -> {}));
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive("t", view3d, null));
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive("t", view3d, (r, v) -> {}, null, true));
	}

	@Test
	@DisplayName("2D: titolo, viewport, scena o sfondo nulli -> IllegalArgumentException")
	void nullArguments2d() {
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive(null, view2d, (r, v) -> {}));
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive("t", (Viewport2D) null, (r, v) -> {}));
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive("t", view2d, null));
		assertThrows(IllegalArgumentException.class, () -> SwingWindow.showInteractive("t", view2d, (r, v) -> {}, null, false));
	}
}
