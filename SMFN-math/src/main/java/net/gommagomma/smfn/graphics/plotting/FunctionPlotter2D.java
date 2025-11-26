package net.gommagomma.smfn.graphics.plotting;

import java.awt.Color;
import java.util.function.BiFunction;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Renderer2D;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.analysis.core.MathFunction;

/**
 * Utility per plottare funzioni matematiche 2D (ad es. heatmap, frattali) 
 * che mappano un punto (x, y) matematico a un valore, che viene poi colorato.
 */
public class FunctionPlotter2D
{
    /**
     * Disegna una funzione 2D che richiede un dominio bidimensionale (es. il piano complesso).
     * 
     * @param renderer Il renderer 2D da usare.
     * @param viewport La viewport che definisce la mappatura.
     * @param function La funzione matematica f(D) = C.
     * @param domainAdapter Un adattatore che combina X e Y matematici in un elemento D.
     * @param colorMapper Un adattatore che mappa il risultato C in un Color AWT.
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotFunction(
            Renderer2D renderer, 
            Viewport viewport, 
            MathFunction<D, C> function,
            BiFunction<Double, Double, D> domainAdapter, // Combina (X, Y) in D
            ColorMapper<C> colorMapper)               // Mappa C in Color
    {
        int width = renderer.getWidth();
        int height = renderer.getHeight();

        for (int pixelX = 0; pixelX < width; pixelX++) {
            for (int pixelY = 0; pixelY < height; pixelY++) {
                
                // 1. Converti pixel in coordinate matematiche (X e Y)
                double mathX = viewport.convertPixelXToMathX(pixelX);
                double mathY = viewport.convertPixelYToMathY(pixelY);
                
                // 2. Crea l'input D a partire da X e Y (es. crea un Complex(x, y))
                D inputElement = domainAdapter.apply(mathX, mathY);

                // 3. Valuta la funzione (usa il modulo math)
                C outputElement = function.evaluate(inputElement);

                // 4. Mappa il risultato a un colore 
                Color color = colorMapper.map(outputElement);

                // 5. Disegna il punto (o un rettangolo 1x1)
                renderer.setColor(color);
                renderer.fillRect(pixelX, pixelY, 1, 1); 
            }
        }
    }
}
