package net.gommagomma.smfn.graphics.plotting;

import java.util.List;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.math.geometry.Point;


/**
 * Utility per plottare curve e punti nello spazio (x, y, z) con la proiezione di una
 * {@link Viewport3D}: una traiettoria, una curva parametrica, un insieme di punti come i vettori
 * di una decomposizione.
 * <p>
 * A differenza delle superfici, curve e punti non hanno facce che ne coprano altre: sono disegnati
 * sopra tutto cio' che e' gia' sul renderer, anche quando in realta' starebbero dietro a una
 * superficie. Per questo conviene disegnarli dopo la superficie (o da soli), e insieme a pareti e
 * assi di {@link SurfaceAxisPlotter} che ne danno il riferimento.
 * <p>
 * Un punto non finito (NaN o infinito) non ha una posizione sullo schermo: viene saltato, e una
 * curva si interrompe li'. Le coordinate fuori dal riquadro della viewport vengono tracciate
 * comunque e possono uscire dalla superficie di disegno, dove il renderer le ritaglia.
 */
public final class SpacePlotter
{
    /** Lato in pixel del quadratino con cui si disegna un punto. */
    private static final int POINT_SIZE = 3;

    private SpacePlotter() {}


    /**
     * Disegna la curva {@code t -> (x, y, z)} come spezzata che unisce {@code samples} punti
     * equidistanti in t da {@code tMin} a {@code tMax} (estremi compresi).
     *
     * @param renderer Il renderer da usare.
     * @param viewport La viewport 3D che definisce riquadro e punto di vista.
     * @param curve La curva: dal parametro restituisce il punto dello spazio (a tre coordinate).
     * @param tMin Il valore iniziale del parametro.
     * @param tMax Il valore finale del parametro (maggiore di {@code tMin}).
     * @param samples Quanti punti campionare (almeno 2): piu' sono, piu' la curva e' liscia.
     * @param color Il colore della curva.
     * @throws IllegalArgumentException se {@code samples} e' minore di 2, se {@code tMin < tMax}
     *         non vale o non sono finiti, o se {@code curve} restituisce un punto che non ha tre coordinate
     */
    public static void plotCurve(Renderer renderer, Viewport3D viewport, Function<Double, Point> curve,
                                 double tMin, double tMax, int samples, RgbColor color)
    {
        if (samples < 2) {
            throw new IllegalArgumentException("Campioni non validi: " + samples + " (richiesti almeno 2).");
        }
        if (!Double.isFinite(tMin) || !Double.isFinite(tMax) || !(tMax > tMin)) {
            throw new IllegalArgumentException(
                "Intervallo del parametro non valido: tMin=" + tMin + ", tMax=" + tMax + " (richiesto tMin < tMax, entrambi finiti).");
        }

        renderer.setColor(color);

        boolean hasPrevious = false;
        int previousX = 0;
        int previousY = 0;
        for (int k = 0; k < samples; k++)
        {
            double t = k == samples - 1 ? tMax : tMin + (tMax - tMin) * k / (samples - 1);
            double[] p = coordinates(curve.apply(t));

            if (!isFinite(p)) {
                hasPrevious = false;
                continue;
            }

            int pixelX = (int) Math.round(viewport.projectToPixelX(p[0], p[1], p[2]));
            int pixelY = (int) Math.round(viewport.projectToPixelY(p[0], p[1], p[2]));
            if (hasPrevious) {
                renderer.drawLine(previousX, previousY, pixelX, pixelY);
            }
            previousX = pixelX;
            previousY = pixelY;
            hasPrevious = true;
        }
    }

    /**
     * Disegna un insieme di punti dello spazio, ciascuno come un piccolo quadrato.
     *
     * @param points I punti dello spazio (a tre coordinate).
     * @param color Il colore dei punti.
     * @throws IllegalArgumentException se un punto non ha tre coordinate
     */
    public static void plotPoints(Renderer renderer, Viewport3D viewport, List<Point> points, RgbColor color)
    {
        renderer.setColor(color);

        for (Point point : points)
        {
            double[] p = coordinates(point);
            if (!isFinite(p)) continue;

            int pixelX = (int) Math.round(viewport.projectToPixelX(p[0], p[1], p[2]));
            int pixelY = (int) Math.round(viewport.projectToPixelY(p[0], p[1], p[2]));
            renderer.fillRect(pixelX - POINT_SIZE / 2, pixelY - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
        }
    }

    private static double[] coordinates(Point point)
    {
        if (point == null || point.dimension() != 3) {
            throw new IllegalArgumentException(
                "Punto non valido: servono tre coordinate, ricevuto " + (point == null ? "null" : point.dimension() + " coordinate") + ".");
        }
        return new double[] { point.getX().getValue(), point.getY().getValue(), point.getZ().getValue() };
    }

    private static boolean isFinite(double[] p)
    {
        return Double.isFinite(p[0]) && Double.isFinite(p[1]) && Double.isFinite(p[2]);
    }
}
