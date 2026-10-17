package net.gommagomma.smfn.demo.geometry;

import net.gommagomma.smfn.graphics.core.RgbColor;
import java.util.function.BiFunction;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.ColorFieldPlotter;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.geometry.Plane;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * "Plot" di un piano 3D come mappa altimetrica: il grafico non ha rendering
 * 3D vero (nessuna mesh, nessuna rotazione) -- FunctionPlotter2D resta un
 * dominio 2D -> colore. Qui il colore rappresenta z, risolto dall'equazione
 * del piano in funzione di (x,y): la stessa tecnica di una mappa topografica.
 * La funzione e' {@link Plane#explicitForm()}, che esiste solo se il piano non e' verticale
 * (nz != 0): un piano verticale non si puo' esprimere come z = f(x,y).
 */
public class GeometricPlanePlot
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

	public static void main(String[] args) {
		// Piano attraverso (2,0,0), (0,2,0), (0,0,2) -> x+y+z=2
		Plane plane = Plane.through(new Point(2.0, 0.0, 0.0), new Point(0.0, 2.0, 0.0), new Point(0.0, 0.0, 2.0));
		System.out.println("Piano: " + plane);

		int width = 700;
		int height = 700;

		SwingRenderer renderer = new SwingRenderer(width, height);
		SwingWindow.show(renderer, "SMFN Plane Plot (mappa altimetrica z = f(x,y))");

		Viewport2D viewport = new Viewport2D(-4.0, 4.0, -4.0, 4.0, width, height);

		BiFunction<Double, Double, Vector<Real>> domainAdapter = (x, y) -> V2.of(new Real[] { new Real(x), new Real(y) });

		// Forma esplicita z = f(x,y) del piano: lancia se il piano e' verticale
		Mapping<Vector<Real>, Real> heightFunction = plane.explicitForm();

		// Colore = altezza z, come una mappa topografica: blu in basso, rosso in alto
		ColorMapper<Real> heightColorMapper = new ColorMapper<>() {
			@Override
			public RgbColor map(Real z) {
				double v = z.getValue();
				double normalized = Math.max(0.0, Math.min(1.0, (v + 5.0) / 10.0)); // z in [-5,5] -> [0,1]
				float hue = (float) (0.66 * (1.0 - normalized)); // 0.66=blu, 0=rosso
				return RgbColor.ofHsb(hue, 0.9f, 0.9f);
			}
		};

		renderer.startDrawing();
		ColorFieldPlotter.plotFunction(renderer, viewport, heightFunction, domainAdapter, heightColorMapper);
		CartesianAxisPlotter.plotAxes(renderer, viewport, RgbColor.DARK_GRAY, true);
		renderer.endDrawingAndFlush();
	}
}
