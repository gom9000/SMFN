package net.gommagomma.smfn.graphics.plotting;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;


/**
 * Utility per plottare superfici z = f(x, y): la funzione viene campionata su una griglia
 * regolare del riquadro {@code [minX, maxX] x [minY, maxY]} della {@link Viewport3D}, il suo
 * risultato diventa l'altezza z, e la griglia viene proiettata sullo schermo.
 * <p>
 * Rispetto agli altri plotter di funzioni la geometria e' quella della superficie (l'output e' una
 * posizione, come in un grafico di curva) e il colore e' un canale in piu', che porta una seconda
 * informazione. L'output {@code C} puo' infatti portare piu' di un numero (ad esempio un
 * {@code Complex}): {@code heightAdapter} ne sceglie uno come altezza e {@code colorMapper}
 * riceve l'intero {@code C}, quindi puo' usare anche il resto (ad esempio l'argomento per la tinta).
 * <p>
 * Un campione non finito (NaN o infinito, ad esempio vicino a un polo) non ha una posizione sullo
 * schermo: le facce o i segmenti che lo toccano non vengono disegnati. I valori di z fuori da
 * {@code [minZ, maxZ]} vengono tracciati comunque e possono uscire dalla superficie di disegno.
 * <p>
 * Il campionamento e' sequenziale: la griglia di una superficie e' piccola (poche migliaia di
 * campioni), a differenza di un campo colorato per pixel.
 */
public class SurfacePlotter
{
    private SurfacePlotter() {}


    /**
     * Disegna la superficie a facce piene, dalla piu' lontana alla piu' vicina (algoritmo del
     * pittore): le facce vicine coprono quelle lontane. L'ordine e' esatto per una superficie a
     * un solo valore z = f(x, y), che e' cio' che questo plotter rappresenta, e si ottiene
     * scorrendo la griglia dal lato lontano, senza ordinamenti.
     * <p>
     * Ogni faccia (una cella della griglia) e' colorata con {@code colorMapper} applicato al valore
     * della funzione nel <b>centro</b> della cella: per questo servono {@code cellsX * cellsY}
     * valutazioni in piu' rispetto ai vertici.
     *
     * @param <D> Il tipo di input (dominio), vincolato a AlgebraicElement.
     * @param <C> Il tipo di output (codominio), vincolato a AlgebraicElement.
     * @param renderer Il renderer da usare.
     * @param viewport La viewport 3D che definisce riquadro e punto di vista.
     * @param function La funzione matematica f(D) = C.
     * @param domainAdapter Un adattatore che combina X e Y matematici in un elemento D.
     * @param heightAdapter Un adattatore che estrae da C l'altezza z (es. Real -> Double).
     * @param colorMapper Un adattatore che mappa il risultato C in un RgbColor.
     * @param cellsX Numero di celle lungo x (almeno 1).
     * @param cellsY Numero di celle lungo y (almeno 1).
     * @throws IllegalArgumentException se {@code cellsX} o {@code cellsY} sono minori di 1
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotSurface(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                     BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                     ColorMapper<C> colorMapper, int cellsX, int cellsY)
    {
        requireCells(cellsX, cellsY);

        Grid grid = new Grid(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY);

        // Facce complete (tutti e quattro i vertici finiti), ordinate dalla piu' lontana.
        int[] order = farthestFirst(grid, viewport);

        for (int cell : order)
        {
            int i = cell / cellsY;
            int j = cell % cellsY;

            double centerX = (grid.x(i) + grid.x(i + 1)) / 2.0;
            double centerY = (grid.y(j) + grid.y(j + 1)) / 2.0;
            C centerValue = function.apply(domainAdapter.apply(centerX, centerY));

            renderer.setColor(colorMapper.map(centerValue));
            renderer.fillPolygon(
                new int[] { grid.pixelX[i][j], grid.pixelX[i + 1][j], grid.pixelX[i + 1][j + 1], grid.pixelX[i][j + 1] },
                new int[] { grid.pixelY[i][j], grid.pixelY[i + 1][j], grid.pixelY[i + 1][j + 1], grid.pixelY[i][j + 1] });
        }
    }

    /**
     * Disegna la superficie come reticolo di segmenti (wireframe) di un solo colore. Il reticolo e'
     * trasparente: non ci sono facce che ne coprano altre, quindi non serve alcun ordinamento.
     *
     * @param color Il colore dei segmenti.
     * @throws IllegalArgumentException se {@code cellsX} o {@code cellsY} sono minori di 1
     * @see #plotSurface per il significato degli altri parametri
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotWireframe(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                       BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                       RgbColor color, int cellsX, int cellsY)
    {
        requireCells(cellsX, cellsY);

        Grid grid = new Grid(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY);

        renderer.setColor(color);

        for (int j = 0; j <= cellsY; j++)
        {
            for (int i = 0; i < cellsX; i++)
            {
                if (grid.finite[i][j] && grid.finite[i + 1][j]) {
                    renderer.drawLine(grid.pixelX[i][j], grid.pixelY[i][j], grid.pixelX[i + 1][j], grid.pixelY[i + 1][j]);
                }
            }
        }
        for (int i = 0; i <= cellsX; i++)
        {
            for (int j = 0; j < cellsY; j++)
            {
                if (grid.finite[i][j] && grid.finite[i][j + 1]) {
                    renderer.drawLine(grid.pixelX[i][j], grid.pixelY[i][j], grid.pixelX[i][j + 1], grid.pixelY[i][j + 1]);
                }
            }
        }
    }


    private static void requireCells(int cellsX, int cellsY)
    {
        if (cellsX < 1 || cellsY < 1) {
            throw new IllegalArgumentException(
                "Risoluzione non valida: cellsX=" + cellsX + ", cellsY=" + cellsY + " (richiesti entrambi >= 1).");
        }
    }

    /**
     * Indici delle celle complete, nell'ordine in cui vanno disegnate (dalla parte piu' lontana).
     * L'indice di una cella (i, j) e' {@code i * cellsY + j}.
     * <p>
     * Per una superficie a un solo valore il raggio di vista che attraversa due punti, uno davanti
     * all'altro, si sposta sempre nello stesso verso lungo x e nello stesso verso lungo y: la cella
     * piu' lontana ha quindi indici maggiori o uguali (o minori o uguali, a seconda del punto di
     * vista) in entrambe le direzioni. Scorrere righe e colonne partendo dal lato lontano rispetta
     * percio' ogni sovrapposizione sullo schermo, ed e' esatto: non serve ordinare per distanza,
     * il che sarebbe solo approssimato per celle ripide.
     */
    private static int[] farthestFirst(Grid grid, Viewport3D viewport)
    {
        int cellsX = grid.cellsX;
        int cellsY = grid.cellsY;

        // Da che parte della griglia sta il lato lontano, lungo x e lungo y.
        boolean farIsHighX = viewport.isFarSideMaxX();
        boolean farIsHighY = viewport.isFarSideMaxY();

        int[] cells = new int[cellsX * cellsY];
        int count = 0;

        for (int a = 0; a < cellsX; a++)
        {
            int i = farIsHighX ? cellsX - 1 - a : a;
            for (int b = 0; b < cellsY; b++)
            {
                int j = farIsHighY ? cellsY - 1 - b : b;
                if (grid.finite[i][j] && grid.finite[i + 1][j] && grid.finite[i + 1][j + 1] && grid.finite[i][j + 1])
                {
                    cells[count++] = i * cellsY + j;
                }
            }
        }

        return Arrays.copyOf(cells, count);
    }


    /**
     * Campioni della funzione sui vertici della griglia, gia' proiettati in pixel interi.
     * {@code finite[i][j]} dice se il vertice (i, j) ha un'altezza finita, e quindi una posizione.
     */
    private static final class Grid
    {
        final Viewport3D viewport;
        final int cellsX;
        final int cellsY;
        final int[][] pixelX;
        final int[][] pixelY;
        final boolean[][] finite;

        <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
        Grid(Viewport3D viewport, Mapping<D, C> function, BiFunction<Double, Double, D> domainAdapter,
             Function<C, Double> heightAdapter, int cellsX, int cellsY)
        {
            this.viewport = viewport;
            this.cellsX = cellsX;
            this.cellsY = cellsY;
            this.pixelX = new int[cellsX + 1][cellsY + 1];
            this.pixelY = new int[cellsX + 1][cellsY + 1];
            this.finite = new boolean[cellsX + 1][cellsY + 1];

            for (int i = 0; i <= cellsX; i++)
            {
                for (int j = 0; j <= cellsY; j++)
                {
                    double x = x(i);
                    double y = y(j);
                    double z = heightAdapter.apply(function.apply(domainAdapter.apply(x, y)));

                    if (Double.isFinite(z)) {
                        finite[i][j] = true;
                        pixelX[i][j] = (int) Math.round(viewport.projectToPixelX(x, y, z));
                        pixelY[i][j] = (int) Math.round(viewport.projectToPixelY(x, y, z));
                    }
                }
            }
        }

        // L'ultimo vertice cade esattamente su max, senza l'errore di arrotondamento di min + range.
        double x(int i) { return i == cellsX ? viewport.maxX : viewport.minX + (viewport.maxX - viewport.minX) * i / cellsX; }
        double y(int j) { return j == cellsY ? viewport.maxY : viewport.minY + (viewport.maxY - viewport.minY) * j / cellsY; }
    }
}
