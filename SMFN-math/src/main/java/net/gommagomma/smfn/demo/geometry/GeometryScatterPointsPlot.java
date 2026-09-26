package net.gommagomma.smfn.demo.geometry;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer1D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.ScatterPlotter;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.geometry.Point;

public class GeometryScatterPointsPlot
{
    public static void main(String[] args)
    {
        int width = 800;
        int height = 600;
        
        SwingRenderer1D renderer = new SwingRenderer1D(width, height);
        SwingWindow.show(renderer, "SMFN Scatter Points Plot");

        // Definisci l'area matematica (Viewport): [-2.0, 1.0] x [-1.5, 1.5] 
        // L'area classica che contiene l'intero set
        Viewport viewport = new Viewport(-5, 5, -5, 5, width, height);

        
        // --- Crea un insieme di punti matematici ---
        List<Point> dataPoints = Arrays.asList(
            new Point(new Real(0.0), new Real(0.0)),
            new Point(new Real(1.0), new Real(2.0)),
            new Point(new Real(-1.0), new Real(-1.0))
        );

        renderer.startDrawing();

        renderer.clear(Color.BLACK);

        // --- Processo di rendering ---
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        // Plotta i punti in rosso
        ScatterPlotter.plotPoints(renderer, viewport, dataPoints, Color.BLUE);
        
        renderer.endDrawingAndFlush();
    }
}
