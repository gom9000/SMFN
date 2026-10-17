package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.BiConsumer;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.graphics.core.Viewport2DController;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.core.Viewport3DController;

/**
 * Mostra un renderer Swing in una finestra visibile.
 * <p>
 * {@link #show(SwingRenderer, String)} apre solo la finestra. Le due {@code showInteractive}
 * fanno tutto il resto: aprono la finestra, collegano mouse (e tastiera) al controller del punto
 * di vista e ridisegnano la scena a ogni cambiamento. Chi le usa descrive soltanto <em>cosa</em>
 * disegnare, con una funzione {@code (renderer, viewport) -> ...}; la funzione viene chiamata a
 * ogni ridisegno, quindi deve limitarsi a disegnare (i dati costosi si calcolano prima, una volta).
 * Vanno chiamate dal thread degli eventi ({@code SwingUtilities.invokeLater}).
 */
public final class SwingWindow
{
	private SwingWindow() {}

	public static <T extends SwingRenderer> T show(T renderer, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(renderer);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		renderer.initBufferStrategy();
		return renderer;
	}

	// --- 3D ---

	/**
	 * Finestra interattiva per una scena 3D, con sfondo nero e antialiasing. Si ruota trascinando,
	 * si sposta col pulsante destro (o Maiusc + trascinamento), si ingrandisce con la rotellina.
	 * Sopra la scena compaiono azimut, elevazione e zoom. La finestra ha la dimensione in pixel
	 * della viewport.
	 *
	 * @param title titolo della finestra
	 * @param initial punto di vista iniziale
	 * @param scene disegna la scena sul renderer, vista dalla viewport data
	 */
	public static void showInteractive(String title, Viewport3D initial, BiConsumer<Renderer, Viewport3D> scene) {
		showInteractive(title, initial, scene, RgbColor.BLACK, true);
	}

	/** Come {@link #showInteractive(String, Viewport3D, BiConsumer)}, con sfondo e antialiasing a scelta. */
	public static void showInteractive(String title, Viewport3D initial, BiConsumer<Renderer, Viewport3D> scene,
	                                   RgbColor background, boolean antialiasing) {
		check(title, initial, scene, background);
		SwingRenderer renderer = new SwingRenderer(initial.pixelWidth, initial.pixelHeight);
		renderer.setAntialiasing(antialiasing);
		show(renderer, title);

		BiConsumer<Renderer, Viewport3D> frame = (r, v) -> {
			r.startDrawing();
			r.clear(background);
			scene.accept(r, v);
			r.drawOverlayText(String.format("azimut %.1f   elevazione %.1f   zoom %.2f", v.azimuth, v.elevation, v.zoom),
			                  10, 20, contrast(background));
			r.endDrawingAndFlush();
		};
		Viewport3DController controller = new Viewport3DController(initial, v -> frame.accept(renderer, v));
		SwingViewport3DInputAdapter input = new SwingViewport3DInputAdapter(controller);
		renderer.addMouseListener(input);
		renderer.addMouseMotionListener(input);
		renderer.addMouseWheelListener(input);
		frame.accept(renderer, initial);
	}

	// --- 2D ---

	/**
	 * Finestra interattiva per una scena 2D, con sfondo bianco. Si sposta trascinando, si
	 * ingrandisce con la rotellina (attorno al cursore) o trascinando un rettangolo con Maiusc o
	 * Ctrl. La finestra ha la dimensione in pixel della viewport.
	 *
	 * @param title titolo della finestra
	 * @param initial riquadro iniziale
	 * @param scene disegna la scena sul renderer, vista dalla viewport data
	 */
	public static void showInteractive(String title, Viewport2D initial, BiConsumer<Renderer, Viewport2D> scene) {
		showInteractive(title, initial, scene, RgbColor.WHITE, false);
	}

	/** Come {@link #showInteractive(String, Viewport2D, BiConsumer)}, con sfondo e antialiasing a scelta. */
	public static void showInteractive(String title, Viewport2D initial, BiConsumer<Renderer, Viewport2D> scene,
	                                   RgbColor background, boolean antialiasing) {
		check(title, initial, scene, background);
		SwingRenderer renderer = new SwingRenderer(initial.pixelWidth, initial.pixelHeight);
		renderer.setAntialiasing(antialiasing);
		show(renderer, title);

		Viewport2DController[] holder = new Viewport2DController[1];
		holder[0] = new Viewport2DController(initial, new Viewport2DController.ViewportUpdateHandler() {
			@Override public void onViewportUpdated(Viewport2D v) {
				draw(renderer, background, scene, v, null);
			}
			@Override public void onTemporaryDraw(int x, int y, int width, int height) {
				draw(renderer, background, scene, holder[0].getCurrentViewport(), new int[] {x, y, width, height});
			}
		});
		SwingViewport2DInputAdapter input = new SwingViewport2DInputAdapter(holder[0]);
		renderer.addMouseListener(input);
		renderer.addMouseMotionListener(input);
		renderer.addMouseWheelListener(input);
		renderer.addKeyListener(input);
		renderer.setFocusable(true);
		renderer.requestFocusInWindow();
		renderer.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) { renderer.requestFocusInWindow(); }
		});
		draw(renderer, background, scene, initial, null);
	}

	private static void draw(SwingRenderer renderer, RgbColor background, BiConsumer<Renderer, Viewport2D> scene,
	                         Viewport2D view, int[] selection) {
		renderer.startDrawing();
		renderer.clear(background);
		scene.accept(renderer, view);
		if (selection != null) {
			renderer.setColor(new RgbColor(0, 0, 255, 100));
			renderer.fillRect(selection[0], selection[1], selection[2], selection[3]);
		}
		renderer.endDrawingAndFlush();
	}

	private static void check(String title, Object viewport, Object scene, RgbColor background) {
		if (title == null || viewport == null || scene == null || background == null) {
			throw new IllegalArgumentException("Titolo, viewport, scena e sfondo non possono essere nulli.");
		}
	}

	/** Bianco su sfondo scuro, grigio scuro su sfondo chiaro. */
	private static RgbColor contrast(RgbColor background) {
		int luminance = (background.r * 299 + background.g * 587 + background.b * 114) / 1000;
		return luminance < 128 ? RgbColor.WHITE : RgbColor.DARK_GRAY;
	}
}
