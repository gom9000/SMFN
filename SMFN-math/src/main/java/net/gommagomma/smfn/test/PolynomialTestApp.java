package net.gommagomma.smfn.test;

import java.awt.Color;
import java.util.function.Function;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer1D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter1D;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter1D;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.functions.PolynomialFunction;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;

public class PolynomialTestApp
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica (y = f(x)) ---
    	RealVector coeffs = new RealVector(new Real(1.0), new Real(1.0), new Real(1.0), new Real(1.0));
        PolynomialFunction<Real> linearFunction = new PolynomialFunction<>(coeffs);

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;
        
        // Inizializza il renderer Swing
        SwingRenderer1D renderer = new SwingRenderer1D(width, height);
        
        // Prepara la finestra Swing
        JFrame frame = new JFrame("SMFN Polynomial Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy(); // Necessario per il buffer

        // Definisci l'area matematica da visualizzare: [-5, 5] x [-10, 20]
        Viewport viewport = new Viewport(-10.0, 10.0, -10.0, 10.0, width, height);

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
        
        // Mostra il risultato a schermo
        renderer.flush();
    }
}
