package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.math.geometry.Point;

/** Test per {@link ScatterPlotter}. */
@DisplayName("ScatterPlotter: un pixel per punto, punti non finiti saltati")
class ScatterPlotterTest
{
	private static final Viewport2D VIEW = new Viewport2D(0, 10, 0, 10, 100, 100);

	@Test
	@DisplayName("Ogni punto e' un pixel, del colore dato, nella posizione della viewport")
	void pointsAndColor() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		ScatterPlotter.plotPoints(r, VIEW, List.of(new Point(0.0, 0.0), new Point(5.0, 5.0), new Point(10.0, 10.0)), RgbColor.RED);
		assertEquals(3, r.points.size());
		assertEquals(VIEW.convertMathXToPixelX(5), r.points.get(1)[0]);
		assertEquals(VIEW.convertMathYToPixelY(5), r.points.get(1)[1]);
		for (RgbColor c : r.pointColors) assertEquals(RgbColor.RED, c);
	}

	@Test
	@DisplayName("Un punto con una coordinata non finita non viene disegnato")
	void nonFinitePointsSkipped() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		ScatterPlotter.plotPoints(r, VIEW, List.of(new Point(Double.NaN, 1.0), new Point(1.0, Double.POSITIVE_INFINITY), new Point(2.0, 2.0)), RgbColor.RED);
		assertEquals(1, r.points.size());
	}

	@Test
	@DisplayName("Nessun punto: nessun disegno")
	void empty() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		ScatterPlotter.plotPoints(r, VIEW, List.of(), RgbColor.RED);
		assertEquals(0, r.points.size());
	}
}
