package net.gommagomma.smfn.demo.geometry;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.SurfacePlotter;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.geometry.Plane;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Il piano come superficie z = f(x, y), accanto alla versione a mappa altimetrica
 * {@link GeometricPlanePlot}: la stessa altezza, che li' era un colore, qui e' la geometria.
 * <p>
 * Qui la funzione non e' {@code Plane.apply} (la forma implicita: distanza con segno, vuole un
 * punto 3D) ma {@link Plane#explicitForm()}, la z risolta in funzione di (x, y). Esiste solo se il
 * piano non e' verticale (nz != 0).
 */
public class GeometricPlaneSurfacePlot
{
    private static final RealField R = RealField.INSTANCE;
    private static final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

    public static void main(String[] args)
    {
        // --- 1. Definizione del piano: attraverso (2,0,0), (0,2,0), (0,0,2) -> x + y + z = 2 ---
        Plane plane = Plane.through(new Point(2.0, 0.0, 0.0), new Point(0.0, 2.0, 0.0), new Point(0.0, 0.0, 2.0));
        System.out.println("Piano: " + plane);

        // Forma esplicita z = f(x, y): lancia se il piano e' verticale
        Mapping<Vector<Real>, Real> f = plane.explicitForm();

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer renderer = new SwingRenderer(width, height);
        SwingWindow.show(renderer, "SMFN Plane Surface: z = f(x, y)");

        // Su [-4,4]^2 il piano ha z = 2 - x - y, cioe' da -6 a 10
        Viewport3D viewport = new Viewport3D(-4, 4, -4, 4, -6, 10, width, height, 215.0, 30.0);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Vector<Real>> domainAdapter = (x, y) -> V2.of(new Real[] { new Real(x), new Real(y) });
        Function<Real, Double> heightAdapter = Real::getValue;

        // Colore = altezza, come nella mappa altimetrica: blu in basso, rosso in alto
        ColorMapper<Real> colorMapper = z -> {
            double t = Math.max(0.0, Math.min(1.0, (z.getValue() + 6.0) / 16.0));
            return RgbColor.ofHsb((float) (0.66 * (1.0 - t)), 0.9f, 0.9f);
        };

        // --- 4. Rendering ---
        int cells = 30;
        renderer.startDrawing();
        renderer.clear(RgbColor.BLACK);
        SurfacePlotter.plotSurface(renderer, viewport, f, domainAdapter, heightAdapter, colorMapper, cells, cells);
        SurfaceAxisPlotter.plotAxes(renderer, viewport, RgbColor.WHITE, true);
        renderer.endDrawingAndFlush();
    }
}
