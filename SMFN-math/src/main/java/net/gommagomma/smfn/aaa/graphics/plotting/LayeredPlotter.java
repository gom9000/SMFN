package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.Renderable;
import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

import java.util.List;

/**
 * Un motore che esegue il plotting di una lista di oggetti Renderable su una Viewport.
 * I Renderable vengono eseguiti in ordine sequenziale (come layers), dal primo all'ultimo.
 * Il primo layer che restituisce un colore non nullo determina il colore del pixel.
 */
public class LayeredPlotter<I extends AlgebraicElement<I>>
{
    /**
     * Esegue il processo di plotting iterando su ogni pixel della Viewport e 
     * sovrapponendo i layers Renderable.
     *
     * @param renderables La lista di oggetti disegnabili (dal basso verso l'alto).
     * @param viewport L'area di visualizzazione che mappa pixel e coordinate matematiche.
     * @param renderer Il dispositivo di output che disegna i pixel.
     */
    public void plot(
        List<Renderable<I>> renderables,
        Viewport<I> viewport,
        Renderer renderer) 
    {
        final int width = viewport.getPixelWidth();
        final int height = viewport.getPixelHeight();

        // Ciclo su tutti i pixel dell'area di rendering
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                
                // 1. Mappa il pixel (x, y) a un punto matematico (I, es. Complex)
                I inputElement = viewport.mapPixelToElement(x, y);
                
                // 2. Itera sui layers e trova il primo che disegna il pixel
                Integer rgbColor = null;
                for (Renderable<I> renderable : renderables) {
                    rgbColor = renderable.renderPixel(inputElement);
                    if (rgbColor != null) {
                        break; // Trovato il colore, interrompi l'iterazione dei layers
                    }
                }
                
                // 3. Disegna il pixel (se è stato trovato un colore)
                if (rgbColor != null) {
                    renderer.setPixel(x, y, rgbColor);
                }
                // Se rgbColor è null, il pixel rimane vuoto (assumendo che il renderer inizializzi lo sfondo)
            }
        }
    }
}
