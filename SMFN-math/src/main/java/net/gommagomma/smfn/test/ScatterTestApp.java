package net.gommagomma.smfn.test;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer1D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter1D;
import net.gommagomma.smfn.graphics.plotting.ScatterPlotter2D;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.geometry.Point;

public class ScatterTestApp
{
    public static void main(String[] args)
    {
    	SwingUtilities.invokeLater(() -> {
        int width = 800;
        int height = 600;
        
        SwingRenderer1D renderer = new SwingRenderer1D(width, height);
        JFrame frame = new JFrame("SMFN Scatter Points Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        // Definisci l'area matematica (Viewport): [-2.0, 1.0] x [-1.5, 1.5] 
        // L'area classica che contiene l'intero set
        Viewport viewport = new Viewport(-5, 5, -5, 5, width, height);

        
        // --- Crea un insieme di punti matematici ---
        List<Point> dataPoints = Arrays.asList(
            new Point(new Real(0.0), new Real(0.0)),
            new Point(new Real(1.0), new Real(2.0)),
            new Point(new Real(-1.0), new Real(-1.0))
        );

        renderer.clear(Color.BLACK);

        // --- Processo di rendering ---
        CartesianAxisPlotter1D.plotAxes(renderer, viewport, Color.DARK_GRAY);

        // Plotta i punti in rosso
        //ScatterPlotter2D.plotPoints(renderer, viewport, dataPoints, Color.BLUE);
        
        renderer.flush();
    });
    }
}
