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
 * nome e range di ciascuno scritti all'estremo lontano dall'angolo.
 * <p>
 * Si disegnano <b>dopo</b> la superficie: stando sul bordo del riquadro sul lato vicino, nessun
 * punto della superficie puo' coprirli, mentre disegnati prima verrebbero quasi interamente
 * nascosti. Gli assi seguono il punto di vista: ruotando la vista passano da uno spigolo
 * all'altro del riquadro.
 */
public final class SurfaceAxisPlotter
{
    private static final DecimalFormat DF = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

    private SurfaceAxisPlotter() {}


    /**
     * @param renderer Il renderer da usare.
     * @param viewport La viewport 3D che definisce riquadro e punto di vista.
     * @param color Il colore di assi ed etichette.
     * @param showLabels Se scrivere il nome di ogni asse e i valori ai suoi estremi.
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

        if (showLabels) {
            // Un'etichetta sola per asse, all'estremo lontano dall'angolo: nome e range.
            renderer.drawText(name + " [" + DF.format(minValue) + ", " + DF.format(maxValue) + "]", px2 + 4, py2 + 14);
        }
    }
}
