package net.gommagomma.smfn.client;

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
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.analysis.fractals.MandelbrotFunction;


// L'app implementa l'interfaccia handler per gestire gli aggiornamenti della viewport
public class MandelbrotFunctionIterativeClient
implements ViewportController.ViewportUpdateHandler
{
    private final SwingRenderer2D renderer;
    private final MandelbrotFunction mandelbrotFunction;
    private final BiFunction<Double, Double, Complex> domainAdapter;
    private final ColorMapper<Natural> baseColorMapper; // Mapper base (richiede un setter per le iterazioni)
    private ViewportController viewportController;
    private final Color selectionColor = new Color(0, 0, 255, 100); // Blu semi-trasparente
    private static final DecimalFormat DF = new DecimalFormat("0.000E0"); // Formattazione scientifica

    // --- Parametri per l'Iterazione Dinamica ---
    private static final int BASE_ITERATIONS = 100; // I_base: Iterazioni minime
    private static final double SCALE_FACTOR = 300.0; // C: Fattore di tuning per lo zoom
    private int currentMaxIterations = BASE_ITERATIONS;


    public MandelbrotFunctionIterativeClient() {
        // --- 1. Funzione e Adattatori (rimangono costanti) ---
        // Inizializziamo con un valore base, verrà aggiornato subito dal rendering iniziale
        this.mandelbrotFunction = new MandelbrotFunction(BASE_ITERATIONS);
        this.domainAdapter = Complex::new;
        
        // Adattatore Colore: L'istanza deve essere mantenuta per essere aggiornata con le nuove MAX_ITERATIONS
        this.baseColorMapper = createDynamicColorMapper();

        // --- 2. Setup Grafico Iniziale ---
        int width = 1600;
        int height = 1200;
        this.renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Mandelbrot Set Interactive Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        // --- 3. Inizializzazione Viewport e Controller ---
        // Area iniziale: [-2.0, 1.0] x [-1.5, 1.5]
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
    
    /**
     * Crea un ColorMapper che utilizza il valore corrente di currentMaxIterations
     */
    private ColorMapper<Natural> createDynamicColorMapper() {
        return new ColorMapper<>() {
            @Override
            public Color map(Natural r) {
                double value = r.getValue();
                // Usa currentMaxIterations che è aggiornato dal renderScene
                if (value == 0) return Color.BLACK; 
                if (value >= currentMaxIterations) return Color.BLACK; 
                
                // Mappatura del colore standard per i frattali (basata sulla radice quadrata)
                float hue = (float) (value / currentMaxIterations);
                hue = (float) Math.sqrt(hue); 
                
                return Color.getHSBColor(0.6f, 1.0f, hue);
            }
        };
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
    }

    // Metodo centralizzato per il rendering di tutta la scena
    private void renderScene(Viewport viewport) {
        // --- Logica delle Iterazioni Dinamiche ---
        double rangeX = viewport.maxX - viewport.minX;
        
        // Calcola log_2(1/S)
        double logFactor = (rangeX > 0) ? Math.log(3.0 / rangeX) / Math.log(2.0) : 0; // 3.0 è l'ampiezza iniziale (1.0 - (-2.0))
        
        // Applica la formula: I_base + log_2(1/S) * C. Max è per evitare valori negativi o troppo bassi.
        this.currentMaxIterations = (int) Math.max(
            BASE_ITERATIONS,
            BASE_ITERATIONS + logFactor * SCALE_FACTOR
        );
        
        // La MandelbrotFunction deve avere un setter per aggiornare MAX_ITERATIONS
        this.mandelbrotFunction.setMaxIterations(this.currentMaxIterations); 
        
        // --- Rendering ---
        
        renderer.startDrawing();

        renderer.clear(Color.WHITE);
        
        // Disegna il frattale
        FunctionPlotter2D.plotFunction(
            renderer, viewport, mandelbrotFunction, domainAdapter, baseColorMapper
        );
        
        // Disegna gli assi sopra il frattale
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        // Calcola l'ampiezza del range matematico
        double rangeY = viewport.maxY - viewport.minY;

        String infoText1 = "Range X: [" + DF.format(viewport.minX) + ", " + DF.format(viewport.maxX) + "] (Ampiezza: " + DF.format(rangeX) + ")";
        String infoText2 = "Range Y: [" + DF.format(viewport.minY) + ", " + DF.format(viewport.maxY) + "] (Ampiezza: " + DF.format(rangeY) + ")";
        String infoText3 = "Zoom: " + DF.format(3.0 / rangeX) + "x"; 
        String infoText4 = "Max Iterations: " + this.currentMaxIterations;

        // Disegna il testo in overlay (coordinate pixel fisse)
        renderer.drawOverlayText(infoText1, 10, 20, Color.RED);
        renderer.drawOverlayText(infoText2, 10, 35, Color.RED);
        renderer.drawOverlayText(infoText3, 10, 50, Color.RED);
        renderer.drawOverlayText(infoText4, 10, 65, Color.RED);

        renderer.endDrawingAndFlush();
    }

    public static void main(String[] args) {
        // Avvia l'applicazione Swing nel thread di eventi dedicato
        SwingUtilities.invokeLater(MandelbrotFunctionIterativeClient::new);
    }
}