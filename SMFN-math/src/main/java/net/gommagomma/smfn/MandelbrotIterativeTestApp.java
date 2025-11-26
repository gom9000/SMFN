package net.gommagomma.smfn;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.function.BiFunction;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.core.ViewportController;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter2D;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.fractals.MandelbrotFunction;

// L'app implementa l'interfaccia handler per gestire gli aggiornamenti della viewport
public class MandelbrotIterativeTestApp
implements ViewportController.ViewportUpdateHandler
{
    private final SwingRenderer2D renderer;
    private final MandelbrotFunction mandelbrotFunction;
    private final BiFunction<Double, Double, Complex> domainAdapter;
    private final ColorMapper<Real> colorMapper;
    private ViewportController viewportController;
    private final Color selectionColor = new Color(0, 0, 255, 100); // Blu semi-trasparente
    private static final DecimalFormat DF = new DecimalFormat("0.000E0"); // Formattazione scientifica

    public MandelbrotIterativeTestApp() {
        // --- 1. Funzione e Adattatori (rimangono costanti) ---
        final int MAX_ITERATIONS = 500;
        this.mandelbrotFunction = new MandelbrotFunction(MAX_ITERATIONS);
        this.domainAdapter = Complex::new;
        
        // Adattatore Colore (implementato con classe anonima)
        this.colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Real r) {
            	double value = r.getValue();
                if (value == 0) return Color.BLACK; 
                if (value >= MAX_ITERATIONS) return Color.BLACK; 
                float hue = (float) (value / MAX_ITERATIONS);
                hue = (float) Math.sqrt(hue); 
                float brightness = hue; 
                return Color.getHSBColor(0.6f, 1.0f, brightness);
            }
        };

        // --- 2. Setup Grafico Iniziale ---
        int width = 1200;
        int height = 800;
        this.renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Mandelbrot Set Interactive Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        // --- 3. Inizializzazione Viewport e Controller ---
        Viewport initialViewport = new Viewport(-2.0, 1.0, -1.5, 1.5, width, height);
        this.viewportController = new ViewportController(initialViewport, this);
        
        // Collega i listener: mouse e tastiera
        renderer.addMouseListener(viewportController);
        renderer.addMouseMotionListener(viewportController);
        renderer.addMouseWheelListener(viewportController);
        renderer.addKeyListener(viewportController); 

        // Assicurati che il Canvas possa ricevere il focus per gli eventi tastiera
        renderer.setFocusable(true);
        renderer.requestFocusInWindow();
        renderer.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                renderer.requestFocusInWindow();
            }
        });

        // --- 4. Primo Rendering ---
        renderScene(initialViewport);
    }
    
    // Implementazione del metodo handler per aggiornamento Viewport
    @Override
    public void onViewportUpdated(Viewport newViewport) {
        renderScene(newViewport);
    }

    // Implementazione del metodo handler per disegno temporaneo (rettangolo di selezione)
    @Override
    public void onTemporaryDraw(int x, int y, int width, int height) {
        // Ridisegna la scena base con la viewport corrente (per cancellare il rettangolo precedente)
        renderScene(viewportController.getCurrentViewport()); 

        // Disegna il nuovo rettangolo di selezione trasparente SOPRA la scena
        renderer.setColor(selectionColor);
        renderer.fillRect(x, y, width, height);
        
        renderer.flush(); // Mostra il buffer
    }

    // Metodo centralizzato per il rendering di tutta la scena
    private void renderScene(Viewport viewport) {
        renderer.clear(Color.WHITE);
        
        // Disegna il frattale
        FunctionPlotter2D.plotFunction(
            renderer, viewport, mandelbrotFunction, domainAdapter, colorMapper
        );
        
        // Disegna gli assi sopra il frattale
        CartesianAxisPlotter2D.plotAxes(renderer, viewport, Color.DARK_GRAY);

        // Calcola l'ampiezza del range matematico
        double rangeX = viewport.maxX - viewport.minX;
        double rangeY = viewport.maxY - viewport.minY;

        String infoText1 = "Range X: [" + DF.format(viewport.minX) + ", " + DF.format(viewport.maxX) + "] (Ampiezza: " + DF.format(rangeX) + ")";
        String infoText2 = "Range Y: [" + DF.format(viewport.minY) + ", " + DF.format(viewport.maxY) + "] (Ampiezza: " + DF.format(rangeY) + ")";
        String infoText3 = "Zoom: " + DF.format(3.0 / rangeX) + "x"; // Calcolo dello zoom relativo all'ampiezza iniziale di 3.0

        // Disegna il testo in overlay (coordinate pixel fisse)
        renderer.drawOverlayText(infoText1, 10, 20, Color.RED);
        renderer.drawOverlayText(infoText2, 10, 35, Color.RED);
        renderer.drawOverlayText(infoText3, 10, 50, Color.RED);

        renderer.flush();
    }

    public static void main(String[] args) {
        // Avvia l'applicazione Swing nel thread di eventi dedicato
        SwingUtilities.invokeLater(MandelbrotIterativeTestApp::new);
    }
}
