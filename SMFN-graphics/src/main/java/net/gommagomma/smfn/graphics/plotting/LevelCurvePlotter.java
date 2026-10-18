package net.gommagomma.smfn.graphics.plotting;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;


/**
 * Utility per plottare le curve di livello di una funzione f(x, y): per ciascun livello, l'insieme
 * dei punti in cui f vale quel livello. E' una rappresentazione alternativa del campo colorato di
 * {@link ColorFieldPlotter}: invece di dare a ogni pixel un colore, disegna sottili linee che
 * seguono i valori scelti, e si legge come una mappa topografica.
 */
public class LevelCurvePlotter
{
    private LevelCurvePlotter() {}


    /**
     * Livelli equidistanti strettamente interni all'intervallo (min, max): {@code count} valori che
     * lo dividono in {@code count + 1} parti uguali. Gli estremi sono esclusi perche' sul minimo e
     * sul massimo la curva si ridurrebbe a un punto o a un'area.
     *
     * @throws IllegalArgumentException se {@code count} e' minore di 1 o se l'intervallo non e'
     *         finito con {@code min < max}
     */
    public static double[] evenLevels(double min, double max, int count)
    {
        if (count < 1) {
            throw new IllegalArgumentException("Numero di livelli non valido: " + count + " (richiesto almeno 1).");
        }
        if (!Double.isFinite(min) || !Double.isFinite(max) || !(max > min)) {
            throw new IllegalArgumentException("Intervallo non valido: min=" + min + ", max=" + max + " (richiesto min < max, entrambi finiti).");
        }
        double[] levels = new double[count];
        for (int k = 0; k < count; k++) {
            levels[k] = min + (max - min) * (k + 1) / (count + 1);
        }
        return levels;
    }


    /**
     * Disegna le curve di livello di {@code scalarAdapter(function(x, y))} sul riquadro della
     * viewport, tutte dello stesso colore. I livelli sono {@code levelCount} valori equidistanti fra
     * il minimo e il massimo che la funzione assume nei punti campionati (estremi esclusi).
     *
     * @param <D> Il tipo di input (dominio), vincolato a AlgebraicElement.
     * @param <C> Il tipo di output (codominio), vincolato a AlgebraicElement.
     * @param renderer Il renderer da usare.
     * @param viewport La viewport 2D che definisce il riquadro.
     * @param function La funzione matematica f(D) = C.
     * @param domainAdapter Un adattatore che combina X e Y matematici in un elemento D.
     * @param scalarAdapter Un adattatore che estrae da C il valore di cui si cercano i livelli.
     * @param color Il colore delle curve.
     * @param levelCount Quante curve (almeno 1).
     * @param cellsX Numero di celle lungo x (almeno 1).
     * @param cellsY Numero di celle lungo y (almeno 1).
     * @throws IllegalArgumentException se {@code levelCount}, {@code cellsX} o {@code cellsY} sono minori di 1
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotLevelCurves(Renderer renderer, Viewport2D viewport, Mapping<D, C> function,
                         BiFunction<Double, Double, D> domainAdapter, Function<C, Double> scalarAdapter,
                         RgbColor color, int levelCount, int cellsX, int cellsY)
    {
        if (levelCount < 1) {
            throw new IllegalArgumentException("Numero di livelli non valido: " + levelCount + " (richiesto almeno 1).");
        }
        draw(renderer, viewport, function, domainAdapter, scalarAdapter, null, levelCount, level -> color, cellsX, cellsY);
    }

    /**
     * Come {@link #plotLevelCurves(Renderer, Viewport2D, Mapping, BiFunction, Function, RgbColor, int, int, int)},
     * ma con i livelli scelti da chi chiama (vedi {@link #evenLevels}) e un colore per ciascuno:
     * ad esempio una tinta che cambia con il livello.
     *
     * @param levels I valori per cui disegnare la curva.
     * @param levelColor Il colore della curva di ciascun livello.
     * @throws IllegalArgumentException se le celle sono meno di 1, o se i livelli o il colore sono nulli
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotLevelCurves(Renderer renderer, Viewport2D viewport, Mapping<D, C> function,
                         BiFunction<Double, Double, D> domainAdapter, Function<C, Double> scalarAdapter,
                         double[] levels, Function<Double, RgbColor> levelColor, int cellsX, int cellsY)
    {
        if (levels == null || levelColor == null) {
            throw new IllegalArgumentException("I livelli e il colore non possono essere nulli.");
        }
        draw(renderer, viewport, function, domainAdapter, scalarAdapter, levels, 0, levelColor, cellsX, cellsY);
    }

    /**
     * I livelli equidistanti fra il minimo e il massimo dei valori finiti, o nessuno se i valori
     * sono tutti uguali o non c'e' alcun valore finito (in quel caso non c'e' curva da disegnare).
     */
    static double[] levelsInRangeOf(double[][] values, int count)
    {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double[] column : values) {
            for (double v : column) {
                if (Double.isFinite(v)) {
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
            }
        }
        return min < max ? evenLevels(min, max, count) : new double[0];
    }

    private static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void draw(Renderer renderer, Viewport2D viewport, Mapping<D, C> function,
              BiFunction<Double, Double, D> domainAdapter, Function<C, Double> scalarAdapter,
              double[] levels, int levelCount, Function<Double, RgbColor> levelColor, int cellsX, int cellsY)
    {
        if (cellsX < 1 || cellsY < 1) {
            throw new IllegalArgumentException(
                "Risoluzione non valida: cellsX=" + cellsX + ", cellsY=" + cellsY + " (richiesti entrambi >= 1).");
        }

        double[] xs = new double[cellsX + 1];
        double[] ys = new double[cellsY + 1];
        for (int i = 0; i <= cellsX; i++) xs[i] = i == cellsX ? viewport.maxX : viewport.minX + viewport.rangeX * i / cellsX;
        for (int j = 0; j <= cellsY; j++) ys[j] = j == cellsY ? viewport.maxY : viewport.minY + viewport.rangeY * j / cellsY;

        double[][] values = new double[cellsX + 1][cellsY + 1];
        for (int i = 0; i <= cellsX; i++) {
            for (int j = 0; j <= cellsY; j++) {
                values[i][j] = scalarAdapter.apply(function.apply(domainAdapter.apply(xs[i], ys[j])));
            }
        }

        double[] chosen = levels != null ? levels : levelsInRangeOf(values, levelCount);
        for (double level : chosen)
        {
            renderer.setColor(levelColor.apply(level));
            for (int i = 0; i < cellsX; i++)
            {
                for (int j = 0; j < cellsY; j++)
                {
                    final double x0 = xs[i], y0 = ys[j], width = xs[i + 1] - xs[i], height = ys[j + 1] - ys[j];
                    MarchingSquares.segments(values[i][j], values[i + 1][j], values[i + 1][j + 1], values[i][j + 1], level,
                        (u1, v1, u2, v2) -> renderer.drawLine(
                            viewport.convertMathXToPixelX(x0 + u1 * width), viewport.convertMathYToPixelY(y0 + v1 * height),
                            viewport.convertMathXToPixelX(x0 + u2 * width), viewport.convertMathYToPixelY(y0 + v2 * height)));
                }
            }
        }
    }
}
