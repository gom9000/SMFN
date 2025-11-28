package net.gommagomma.smfn.graphics.plotting;

import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.core.algebra.AlgebraicElement;
import net.gommagomma.smfn.math.core.analysis.MathFunction;


/**
 * Utility per plottare funzioni matematiche 1D (y=f(x)) campionando in base ai pixel X.
 * Utilizza adattatori funzionali per convertire i tipi matematici generici (D, C) 
 * in valori double plottabili.
 */
public class FunctionPlotter1D
{
	private FunctionPlotter1D() {}


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
    void plotFunction(Renderer renderer, Viewport viewport, MathFunction<D, C> function, Function<Double, D> domainAdapter, Function<C, Double> codomainAdapter) 
    {
        int width = renderer.getWidth();
        int lastPixelX = Integer.MIN_VALUE;
        int lastPixelY = Integer.MIN_VALUE;

        for (int pixelX = 0; pixelX < width; pixelX++)
        {
            // 1. Converti coordinata pixel X in coordinata matematica double X
            double mathX = viewport.convertPixelXToMathX(pixelX);

            // 2. Adatta il double al tipo di dominio D (es. double -> Real)
            D inputElement = domainAdapter.apply(mathX);

            // 3. Valutazione matematica (usa il modulo math)
            C outputElement = function.evaluate(inputElement);

            // 4. Estrai il double dal codominio C (es. Real -> double)
            double mathY = codomainAdapter.apply(outputElement);

            // 5. Converti math Y in pixel Y
            int pixelY = viewport.convertMathYToPixelY(mathY);

            // 6. Disegna la linea
            if (pixelX > 0 && Math.abs(pixelY - lastPixelY) < renderer.getHeight() * 2) {
            	renderer.drawLine(lastPixelX, lastPixelY, pixelX, pixelY);
            }

            lastPixelX = pixelX;
            lastPixelY = pixelY;
        }
    }
}
