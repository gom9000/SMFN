package net.gommagomma.smfn.graphics.plotting;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Light;
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
 * <p>
 * Ogni metodo ha due forme. Quella con la funzione (e i suoi adattatori e la risoluzione) campiona
 * e disegna in un colpo solo: e' quella per un disegno singolo. Quella con una {@link SurfaceGrid}
 * disegna campioni gia' presi, e serve quando la stessa superficie si ridisegna spesso (rotazione,
 * zoom): la funzione si valuta una volta sola.
 */
public class SurfacePlotter
{
    /** Lato in pixel dei riquadri in cui si divide una faccia a colori per vertice. */
    private static final double SMOOTH_SUBDIVISION_PIXELS = 4.0;
    private static final int SMOOTH_MAX_SUBDIVISIONS = 8;

    private SurfacePlotter() {}


    // =====================================================================================
    // Facce piene, colore uniforme per faccia
    // =====================================================================================

    /**
     * Disegna la superficie a facce piene, dalla piu' lontana alla piu' vicina (algoritmo del
     * pittore): le facce vicine coprono quelle lontane. L'ordine e' esatto per una superficie a
     * un solo valore z = f(x, y), che e' cio' che questo plotter rappresenta, e si ottiene
     * scorrendo la griglia dal lato lontano, senza ordinamenti.
     * <p>
     * Ogni faccia (una cella della griglia) e' colorata con {@code colorMapper} applicato al valore
     * della funzione nel <b>centro</b> della cella: per questo servono {@code cellsX * cellsY}
     * valutazioni in piu' rispetto ai vertici. Per un colore che segua la superficie senza
     * scalini vedi {@link #plotSmoothSurface}.
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
        plotSurface(renderer, viewport, SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY), colorMapper);
    }

    /**
     * Come {@link #plotSurface(Renderer, Viewport3D, Mapping, BiFunction, Function, ColorMapper, int, int)},
     * ma con le facce illuminate: il colore di ciascuna, scelto da {@code colorMapper}, viene
     * scalato dall'intensita' che la {@code light} dà alla sua inclinazione. Il colore resta un
     * canale indipendente dalla geometria, e la luce ne e' un secondo che agisce sopra: la forma
     * si legge anche quando il colore e' uniforme.
     * <p>
     * La normale di una faccia e' quella del piano che meglio la approssima, calcolata dalle altezze
     * dei suoi quattro vertici nelle coordinate del cubo della scena (quelle che si vedono sullo
     * schermo). Si usa sempre il lato rivolto all'osservatore: guardando la superficie da sotto,
     * una luce che sta sopra non illumina la faccia che si vede.
     *
     * @param light La luce con cui illuminare le facce.
     * @throws IllegalArgumentException se {@code light} e' nullo o se le celle sono meno di 1
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotSurface(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                     BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                     ColorMapper<C> colorMapper, Light light, int cellsX, int cellsY)
    {
        requireLight(light);
        plotSurface(renderer, viewport, SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY), colorMapper, light);
    }

    /**
     * Come il primo {@code plotSurface}, su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport
     */
    public static <C extends AlgebraicElement<C>>
    void plotSurface(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, ColorMapper<C> colorMapper)
    {
        drawFlatFaces(renderer, viewport, grid, colorMapper, null);
    }

    /**
     * Come il secondo {@code plotSurface}, su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport o se {@code light} e' nullo
     */
    public static <C extends AlgebraicElement<C>>
    void plotSurface(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, ColorMapper<C> colorMapper, Light light)
    {
        requireLight(light);
        drawFlatFaces(renderer, viewport, grid, colorMapper, light);
    }

    private static <C extends AlgebraicElement<C>>
    void drawFlatFaces(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, ColorMapper<C> colorMapper, Light light)
    {
        requireMatch(grid, viewport);
        Projection projection = new Projection(grid, viewport);
        double[] toViewer = viewport.towardViewer();

        for (int cell : farthestFirst(grid, viewport))
        {
            int i = cell / grid.cellsY;
            int j = cell % grid.cellsY;

            RgbColor color = colorMapper.map(grid.centerValue(i, j));
            if (light != null) {
                color = illuminated(color, light, grid, viewport, toViewer, i, j);
            }

            renderer.setColor(color);
            projection.fillCell(renderer, i, j);
        }
    }


    // =====================================================================================
    // Facce piene, colore interpolato fra i vertici
    // =====================================================================================

    /**
     * Come {@link #plotSurface(Renderer, Viewport3D, Mapping, BiFunction, Function, ColorMapper, int, int)},
     * ma il colore e' quello dei <b>vertici</b> e varia con continuita' dentro ogni faccia.
     * <p>
     * Con un colore per faccia, preso dal centro, dove la funzione cambia in fretta (una scogliera,
     * un polo) la faccia ha un colore che non e' quello dei suoi bordi, e si vedono strisce. Qui
     * {@code colorMapper} e' applicato ai quattro vertici e il colore dentro la faccia ne e' l'interpolazione
     * bilineare (fra i colori, non fra i valori: per un colore che non e' una funzione continua del
     * valore, come una palette a salti, il passaggio fra due vertici attraversa i colori intermedi).
     * Costa meno di un colore per faccia: non servono le valutazioni al centro delle celle.
     * <p>
     * La faccia e' divisa in piccoli riquadri, ciascuno di un colore: il renderer ha solo
     * riempimenti uniformi. Se la faccia si divide, ne viene prima riempita l'intera superficie con
     * il colore medio (vedi il commento nel codice), quindi le chiamate a {@code fillPolygon} per
     * faccia sono 1 + k * k.
     *
     * @see #plotSurface per il significato dei parametri
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotSmoothSurface(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                           BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                           ColorMapper<C> colorMapper, int cellsX, int cellsY)
    {
        plotSmoothSurface(renderer, viewport, SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY), colorMapper);
    }

    /**
     * Come {@link #plotSmoothSurface(Renderer, Viewport3D, Mapping, BiFunction, Function, ColorMapper, int, int)},
     * con le facce illuminate: l'intensita' della luce e' una per faccia (vedi
     * {@link #plotSurface(Renderer, Viewport3D, Mapping, BiFunction, Function, ColorMapper, Light, int, int)})
     * e scala il colore interpolato.
     *
     * @throws IllegalArgumentException se {@code light} e' nullo o se le celle sono meno di 1
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotSmoothSurface(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                           BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                           ColorMapper<C> colorMapper, Light light, int cellsX, int cellsY)
    {
        requireLight(light);
        plotSmoothSurface(renderer, viewport, SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY), colorMapper, light);
    }

    /**
     * Come il primo {@code plotSmoothSurface}, su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport
     */
    public static <C extends AlgebraicElement<C>>
    void plotSmoothSurface(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, ColorMapper<C> colorMapper)
    {
        drawSmoothFaces(renderer, viewport, grid, colorMapper, null);
    }

    /**
     * Come il secondo {@code plotSmoothSurface}, su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport o se {@code light} e' nullo
     */
    public static <C extends AlgebraicElement<C>>
    void plotSmoothSurface(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, ColorMapper<C> colorMapper, Light light)
    {
        requireLight(light);
        drawSmoothFaces(renderer, viewport, grid, colorMapper, light);
    }

    private static <C extends AlgebraicElement<C>>
    void drawSmoothFaces(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, ColorMapper<C> colorMapper, Light light)
    {
        requireMatch(grid, viewport);
        Projection projection = new Projection(grid, viewport);
        double[] toViewer = viewport.towardViewer();

        for (int cell : farthestFirst(grid, viewport))
        {
            int i = cell / grid.cellsY;
            int j = cell % grid.cellsY;

            RgbColor c00 = colorMapper.map(grid.vertexValue(i, j));
            RgbColor c10 = colorMapper.map(grid.vertexValue(i + 1, j));
            RgbColor c11 = colorMapper.map(grid.vertexValue(i + 1, j + 1));
            RgbColor c01 = colorMapper.map(grid.vertexValue(i, j + 1));

            double[] normal = light == null ? null : faceNormal(grid, viewport, toViewer, i, j);

            int[] xs = { projection.x[i][j], projection.x[i + 1][j], projection.x[i + 1][j + 1], projection.x[i][j + 1] };
            int[] ys = { projection.y[i][j], projection.y[i + 1][j], projection.y[i + 1][j + 1], projection.y[i][j + 1] };

            int k = c00.equals(c10) && c00.equals(c11) && c00.equals(c01) ? 1 : subdivisions(xs, ys);

            if (k > 1)
            {
                // Prima la faccia intera, di un colore medio: i riquadri sul suo bordo seguono i lati
                // con l'errore di arrotondamento dei loro vertici, e due facce vicine divise in modo
                // diverso lascerebbero fra loro sottili fessure da cui traspare lo sfondo. Il bordo
                // della faccia intera e' retto e identico per le due facce, e le fessure restano
                // coperte da questo colore.
                RgbColor average = bilinear(c00, c10, c11, c01, 0.5, 0.5);
                renderer.setColor(light == null ? average : light.illuminate(average, normal[0], normal[1], normal[2]));
                renderer.fillPolygon(xs, ys);
            }

            for (int a = 0; a < k; a++)
            {
                for (int b = 0; b < k; b++)
                {
                    double u0 = (double) a / k;
                    double u1 = (double) (a + 1) / k;
                    double v0 = (double) b / k;
                    double v1 = (double) (b + 1) / k;

                    RgbColor color = bilinear(c00, c10, c11, c01, (u0 + u1) / 2.0, (v0 + v1) / 2.0);
                    if (light != null) {
                        color = light.illuminate(color, normal[0], normal[1], normal[2]);
                    }

                    renderer.setColor(color);
                    renderer.fillPolygon(
                        new int[] { pixel(xs, u0, v0), pixel(xs, u1, v0), pixel(xs, u1, v1), pixel(xs, u0, v1) },
                        new int[] { pixel(ys, u0, v0), pixel(ys, u1, v0), pixel(ys, u1, v1), pixel(ys, u0, v1) });
                }
            }
        }
    }

    /** In quanti riquadri per lato dividere una faccia, in base alla sua dimensione sullo schermo. */
    private static int subdivisions(int[] xs, int[] ys)
    {
        int width = Math.max(Math.max(xs[0], xs[1]), Math.max(xs[2], xs[3])) - Math.min(Math.min(xs[0], xs[1]), Math.min(xs[2], xs[3]));
        int height = Math.max(Math.max(ys[0], ys[1]), Math.max(ys[2], ys[3])) - Math.min(Math.min(ys[0], ys[1]), Math.min(ys[2], ys[3]));
        int k = (int) Math.ceil(Math.max(width, height) / SMOOTH_SUBDIVISION_PIXELS);
        return Math.max(1, Math.min(SMOOTH_MAX_SUBDIVISIONS, k));
    }

    /**
     * Coordinata pixel del punto (u, v) della faccia, con i vertici in ordine (0,0), (1,0), (1,1), (0,1).
     * Si interpola prima lungo i due lati u e poi fra di loro, e agli estremi (v = 0 o 1) il risultato
     * e' esattamente quello del lato: due facce vicine arrivano cosi' allo stesso pixel sul lato comune.
     */
    private static int pixel(int[] c, double u, double v)
    {
        double bottom = c[0] * (1.0 - u) + c[1] * u;
        double top = c[3] * (1.0 - u) + c[2] * u;
        return (int) Math.round(bottom * (1.0 - v) + top * v);
    }

    /** Colore al punto (u, v) di una faccia con i colori dati ai vertici (0,0), (1,0), (1,1), (0,1). */
    private static RgbColor bilinear(RgbColor c00, RgbColor c10, RgbColor c11, RgbColor c01, double u, double v)
    {
        return new RgbColor(
            channel(c00.r, c10.r, c11.r, c01.r, u, v),
            channel(c00.g, c10.g, c11.g, c01.g, u, v),
            channel(c00.b, c10.b, c11.b, c01.b, u, v),
            channel(c00.a, c10.a, c11.a, c01.a, u, v));
    }

    private static int channel(int v00, int v10, int v11, int v01, double u, double v)
    {
        double bottom = v00 * (1.0 - u) + v10 * u;
        double top = v01 * (1.0 - u) + v11 * u;
        long value = Math.round(bottom * (1.0 - v) + top * v);
        return (int) Math.max(0, Math.min(255, value));
    }


    // =====================================================================================
    // Reticoli
    // =====================================================================================

    /**
     * Disegna la superficie come reticolo di segmenti (wireframe) di un solo colore. Il reticolo e'
     * trasparente: non ci sono facce che ne coprano altre, quindi non serve alcun ordinamento. Per
     * un reticolo che nasconde le proprie linee dietro i rilievi vedi {@link #plotHiddenLineWireframe}.
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
        plotWireframe(renderer, viewport, SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY), color);
    }

    /**
     * Come {@link #plotWireframe(Renderer, Viewport3D, Mapping, BiFunction, Function, RgbColor, int, int)},
     * su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport
     */
    public static <C extends AlgebraicElement<C>>
    void plotWireframe(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, RgbColor color)
    {
        requireMatch(grid, viewport);
        Projection projection = new Projection(grid, viewport);

        renderer.setColor(color);

        for (int j = 0; j <= grid.cellsY; j++)
        {
            for (int i = 0; i < grid.cellsX; i++)
            {
                if (grid.isFinite(i, j) && grid.isFinite(i + 1, j)) {
                    renderer.drawLine(projection.x[i][j], projection.y[i][j], projection.x[i + 1][j], projection.y[i + 1][j]);
                }
            }
        }
        for (int i = 0; i <= grid.cellsX; i++)
        {
            for (int j = 0; j < grid.cellsY; j++)
            {
                if (grid.isFinite(i, j) && grid.isFinite(i, j + 1)) {
                    renderer.drawLine(projection.x[i][j], projection.y[i][j], projection.x[i][j + 1], projection.y[i][j + 1]);
                }
            }
        }
    }

    /**
     * Disegna il reticolo con le linee nascoste: ogni cella viene riempita con il colore di sfondo
     * e poi se ne tracciano i quattro lati, dalla cella piu' lontana alla piu' vicina, cosi' le
     * celle vicine coprono le linee di quelle lontane. Affianca {@link #plotWireframe}, che invece e'
     * trasparente e mostra tutto il reticolo, comprese le parti che la superficie nasconderebbe.
     * <p>
     * Il riempimento nasconde anche cio' che era gia' sul renderer: {@code backgroundColor} deve essere
     * quello dello sfondo (lo stesso passato a {@code clear}), altrimenti le celle risultano come
     * macchie. Una cella con un vertice non finito non viene disegnata.
     *
     * @param color Il colore dei segmenti.
     * @param backgroundColor Il colore con cui si coprono le linee nascoste.
     * @throws IllegalArgumentException se {@code cellsX} o {@code cellsY} sono minori di 1
     * @see #plotSurface per il significato degli altri parametri
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotHiddenLineWireframe(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                                 BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                                 RgbColor color, RgbColor backgroundColor, int cellsX, int cellsY)
    {
        plotHiddenLineWireframe(renderer, viewport,
            SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY), color, backgroundColor);
    }

    /**
     * Come {@link #plotHiddenLineWireframe(Renderer, Viewport3D, Mapping, BiFunction, Function, RgbColor, RgbColor, int, int)},
     * su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport
     */
    public static <C extends AlgebraicElement<C>>
    void plotHiddenLineWireframe(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid,
                                 RgbColor color, RgbColor backgroundColor)
    {
        requireMatch(grid, viewport);
        Projection projection = new Projection(grid, viewport);

        for (int cell : farthestFirst(grid, viewport))
        {
            int i = cell / grid.cellsY;
            int j = cell % grid.cellsY;

            int[] xs = { projection.x[i][j], projection.x[i + 1][j], projection.x[i + 1][j + 1], projection.x[i][j + 1] };
            int[] ys = { projection.y[i][j], projection.y[i + 1][j], projection.y[i + 1][j + 1], projection.y[i][j + 1] };

            renderer.setColor(backgroundColor);
            renderer.fillPolygon(xs, ys);

            renderer.setColor(color);
            for (int k = 0; k < 4; k++) {
                int next = (k + 1) % 4;
                renderer.drawLine(xs[k], ys[k], xs[next], ys[next]);
            }
        }
    }


    // =====================================================================================
    // Curve di livello
    // =====================================================================================

    /**
     * Disegna la superficie a facce piene di un solo colore con sopra le sue curve di livello: le
     * linee a z costante, che seguono la quota come su una mappa topografica. Le curve stanno sulla
     * superficie, quindi la parte nascosta dalle facce vicine non si vede.
     * <p>
     * I livelli sono {@code levelCount} valori di z equidistanti fra il minimo e il massimo
     * dell'altezza nei punti campionati (estremi esclusi). Ogni curva e' calcolata cella per cella
     * interpolando linearmente le altezze dei vertici; una cella con un vertice non finito non e'
     * disegnata.
     *
     * @param faceColor Il colore delle facce.
     * @param lineColor Il colore delle curve.
     * @param levelCount Quante curve (almeno 1).
     * @throws IllegalArgumentException se {@code levelCount} o le celle sono minori di 1
     * @see #plotSurface per il significato degli altri parametri
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotLevelCurves(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                         BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                         RgbColor faceColor, RgbColor lineColor, int levelCount, int cellsX, int cellsY)
    {
        requireLevelCount(levelCount);
        plotLevelCurves(renderer, viewport, SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY),
                        faceColor, lineColor, levelCount);
    }

    /**
     * Come {@link #plotLevelCurves(Renderer, Viewport3D, Mapping, BiFunction, Function, RgbColor, RgbColor, int, int)},
     * su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport o se {@code levelCount} e' minore di 1
     */
    public static <C extends AlgebraicElement<C>>
    void plotLevelCurves(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid,
                         RgbColor faceColor, RgbColor lineColor, int levelCount)
    {
        requireLevelCount(levelCount);
        double[] range = grid.heightRange();
        double[] levels = range != null && range[0] < range[1]
            ? LevelCurvePlotter.evenLevels(range[0], range[1], levelCount) : new double[0];
        drawLevelCurves(renderer, viewport, grid, value -> faceColor, levels, level -> lineColor);
    }

    /**
     * Come {@link #plotLevelCurves(Renderer, Viewport3D, Mapping, BiFunction, Function, RgbColor, RgbColor, int, int)},
     * ma con facce colorate da {@code colorMapper} (come in {@link #plotSurface}), i livelli scelti
     * da chi chiama (vedi {@link LevelCurvePlotter#evenLevels}) e un colore per ciascuno.
     *
     * @param levels I valori di z per cui disegnare la curva.
     * @param levelColor Il colore della curva di ciascun livello.
     * @throws IllegalArgumentException se le celle sono meno di 1 o se i livelli o il colore sono nulli
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotLevelCurves(Renderer renderer, Viewport3D viewport, Mapping<D, C> function,
                         BiFunction<Double, Double, D> domainAdapter, Function<C, Double> heightAdapter,
                         ColorMapper<C> colorMapper, double[] levels, Function<Double, RgbColor> levelColor,
                         int cellsX, int cellsY)
    {
        requireLevels(levels, levelColor);
        plotLevelCurves(renderer, viewport, SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cellsX, cellsY),
                        colorMapper, levels, levelColor);
    }

    /**
     * Come la forma completa di {@code plotLevelCurves}, su campioni gia' presi.
     *
     * @throws IllegalArgumentException se la griglia non ha il riquadro della viewport o se i livelli o il colore sono nulli
     */
    public static <C extends AlgebraicElement<C>>
    void plotLevelCurves(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid,
                         ColorMapper<C> colorMapper, double[] levels, Function<Double, RgbColor> levelColor)
    {
        requireLevels(levels, levelColor);
        drawLevelCurves(renderer, viewport, grid, colorMapper, levels, levelColor);
    }

    private static <C extends AlgebraicElement<C>>
    void drawLevelCurves(Renderer renderer, Viewport3D viewport, SurfaceGrid<C> grid, ColorMapper<C> colorMapper,
                         double[] levels, Function<Double, RgbColor> levelColor)
    {
        requireMatch(grid, viewport);
        Projection projection = new Projection(grid, viewport);

        for (int cell : farthestFirst(grid, viewport))
        {
            int i = cell / grid.cellsY;
            int j = cell % grid.cellsY;

            renderer.setColor(colorMapper.map(grid.centerValue(i, j)));
            projection.fillCell(renderer, i, j);

            final double x0 = grid.x(i), y0 = grid.y(j), width = grid.x(i + 1) - x0, height = grid.y(j + 1) - y0;
            for (double level : levels)
            {
                renderer.setColor(levelColor.apply(level));
                MarchingSquares.segments(grid.height(i, j), grid.height(i + 1, j), grid.height(i + 1, j + 1), grid.height(i, j + 1), level,
                    (u1, v1, u2, v2) -> renderer.drawLine(
                        (int) Math.round(viewport.projectToPixelX(x0 + u1 * width, y0 + v1 * height, level)),
                        (int) Math.round(viewport.projectToPixelY(x0 + u1 * width, y0 + v1 * height, level)),
                        (int) Math.round(viewport.projectToPixelX(x0 + u2 * width, y0 + v2 * height, level)),
                        (int) Math.round(viewport.projectToPixelY(x0 + u2 * width, y0 + v2 * height, level))));
            }
        }
    }


    // =====================================================================================
    // Comuni
    // =====================================================================================

    private static void requireLight(Light light)
    {
        if (light == null) {
            throw new IllegalArgumentException("La luce non puo' essere nulla (per non illuminare si usa l'altro metodo).");
        }
    }

    private static void requireLevelCount(int levelCount)
    {
        if (levelCount < 1) {
            throw new IllegalArgumentException("Numero di livelli non valido: " + levelCount + " (richiesto almeno 1).");
        }
    }

    private static void requireLevels(double[] levels, Function<Double, RgbColor> levelColor)
    {
        if (levels == null || levelColor == null) {
            throw new IllegalArgumentException("I livelli e il colore non possono essere nulli.");
        }
    }

    private static void requireMatch(SurfaceGrid<?> grid, Viewport3D viewport)
    {
        if (!grid.matches(viewport)) {
            throw new IllegalArgumentException(
                "La griglia e' stata campionata su [" + grid.minX + ", " + grid.maxX + "] x [" + grid.minY + ", " + grid.maxY
                + "], la viewport ha [" + viewport.minX + ", " + viewport.maxX + "] x [" + viewport.minY + ", " + viewport.maxY + "].");
        }
    }

    /** La normale di una faccia, nel cubo della scena e dal lato che l'osservatore vede. */
    private static double[] faceNormal(SurfaceGrid<?> grid, Viewport3D viewport, double[] toViewer, int i, int j)
    {
        // Altezze dei quattro vertici nel cubo della scena
        double z00 = viewport.normalizedZ(grid.height(i, j));
        double z10 = viewport.normalizedZ(grid.height(i + 1, j));
        double z01 = viewport.normalizedZ(grid.height(i, j + 1));
        double z11 = viewport.normalizedZ(grid.height(i + 1, j + 1));

        // Lati della cella nel cubo: l'intervallo [-1, 1] diviso nel numero di celle
        double cellWidthX = 2.0 / grid.cellsX;
        double cellWidthY = 2.0 / grid.cellsY;

        // Pendenze medie lungo x e lungo y; la normale verso l'alto di z = h(x, y) e' (-hx, -hy, 1)
        double slopeX = ((z10 + z11) - (z00 + z01)) / (2.0 * cellWidthX);
        double slopeY = ((z01 + z11) - (z00 + z10)) / (2.0 * cellWidthY);
        double normalX = -slopeX;
        double normalY = -slopeY;
        double normalZ = 1.0;

        // Si illumina il lato che l'osservatore vede
        if (normalX * toViewer[0] + normalY * toViewer[1] + normalZ * toViewer[2] < 0.0) {
            return new double[] { slopeX, slopeY, -1.0 };
        }
        return new double[] { normalX, normalY, normalZ };
    }

    /** Il colore della cella (i, j) illuminato in base alla sua inclinazione, vista dall'osservatore. */
    private static RgbColor illuminated(RgbColor color, Light light, SurfaceGrid<?> grid, Viewport3D viewport,
                                        double[] toViewer, int i, int j)
    {
        double[] normal = faceNormal(grid, viewport, toViewer, i, j);
        return light.illuminate(color, normal[0], normal[1], normal[2]);
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
    private static int[] farthestFirst(SurfaceGrid<?> grid, Viewport3D viewport)
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
                if (grid.isCellComplete(i, j)) {
                    cells[count++] = i * cellsY + j;
                }
            }
        }

        return Arrays.copyOf(cells, count);
    }


    /** I vertici della griglia proiettati sullo schermo con una viewport, in pixel interi. */
    private static final class Projection
    {
        final int[][] x;
        final int[][] y;

        Projection(SurfaceGrid<?> grid, Viewport3D viewport)
        {
            x = new int[grid.cellsX + 1][grid.cellsY + 1];
            y = new int[grid.cellsX + 1][grid.cellsY + 1];

            for (int i = 0; i <= grid.cellsX; i++)
            {
                for (int j = 0; j <= grid.cellsY; j++)
                {
                    if (grid.isFinite(i, j)) {
                        x[i][j] = (int) Math.round(viewport.projectToPixelX(grid.x(i), grid.y(j), grid.height(i, j)));
                        y[i][j] = (int) Math.round(viewport.projectToPixelY(grid.x(i), grid.y(j), grid.height(i, j)));
                    }
                }
            }
        }

        /** Riempie la faccia (i, j) con il colore corrente del renderer. */
        void fillCell(Renderer renderer, int i, int j)
        {
            renderer.fillPolygon(
                new int[] { x[i][j], x[i + 1][j], x[i + 1][j + 1], x[i][j + 1] },
                new int[] { y[i][j], y[i + 1][j], y[i + 1][j + 1], y[i][j + 1] });
        }
    }
}
