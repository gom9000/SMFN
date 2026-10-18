package net.gommagomma.smfn.graphics.plotting;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;

/**
 * Renderer di prova che registra le primitive invece di disegnare: segmenti, punti, testi, poligoni
 * (ciascuno con il colore corrente al momento della chiamata).
 */
final class RecordingRenderer implements Renderer
{
	final int width;
	final int height;
	final List<int[]> lines = new ArrayList<>();
	final List<int[]> points = new ArrayList<>();
	final List<RgbColor> pointColors = new ArrayList<>();
	final List<String> texts = new ArrayList<>();
	private RgbColor color = RgbColor.BLACK;

	RecordingRenderer(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override public void startDrawing() {}
	@Override public void endDrawingAndFlush() {}
	@Override public int getWidth() { return width; }
	@Override public int getHeight() { return height; }
	@Override public void clear(RgbColor c) {}
	@Override public void setColor(RgbColor c) { color = c; }
	@Override public void drawLine(int x1, int y1, int x2, int y2) { lines.add(new int[] { x1, y1, x2, y2 }); }
	@Override public void drawPoint(int x, int y) { points.add(new int[] { x, y }); pointColors.add(color); }
	@Override public void drawText(String text, int x, int y) { texts.add(text); }
	@Override public void drawOverlayText(String text, int x, int y, RgbColor c) {}
	@Override public void fillPolygon(int[] xs, int[] ys) {}
}
