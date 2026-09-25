package net.gommagomma.smfn.demo;

import java.awt.Color;
import java.util.function.BiFunction;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.geometry.Plane;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * "Plot" di un piano 3D come mappa altimetrica: il grafico non ha rendering
 * 3D vero (nessuna mesh, nessuna rotazione) -- FunctionPlotter2D resta un
 * dominio 2D -> colore. Qui il colore rappresenta z, risolto dall'equazione
 * del piano in funzione di (x,y): la stessa tecnica di una mappa topografica.
 * Funziona solo se il piano non e' verticale (nz != 0) -- un piano verticale
 * non si puo' esprimere come z = f(x,y).
 */
public class GeometricPlanePlot
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

	public static void main(String[] args) {
		// Piano attraverso (2,0,0), (0,2,0), (0,0,2) -> x+y+z=2
		Plane plane = Plane.through(new Point(2.0, 0.0, 0.0), new Point(0.0, 2.0, 0.0), new Point(0.0, 0.0, 2.0));
		System.out.println("Piano: " + plane);

		Real nz = plane.getNormalZ();
		if (Math.abs(nz.getValue()) < MathConstants.EPSILON) {
			throw new IllegalStateException("Piano verticale (nz=0): non rappresentabile come mappa altimetrica z=f(x,y).");
		}

		int width = 700;
		int height = 700;

		SwingRenderer2D renderer = new SwingRenderer2D(width, height);
		SwingWindow.show(renderer, "SMFN Plane Plot (mappa altimetrica z = f(x,y))");

		Viewport viewport = new Viewport(-4.0, 4.0, -4.0, 4.0, width, height);

		BiFunction<Double, Double, Vector<Real>> domainAdapter = (x, y) -> V2.of(new Real[] { new Real(x), new Real(y) });

		Point origin = plane.getOrigin();
		Real nx = plane.getNormalX();
		Real ny = plane.getNormalY();

		// z = oz - (nx*(x-ox) + ny*(y-oy)) / nz, dall'equazione del piano
		Mapping<Vector<Real>, Real> heightFunction = xy -> {
			Real x = xy.get(0);
			Real y = xy.get(1);
			Real dx = R.subtract(x, origin.getX());
			Real dy = R.subtract(y, origin.getY());
			Real numerator = R.add(R.multiply(nx, dx), R.multiply(ny, dy));
			return R.subtract(origin.getZ(), R.divide(numerator, nz));
		};

		// Colore = altezza z, come una mappa topografica: blu in basso, rosso in alto
		ColorMapper<Real> heightColorMapper = new ColorMapper<>() {
			@Override
			public Color map(Real z) {
				double v = z.getValue();
				double normalized = Math.max(0.0, Math.min(1.0, (v + 5.0) / 10.0)); // z in [-5,5] -> [0,1]
				float hue = (float) (0.66 * (1.0 - normalized)); // 0.66=blu, 0=rosso
				return Color.getHSBColor(hue, 0.9f, 0.9f);
			}
		};

		renderer.startDrawing();
		FunctionPlotter2D.plotFunction(renderer, viewport, heightFunction, domainAdapter, heightColorMapper);
		CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);
		renderer.endDrawingAndFlush();
	}
}
