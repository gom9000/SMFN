package net.gommagomma.smfn;

import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer1D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter1D;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter1D;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.functions.LinearFunction;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.Function;

public class LinearTestApp
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica (f(x) = 2x + 1) ---
        LinearFunction<Real> linearFunction = new LinearFunction<>(new Real(1.0), new Real(0.0));

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        // Inizializza il renderer Swing
        SwingRenderer1D renderer = new SwingRenderer1D(width, height);
        
        // Prepara la finestra Swing
        JFrame frame = new JFrame("SMFN 1D Plotting Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy(); // Necessario per il buffer

        // Definisci l'area matematica da visualizzare:
        Viewport viewport = new Viewport(-10.0, 10.0, -10.0, 20.0, width, height);

        // --- 3. Definizione degli adattatori (Adapter Pattern) ---
        // Adattatore da double a Real (per il dominio)
        Function<Double, Real> domainAdapter = Real::new;
        // Adattatore da Real a double (per il codominio)
        Function<Real, Double> codomainAdapter = Real::getValue; // Assumo esista getValue() in Real

        // --- 4. Processo di rendering (Composizione dei grafici) ---
        
        // Pulisci lo sfondo
        renderer.clear(Color.BLACK);

        // Disegna gli assi cartesiani
        CartesianAxisPlotter1D.plotAxes(renderer, viewport, Color.DARK_GRAY);

        // Disegna la funzione lineare (in blu)
        renderer.setColor(Color.BLUE);
        FunctionPlotter1D.plotFunction(
            renderer, viewport, linearFunction, domainAdapter, codomainAdapter
        );

        // Calcola l'ampiezza del range matematico
        double rangeX = viewport.maxX - viewport.minX;
        double rangeY = viewport.maxY - viewport.minY;

        DecimalFormat DF = new DecimalFormat("0.000E0"); // Formattazione scientifica
        String infoText1 = "Range X: [" + DF.format(viewport.minX) + ", " + DF.format(viewport.maxX) + "] (Ampiezza: " + DF.format(rangeX) + ")";
        String infoText2 = "Range Y: [" + DF.format(viewport.minY) + ", " + DF.format(viewport.maxY) + "] (Ampiezza: " + DF.format(rangeY) + ")";
        String infoText3 = "Zoom: " + DF.format(3.0 / rangeX) + "x"; // Calcolo dello zoom relativo all'ampiezza iniziale di 3.0

        // Disegna il testo in overlay (coordinate pixel fisse)
        renderer.drawOverlayText(infoText1, 10, 20, Color.RED);
        renderer.drawOverlayText(infoText2, 10, 35, Color.RED);
        renderer.drawOverlayText(infoText3, 10, 50, Color.RED);

        // Mostra il risultato a schermo
        renderer.flush();
    }
}
