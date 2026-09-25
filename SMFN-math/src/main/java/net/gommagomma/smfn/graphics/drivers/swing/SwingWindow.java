package net.gommagomma.smfn.graphics.drivers.swing;

import javax.swing.JFrame;

/**
 * Mostra un renderer Swing in una finestra visibile.
 */
public final class SwingWindow
{
	private SwingWindow() {}

	public static <T extends SwingRendererBase> T show(T renderer, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(renderer);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		renderer.initBufferStrategy();
		return renderer;
	}
}
