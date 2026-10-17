package net.gommagomma.smfn.demo.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Light;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.SurfaceGrid;
import net.gommagomma.smfn.graphics.plotting.SurfacePlotter;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Superficie z = sin(r) / r, con r = sqrt(x^2 + y^2), che si ruota trascinando il mouse (a destra e
 * a sinistra per l'azimut, in alto e in basso per l'elevazione), si sposta col pulsante destro (o
 * Maiusc + trascinamento) e si ingrandisce con la rotellina, attorno al cursore.
 * <p>
 * Il colore segue l'altezza ed e' interpolato fra i vertici, e le facce sono illuminate da una luce
 * fissa sulla scena. Dietro la superficie stanno le pareti del riquadro con la loro griglia, davanti
 * gli assi con le tacche. La demo descrive soltanto la scena: finestra, mouse e ridisegno li gestisce
 * {@link SwingWindow#showInteractive(String, Viewport3D, java.util.function.BiConsumer)}.
 * <p>
 * La funzione si valuta una sola volta, in {@link SurfaceGrid}: ruotare, spostare e ingrandire
 * cambiano soltanto la proiezione.
 */
public class SurfaceInteractivePlot
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(SurfaceInteractivePlot::run);
    }

    private static void run()
    {
        // Modello matematico: z = sin(r) / r (prolungata con 1 nell'origine)
        Mapping<Complex, Real> sinc = point -> {
            double r = Math.hypot(point.getRe(), point.getIm());
            return new Real(r == 0.0 ? 1.0 : Math.sin(r) / r);
        };
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
        Function<Real, Double> heightAdapter = Real::getValue;
        ColorMapper<Real> colorMapper = value -> {
            double t = Math.max(0.0, Math.min(1.0, (value.getValue() + 0.3) / 1.3));   // altezza in [-0.3, 1] -> [0, 1]
            return RgbColor.ofHsb((float) (0.66 - 0.66 * t), 0.8f, 0.95f);              // dal blu al rosso
        };
        Light light = new Light(-40.0, 45.0, 0.25);

        double range = 12.0;
        Viewport3D initial = new Viewport3D(-range, range, -range, range, -0.3, 1.0, 800, 600, 35.0, 40.0);

        // La superficie si campiona una volta sola: il riquadro non cambia ruotando o ingrandendo.
        SurfaceGrid<Real> grid = SurfaceGrid.sample(initial, sinc, domainAdapter, heightAdapter, 80, 80);

        SwingWindow.showInteractive("SMFN Surface: z = sin(r) / r (trascina per ruotare, tasto destro per spostare, rotellina per lo zoom)",
            initial, (renderer, view) -> {
                SurfaceAxisPlotter.plotWalls(renderer, view, RgbColor.DARK_GRAY);
                SurfacePlotter.plotSmoothSurface(renderer, view, grid, colorMapper, light);
                //SurfacePlotter.plotWireframe(renderer, view, grid, RgbColor.CYAN);
                SurfaceAxisPlotter.plotAxes(renderer, view, RgbColor.WHITE, true);
            });
    }
}
