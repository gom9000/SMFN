package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.analysis.core.MathFunction;

import java.util.function.Function;

/**
 * Un motore che esegue il plotting di una funzione matematica su una Viewport,
 * utilizzando un ColorMapper e un Renderer specifici.
 */
public class FunctionPlotter<I extends AlgebraicElement<I>, O extends AlgebraicElement<O>>
{
    /**
     * Esegue il processo di plotting iterando su ogni pixel della Viewport,
     * calcolando il valore della funzione in quel punto e colorando il pixel
     * in base al risultato.
     *
     * @param function La funzione matematica da valutare (es. MandelbrotFunction).
     * @param viewport L'area di visualizzazione che mappa pixel e coordinate matematiche.
     * @param colorMapper Il mapper che converte un valore double in un colore RGB.
     * @param renderer Il dispositivo di output che disegna i pixel.
     * @param outputToDoubleConverter Una funzione che estrae il valore double
     *                                dal risultato O (es. da un oggetto Real).
     */
    public void plot(
        MathFunction<I, O> function,
        Viewport<I> viewport,
        ColorMapper colorMapper,
        Renderer renderer,
        Function<O, Double> outputToDoubleConverter) 
    {
        final int width = viewport.getPixelWidth();
        final int height = viewport.getPixelHeight();

        // Ciclo su tutti i pixel dell'area di rendering
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {    
                // 1. Mappa il pixel (x, y) a un punto matematico (I, es. Complex)
                I inputElement = viewport.mapPixelToElement(x, y);
                
                // 2. Esegui la funzione matematica (es. calcola le iterazioni Mandelbrot)
                O outputValue = function.evaluate(inputElement);
                
                // 3. Converti il risultato O in un double usando la funzione fornita
                double valueForColoring = outputToDoubleConverter.apply(outputValue); 
                
                // 4. Mappa il valore risultante in un colore RGB
                int rgbColor = colorMapper.toRGB(valueForColoring);
                
                // 5. Disegna il pixel usando il renderer
                renderer.setPixel(x, y, rgbColor);
            }
        }
    }
}
