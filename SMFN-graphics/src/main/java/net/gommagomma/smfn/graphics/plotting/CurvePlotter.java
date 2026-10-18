package net.gommagomma.smfn.graphics.plotting;

import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;


/**
 * Utility per plottare funzioni matematiche 1D (y=f(x)) campionando in base ai pixel X.
 * Utilizza adattatori funzionali per convertire i tipi matematici generici (D, C) 
 * in valori double plottabili. Un valore non finito (NaN o infinito) interrompe la curva.
 */
public class CurvePlotter
{
	private CurvePlotter() {}


    /**
     * Disegna una funzione 1D campionando lo spazio dei pixel X.
     * 
     * @param <D> Il tipo di input (dominio), vincolato a AlgebraicElement.
     * @param <C> Il tipo di output (codominio), vincolato a AlgebraicElement.
     * @param renderer Il renderer da usare.
     * @param viewport La viewport che definisce la mappatura.
     * @param function La funzione matematica f(D) = C.
     * @param domainAdapter Adattatore da double a D (es. Double -> Real).
     * @param codomainAdapter Adattatore da C a double (es. Real -> Double).
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotFunction(Renderer renderer, Viewport2D viewport, Mapping<D, C> function, Function<Double, D> domainAdapter, Function<C, Double> codomainAdapter) 
    {
        int width = renderer.getWidth();
        boolean hasPrevious = false;
        int lastPixelX = 0;
        int lastPixelY = 0;

        for (int pixelX = 0; pixelX < width; pixelX++)
        {
            // Converte coordinata pixel X in coordinata matematica double X
            double mathX = viewport.convertPixelXToMathX(pixelX);

            // Adatta il double al tipo di dominio D
            D inputElement = domainAdapter.apply(mathX);

            // Valutazione matematica
            C outputElement = function.apply(inputElement);

            // Estrae il double dal codominio C
            double mathY = codomainAdapter.apply(outputElement);

            // Un valore non finito (un polo, il logaritmo di un negativo) non ha posizione: la curva
            // si interrompe, senza una linea verso un punto inventato.
            if (!Double.isFinite(mathY)) {
                hasPrevious = false;
                continue;
            }

            // Converti math Y in pixel Y
            int pixelY = viewport.convertMathYToPixelY(mathY);

            // Disegna
            if (hasPrevious && Math.abs(pixelY - lastPixelY) < renderer.getHeight() * 2) {
            	renderer.drawLine(lastPixelX, lastPixelY, pixelX, pixelY);
            }

            lastPixelX = pixelX;
            lastPixelY = pixelY;
            hasPrevious = true;
        }
    }
}
