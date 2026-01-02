package net.gommagomma.smfn.client;

import java.awt.Color;
import java.util.function.Function;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer1D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter1D;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomials;
import net.gommagomma.smfn.math.algebra.structures.RealField;

public class PolynomialClient
{
    public static void main(String[] args)
    {
//    	RealMatrixRing MRing = new RealMatrixRing(2);
//    	
//    	RealMatrix M1 = MRing.createMatrix(new double[][]{{1, 0},{0, 1}});
//    	RealMatrix M0 = MRing.createMatrix(new double[][]{{1, 0},{0, -1}});
//    	RealMatrix M2 = MRing.createMatrix(new double[][]{{1, -1},{3, 2}});
//    	RealMatrix[] data = {M2, M1, M0};
//    	
//    	Polynomial<RealMatrix> polyMatrix = Polynomials.ring(MRing, data);
//    	
//    	System.out.println("P(x) = " + polyMatrix.toString());
//    	System.out.println("P(M2) evaluate = " + polyMatrix.evaluate(M2));

    	
        // --- 1. Definizione del Contesto e del Polinomio P(x) = 1.5x^3 + 3.0x^2 - 5.0x - 2.0 ---
        
        // 1.1 Definiamo la struttura base (Anello/Campo dei coefficienti)
        RealField R = RealField.INSTANCE;
        Real[] coeffs = {new Real(-2.0), new Real(-5.0), new Real(3.0), new Real(1.5)};

        Polynomial<Real> cubicPolynomial = Polynomials.of(R, coeffs);

        System.out.println("Polinomio da plottare: P(x) = " + cubicPolynomial);


        // --- 2. Setup del contesto grafico (nessuna modifica qui) ---
        int width = 800;
        int height = 600;
        
        SwingRenderer1D renderer = new SwingRenderer1D(width, height);
        
        JFrame frame = new JFrame("SMFN Polynomial Example: P(x) = 1.5x^3 + 3x^2 - 5x - 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        renderer.requestFocusInWindow();
        renderer.initBufferStrategy();

        // Range visualizzato
        Viewport viewport = new Viewport(-3.5, 3.5, -8.5, 8.5, width, height);

        // --- 3. Definizione degli adattatori (Adapter Pattern) ---
        // Adattatore da double a Real (per il dominio)
        Function<Double, Real> domainAdapter = Real::new;
        // Adattatore da Real a double (per il codominio)
        Function<Real, Double> codomainAdapter = Real::getValue; 

        // --- 4. Processo di rendering (Composizione dei grafici) ---
        renderer.startDrawing();
        
        renderer.clear(Color.BLACK);

        // Disegna gli assi cartesiani
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);
        
        // Disegna il Polinomio (ora la variabile è cubicPolynomial)
        renderer.setColor(Color.BLUE);
        FunctionPlotter1D.plotFunction(
            renderer, viewport, cubicPolynomial, domainAdapter, codomainAdapter
        );
        
        renderer.endDrawingAndFlush();
    }
}