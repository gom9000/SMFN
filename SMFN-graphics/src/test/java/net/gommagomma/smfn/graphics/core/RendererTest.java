package net.gommagomma.smfn.graphics.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test per il comportamento di default di {@link Renderer}: fillRect come caso particolare di fillPolygon. */
@DisplayName("Renderer: fillRect di default passa da fillPolygon")
class RendererTest
{
	private static final class OnlyPolygon implements Renderer
	{
		final List<int[][]> polygons = new ArrayList<>();
		@Override public void startDrawing() {}
		@Override public void endDrawingAndFlush() {}
		@Override public int getWidth() { return 10; }
		@Override public int getHeight() { return 10; }
		@Override public void clear(RgbColor c) {}
		@Override public void setColor(RgbColor c) {}
		@Override public void drawLine(int x1, int y1, int x2, int y2) {}
		@Override public void drawPoint(int x, int y) {}
		@Override public void drawText(String text, int x, int y) {}
		@Override public void drawOverlayText(String text, int x, int y, RgbColor c) {}
		@Override public void fillPolygon(int[] xs, int[] ys) { polygons.add(new int[][] { xs.clone(), ys.clone() }); }
	}

	@Test
	@DisplayName("Un rettangolo e' un poligono a quattro vertici con gli angoli giusti")
	void rectangleIsAPolygon() {
		OnlyPolygon r = new OnlyPolygon();
		r.fillRect(2, 3, 4, 5);
		assertEquals(1, r.polygons.size());
		assertArrayEquals(new int[] { 2, 6, 6, 2 }, r.polygons.get(0)[0]);
		assertArrayEquals(new int[] { 3, 3, 8, 8 }, r.polygons.get(0)[1]);
	}

	@Test
	@DisplayName("Larghezza o altezza non positive: nulla da riempire")
	void emptyRectangles() {
		OnlyPolygon r = new OnlyPolygon();
		r.fillRect(0, 0, 0, 5);
		r.fillRect(0, 0, 5, 0);
		r.fillRect(0, 0, -3, 5);
		assertEquals(0, r.polygons.size());
	}
}
