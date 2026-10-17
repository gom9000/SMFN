package net.gommagomma.smfn.graphics.plotting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;

/**
 * Utility per plottare gli assi di una {@link Viewport3D}: gli spigoli x, y e z del riquadro che
 * si incontrano nell'angolo piu' vicino all'osservatore (z all'altezza di {@code minZ}), con
 * con tacche e valori lungo ciascuno e il nome scritto all'estremo lontano dall'angolo.
 * <p>
 * Si disegnano <b>dopo</b> la superficie: stando sul bordo del riquadro sul lato vicino, nessun
 * punto della superficie puo' coprirli, mentre disegnati prima verrebbero quasi interamente
 * nascosti. Gli assi seguono il punto di vista: ruotando la vista passano da uno spigolo
 * all'altro del riquadro.
 * <p>
 * {@link #plotWalls} disegna invece le tre pareti del riquadro sul lato lontano (due laterali e
 * il pavimento) con una griglia sui valori delle tacche: sta dietro la superficie, quindi si
 * disegna <b>prima</b>.
 */
public final class SurfaceAxisPlotter
{
    private static final DecimalFormat DF = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

    /** Lunghezza in pixel delle tacche. */
    private static final int TICK_SIZE = 5;
    /** Un asse piu' corto di cosi' sullo schermo (visto quasi di punta) non porta tacche. */
    private static final double MIN_AXIS_LENGTH_FOR_TICKS = 30.0;
    /** Spazio sullo schermo che si vuole per ogni tacca: ne determina il numero. */
    private static final double PIXELS_PER_TICK = 60.0;
    /** Larghezza approssimata di un carattere delle etichette, per allineare a sinistra del segno. */
    private static final int CHAR_WIDTH = 7;

    private SurfaceAxisPlotter() {}


    /**
     * @param renderer Il renderer da usare.
     * @param viewport La viewport 3D che definisce riquadro e punto di vista.
     * @param color Il colore di assi ed etichette.
     * @param showLabels Se disegnare le tacche con i loro valori e scrivere il nome di ogni asse.
     */
    public static void plotAxes(Renderer renderer, Viewport3D viewport, RgbColor color, boolean showLabels)
    {
        // Angolo vicino del riquadro, a z minimo: l'origine dei tre assi.
        double cornerX = viewport.isFarSideMaxX() ? viewport.minX : viewport.maxX;
        double cornerY = viewport.isFarSideMaxY() ? viewport.minY : viewport.maxY;
        double cornerZ = viewport.minZ;

        renderer.setColor(color);

        // Ogni asse parte dall'angolo e finisce sul lato opposto del riquadro, dove sta l'etichetta.
        drawAxis(renderer, viewport, "x", cornerX, cornerY, cornerZ,
                 cornerX == viewport.minX ? viewport.maxX : viewport.minX, cornerY, cornerZ,
                 viewport.minX, viewport.maxX, showLabels);
        drawAxis(renderer, viewport, "y", cornerX, cornerY, cornerZ,
                 cornerX, cornerY == viewport.minY ? viewport.maxY : viewport.minY, cornerZ,
                 viewport.minY, viewport.maxY, showLabels);
        drawAxis(renderer, viewport, "z", cornerX, cornerY, cornerZ,
                 cornerX, cornerY, viewport.maxZ,
                 viewport.minZ, viewport.maxZ, showLabels);
    }


    private static void drawAxis(Renderer renderer, Viewport3D viewport, String name,
                                 double x1, double y1, double z1, double x2, double y2, double z2,
                                 double minValue, double maxValue, boolean showLabels)
    {
        int px1 = (int) Math.round(viewport.projectToPixelX(x1, y1, z1));
        int py1 = (int) Math.round(viewport.projectToPixelY(x1, y1, z1));
        int px2 = (int) Math.round(viewport.projectToPixelX(x2, y2, z2));
        int py2 = (int) Math.round(viewport.projectToPixelY(x2, y2, z2));

        renderer.drawLine(px1, py1, px2, py2);

        if (!showLabels) return;

        double length = Math.hypot(px2 - px1, py2 - py1);
        double alongX = length > 0 ? (px2 - px1) / length : 0.0;
        double alongY = length > 0 ? (py2 - py1) / length : 0.0;

        drawTicks(renderer, viewport, px1, py1, px2, py2, length, minValue, maxValue);

        // Il nome dell'asse sta oltre l'estremo lontano dall'angolo, dopo l'ultima tacca.
        renderer.drawText(name, (int) Math.round(px2 + alongX * 18) + 4, (int) Math.round(py2 + alongY * 18) + 14);
    }

    /**
     * Tacche con il valore, a intervalli "tondi" (1, 2, 5 per una potenza di dieci) e in numero
     * proporzionato alla lunghezza dell'asse sullo schermo: vista di taglio un asse e' troppo
     * corto per portarne, e non ne ha. Ogni tacca e' un segmento sullo schermo, perpendicolare
     * all'asse e rivolto all'esterno della scena, cosi' non cade sulla superficie.
     */
    private static void drawTicks(Renderer renderer, Viewport3D viewport, int px1, int py1, int px2, int py2,
                                  double length, double minValue, double maxValue)
    {
        double[] values = tickValues(minValue, maxValue, length);
        if (values.length == 0) return;

        // Perpendicolare all'asse sullo schermo, verso l'esterno rispetto al centro della scena
        double perpX = -(py2 - py1) / length;
        double perpY = (px2 - px1) / length;
        double centerX = viewport.pixelWidth / 2.0 + viewport.offsetX;
        double centerY = viewport.pixelHeight / 2.0 + viewport.offsetY;
        if ((px1 - centerX) * perpX + (py1 - centerY) * perpY < 0) {
            perpX = -perpX;
            perpY = -perpY;
        }

        for (double value : values)
        {
            double t = (value - minValue) / (maxValue - minValue);
            int tx = (int) Math.round(px1 + (px2 - px1) * t);
            int ty = (int) Math.round(py1 + (py2 - py1) * t);
            int tipX = (int) Math.round(tx + perpX * TICK_SIZE);
            int tipY = (int) Math.round(ty + perpY * TICK_SIZE);

            renderer.drawLine(tx, ty, tipX, tipY);

            String text = DF.format(value);
            int textX = perpX >= 0 ? tipX + 3 : tipX - 3 - CHAR_WIDTH * text.length();
            renderer.drawText(text, textX, tipY + 5);
        }
    }

    /**
     * I valori delle tacche di un asse di dato intervallo, lungo {@code length} pixel sullo
     * schermo: nessuno se l'asse e' troppo corto.
     */
    private static double[] tickValues(double minValue, double maxValue, double length)
    {
        if (length < MIN_AXIS_LENGTH_FOR_TICKS) return new double[0];

        double spacing = niceSpacing(maxValue - minValue, (int) Math.max(1, Math.min(10, length / PIXELS_PER_TICK)));
        long first = (long) Math.ceil(minValue / spacing - 1e-9);
        long last = (long) Math.floor(maxValue / spacing + 1e-9);

        double[] values = new double[(int) Math.max(0, last - first + 1)];
        for (int k = 0; k < values.length; k++) {
            values[k] = (first + k) * spacing;
        }
        return values;
    }

    /** Lunghezza sullo schermo, in pixel, del segmento fra due punti della scena. */
    private static double pixelLength(Viewport3D viewport, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return Math.hypot(viewport.projectToPixelX(x2, y2, z2) - viewport.projectToPixelX(x1, y1, z1),
                          viewport.projectToPixelY(x2, y2, z2) - viewport.projectToPixelY(x1, y1, z1));
    }


    /**
     * Disegna le tre pareti del riquadro sul lato lontano dall'osservatore (la parete in fondo a
     * x, quella in fondo a y e il pavimento, o il soffitto se si guarda da sotto), ciascuna con il
     * suo bordo e una griglia di linee sugli stessi valori delle tacche di {@link #plotAxes}.
     * Servono a leggere le quote della superficie contro un riferimento.
     * <p>
     * Le pareti stanno <b>dietro</b> la superficie: vanno disegnate prima di essa (gli assi, che
     * stanno davanti, dopo). Come per le tacche, un asse visto quasi di punta e' troppo corto per
     * portare linee di griglia, e della sua parete resta il solo bordo.
     *
     * @param renderer Il renderer da usare.
     * @param viewport La viewport 3D che definisce riquadro e punto di vista.
     * @param color Il colore delle linee (di solito un grigio scuro, per non competere con la superficie).
     */
    public static void plotWalls(Renderer renderer, Viewport3D viewport, RgbColor color)
    {
        double farX = viewport.isFarSideMaxX() ? viewport.maxX : viewport.minX;
        double farY = viewport.isFarSideMaxY() ? viewport.maxY : viewport.minY;
        // Guardando dall'alto il pavimento e' sotto la superficie, dal basso lo e' il soffitto.
        double floorZ = viewport.elevation >= 0.0 ? viewport.minZ : viewport.maxZ;

        double[] xs = tickValues(viewport.minX, viewport.maxX,
            pixelLength(viewport, viewport.minX, farY, floorZ, viewport.maxX, farY, floorZ));
        double[] ys = tickValues(viewport.minY, viewport.maxY,
            pixelLength(viewport, farX, viewport.minY, floorZ, farX, viewport.maxY, floorZ));
        double[] zs = tickValues(viewport.minZ, viewport.maxZ,
            pixelLength(viewport, farX, farY, viewport.minZ, farX, farY, viewport.maxZ));

        renderer.setColor(color);

        // Pavimento: z = floorZ
        for (double x : xs) line(renderer, viewport, x, viewport.minY, floorZ, x, viewport.maxY, floorZ);
        for (double y : ys) line(renderer, viewport, viewport.minX, y, floorZ, viewport.maxX, y, floorZ);
        rectangle(renderer, viewport, viewport.minX, viewport.minY, floorZ, viewport.maxX, viewport.maxY, floorZ);

        // Parete in fondo a y: y = farY
        for (double x : xs) line(renderer, viewport, x, farY, viewport.minZ, x, farY, viewport.maxZ);
        for (double z : zs) line(renderer, viewport, viewport.minX, farY, z, viewport.maxX, farY, z);
        rectangle(renderer, viewport, viewport.minX, farY, viewport.minZ, viewport.maxX, farY, viewport.maxZ);

        // Parete in fondo a x: x = farX
        for (double y : ys) line(renderer, viewport, farX, y, viewport.minZ, farX, y, viewport.maxZ);
        for (double z : zs) line(renderer, viewport, farX, viewport.minY, z, farX, viewport.maxY, z);
        rectangle(renderer, viewport, farX, viewport.minY, viewport.minZ, farX, viewport.maxY, viewport.maxZ);
    }

    private static void line(Renderer renderer, Viewport3D viewport,
                             double x1, double y1, double z1, double x2, double y2, double z2)
    {
        renderer.drawLine(
            (int) Math.round(viewport.projectToPixelX(x1, y1, z1)), (int) Math.round(viewport.projectToPixelY(x1, y1, z1)),
            (int) Math.round(viewport.projectToPixelX(x2, y2, z2)), (int) Math.round(viewport.projectToPixelY(x2, y2, z2)));
    }

    /**
     * Il bordo di un rettangolo del riquadro, dato da due angoli opposti: in uno dei tre piani
     * (le coordinate che coincidono nei due angoli).
     */
    private static void rectangle(Renderer renderer, Viewport3D viewport,
                                  double x1, double y1, double z1, double x2, double y2, double z2)
    {
        // Il piano e' quello in cui una coordinata e' uguale nei due angoli: le altre due si alternano.
        if (z1 == z2) {
            line(renderer, viewport, x1, y1, z1, x2, y1, z1);
            line(renderer, viewport, x2, y1, z1, x2, y2, z1);
            line(renderer, viewport, x2, y2, z1, x1, y2, z1);
            line(renderer, viewport, x1, y2, z1, x1, y1, z1);
        } else if (y1 == y2) {
            line(renderer, viewport, x1, y1, z1, x2, y1, z1);
            line(renderer, viewport, x2, y1, z1, x2, y1, z2);
            line(renderer, viewport, x2, y1, z2, x1, y1, z2);
            line(renderer, viewport, x1, y1, z2, x1, y1, z1);
        } else {
            line(renderer, viewport, x1, y1, z1, x1, y2, z1);
            line(renderer, viewport, x1, y2, z1, x1, y2, z2);
            line(renderer, viewport, x1, y2, z2, x1, y1, z2);
            line(renderer, viewport, x1, y1, z2, x1, y1, z1);
        }
    }

    /** Intervallo "tondo" (1, 2 o 5 per una potenza di dieci) che da' circa {@code count} intervalli nel range. */
    private static double niceSpacing(double range, int count)
    {
        double rough = range / count;
        double power = Math.pow(10, Math.floor(Math.log10(rough)));
        double normalized = rough / power;
        double nice = normalized < 1.5 ? 1.0 : normalized < 3.5 ? 2.0 : normalized < 7.5 ? 5.0 : 10.0;
        return nice * power;
    }
}
