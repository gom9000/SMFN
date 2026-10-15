package net.gommagomma.smfn.graphics.plotting;

import java.util.function.BiFunction;
import java.util.stream.IntStream;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;


/**
 * Utility per plottare funzioni matematiche 2D (ad es. heatmap, frattali)
 * che mappano un punto (x, y) matematico a un valore, che viene poi colorato.
 */
public class FunctionPlotter2D
{
	private FunctionPlotter2D() {}


    /**
     * Disegna una funzione 2D che richiede un dominio bidimensionale (es. il piano complesso).
     *
     * @param <D> Il tipo di input (dominio), vincolato a AlgebraicElement.
     * @param <C> Il tipo di output (codominio), vincolato a AlgebraicElement.
     * @param renderer Il renderer da usare.
     * @param viewport La viewport che definisce la mappatura.
     * @param function La funzione matematica f(D) = C.
     * @param domainAdapter Un adattatore che combina X e Y matematici in un elemento D.
     * @param colorMapper Un adattatore che mappa il risultato C in un RgbColor.
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotFunction(Renderer renderer, Viewport viewport, Mapping<D, C> function, BiFunction<Double, Double, D> domainAdapter, ColorMapper<C> colorMapper)
    {
        int width = renderer.getWidth();
        int height = renderer.getHeight();

        for (int pixelX = 0; pixelX < width; pixelX++)
        {
            for (int pixelY = 0; pixelY < height; pixelY++)
            {
                RgbColor color = computePixelColor(viewport, function, domainAdapter, colorMapper, pixelX, pixelY);

                renderer.setColor(color);
                renderer.drawPoint(pixelX, pixelY);
            }
        }
    }

    /**
     * Variante parallela che calcola il colore di ogni pixel concorrentemente su piu' thread
     * e disegna in una seconda fase strettamente sequenziale.
     */
    public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    void plotFunctionParallel(Renderer renderer, Viewport viewport, Mapping<D, C> function, BiFunction<Double, Double, D> domainAdapter, ColorMapper<C> colorMapper)
    {
        int width = renderer.getWidth();
        int height = renderer.getHeight();

        RgbColor[][] colors = new RgbColor[width][height];

        IntStream.range(0, width).parallel().forEach(pixelX ->
        {
            for (int pixelY = 0; pixelY < height; pixelY++)
            {
                colors[pixelX][pixelY] = computePixelColor(viewport, function, domainAdapter, colorMapper, pixelX, pixelY);
            }
        });

        for (int pixelX = 0; pixelX < width; pixelX++)
        {
            for (int pixelY = 0; pixelY < height; pixelY++)
            {
                renderer.setColor(colors[pixelX][pixelY]);
                renderer.drawPoint(pixelX, pixelY);
            }
        }
    }

    /**
     * Calcola il colore di un singolo pixel
     */
    private static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
    RgbColor computePixelColor(Viewport viewport, Mapping<D, C> function, BiFunction<Double, Double, D> domainAdapter, ColorMapper<C> colorMapper, int pixelX, int pixelY)
    {
        // Converte pixel in coordinate matematiche (X e Y)
        double mathX = viewport.convertPixelXToMathX(pixelX);
        double mathY = viewport.convertPixelYToMathY(pixelY);

        // Crea l'input D a partire da X e Y
        D inputElement = domainAdapter.apply(mathX, mathY);

        // Valuta la funzione
        C outputElement = function.apply(inputElement);

        // Mappa il risultato a un colore
        return colorMapper.map(outputElement);
    }
}