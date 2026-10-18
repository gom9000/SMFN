package net.gommagomma.smfn.graphics.plotting;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;


/**
 * I campioni di una superficie z = f(x, y) su una griglia regolare del riquadro
 * {@code [minX, maxX] x [minY, maxY]}: la funzione viene valutata una volta sola, e i campioni si
 * possono disegnare quante volte si vuole con {@link SurfacePlotter}, da punti di vista diversi.
 * <p>
 * Serve alla visualizzazione interattiva: ruotando o ingrandendo la scena cambia solo la proiezione,
 * non la superficie, quindi ricampionarla a ogni ridisegno e' lavoro sprecato (e per una funzione
 * costosa, come un frattale, lo spreco si vede). Per un disegno singolo basta passare la funzione
 * al plotter, che campiona da se'.
 * <p>
 * Di ogni vertice si conservano il valore {@code C} intero e l'altezza che ne ha estratto
 * {@code heightAdapter}; il valore al centro di ogni cella (che da' il colore di una faccia piena)
 * si calcola alla prima richiesta, perche' un reticolo o una superficie a colori per vertice non
 * ne hanno bisogno.
 * <p>
 * La griglia e' legata al suo riquadro: va disegnata con una viewport dello stesso riquadro
 * (cambiano punto di vista, zoom e spostamento, non i limiti), altrimenti il plotter rifiuta.
 *
 * @param <C> il tipo di valore della funzione
 */
public final class SurfaceGrid<C extends AlgebraicElement<C>>
{
    public final double minX;
    public final double maxX;
    public final double minY;
    public final double maxY;
    public final int cellsX;
    public final int cellsY;

    private final double[][] height;
    private final boolean[][] finite;
    private final Object[][] vertexValue;
    private final BiFunction<Double, Double, C> valueAt;
    private Object[][] centerValue;


    private <D extends AlgebraicElement<D>>
    SurfaceGrid(Viewport3D box, Mapping<D, C> function, BiFunction<Double, Double, D> domainAdapter,
                Function<C, Double> heightAdapter, int cellsX, int cellsY)
    {
        if (cellsX < 1 || cellsY < 1) {
            throw new IllegalArgumentException(
                "Risoluzione non valida: cellsX=" + cellsX + ", cellsY=" + cellsY + " (richiesti entrambi >= 1).");
        }
        this.minX = box.minX;
        this.maxX = box.maxX;
        this.minY = box.minY;
        this.maxY = box.maxY;
        this.cellsX = cellsX;
        this.cellsY = cellsY;
        this.valueAt = (x, y) -> function.apply(domainAdapter.apply(x, y));

        this.height = new double[cellsX + 1][cellsY + 1];
        this.finite = new boolean[cellsX + 1][cellsY + 1];
        this.vertexValue = new Object[cellsX + 1][cellsY + 1];

        for (int i = 0; i <= cellsX; i++)
        {
            for (int j = 0; j <= cellsY; j++)
            {
                C value = valueAt.apply(x(i), y(j));
                double z = heightAdapter.apply(value);
                vertexValue[i][j] = value;
                if (Double.isFinite(z)) {
                    finite[i][j] = true;
                    height[i][j] = z;
                }
            }
        }
    }

    /**
     * Campiona la funzione sul riquadro x-y della viewport, in {@code cellsX * cellsY} celle.
     *
     * @param viewport la viewport che ne da' il riquadro (punto di vista, zoom e spostamento non contano)
     * @param function la funzione matematica f(D) = C
     * @param domainAdapter combina x e y matematici in un elemento D
     * @param heightAdapter estrae da C l'altezza z
     * @param cellsX celle lungo x (almeno 1)
     * @param cellsY celle lungo y (almeno 1)
     * @throws IllegalArgumentException se le celle sono meno di 1
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    SurfaceGrid<C> sample(Viewport3D viewport, Mapping<D, C> function, BiFunction<Double, Double, D> domainAdapter,
                          Function<C, Double> heightAdapter, int cellsX, int cellsY)
    {
        return new SurfaceGrid<>(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY);
    }


    // L'ultimo vertice cade esattamente su max, senza l'errore di arrotondamento di min + range.
    /** Coordinata x del vertice di indice {@code i} (da 0 a {@code cellsX}). */
    public double x(int i) { return i == cellsX ? maxX : minX + (maxX - minX) * i / cellsX; }

    /** Coordinata y del vertice di indice {@code j} (da 0 a {@code cellsY}). */
    public double y(int j) { return j == cellsY ? maxY : minY + (maxY - minY) * j / cellsY; }

    /** Vero se il vertice (i, j) ha un'altezza finita, e quindi una posizione sullo schermo. */
    public boolean isFinite(int i, int j) { return finite[i][j]; }

    /** L'altezza del vertice (i, j); senza significato se il vertice non e' finito. */
    public double height(int i, int j) { return height[i][j]; }

    /** Il valore della funzione nel vertice (i, j). */
    @SuppressWarnings("unchecked")
    public C vertexValue(int i, int j) { return (C) vertexValue[i][j]; }

    /**
     * Il valore della funzione nel centro della cella (i, j), con i da 0 a {@code cellsX - 1} e j
     * da 0 a {@code cellsY - 1}. Calcolato alla prima richiesta per tutta la griglia.
     */
    @SuppressWarnings("unchecked")
    public C centerValue(int i, int j)
    {
        if (centerValue == null)
        {
            Object[][] computed = new Object[cellsX][cellsY];
            for (int a = 0; a < cellsX; a++) {
                for (int b = 0; b < cellsY; b++) {
                    computed[a][b] = valueAt.apply((x(a) + x(a + 1)) / 2.0, (y(b) + y(b + 1)) / 2.0);
                }
            }
            centerValue = computed;
        }
        return (C) centerValue[i][j];
    }

    /** Vero se tutti e quattro i vertici della cella (i, j) hanno un'altezza finita. */
    public boolean isCellComplete(int i, int j)
    {
        return finite[i][j] && finite[i + 1][j] && finite[i + 1][j + 1] && finite[i][j + 1];
    }

    /**
     * Minimo e massimo delle altezze finite, come {@code {min, max}}; {@code null} se non ce n'e'
     * nessuna.
     */
    public double[] heightRange()
    {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i <= cellsX; i++) {
            for (int j = 0; j <= cellsY; j++) {
                if (finite[i][j]) {
                    min = Math.min(min, height[i][j]);
                    max = Math.max(max, height[i][j]);
                }
            }
        }
        return min <= max ? new double[] { min, max } : null;
    }

    /** Vero se questa griglia ha il riquadro x-y della viewport. */
    boolean matches(Viewport3D viewport)
    {
        return minX == viewport.minX && maxX == viewport.maxX && minY == viewport.minY && maxY == viewport.maxY;
    }
}
