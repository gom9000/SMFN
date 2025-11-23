package net.gommagomma.smfn.graphics.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

/**
 * Rappresenta l'area visibile (viewport). 
 * Detiene lo stato delle dimensioni in pixel e dei limiti matematici.
 * Utilizza internamente una CoordinateMapper per eseguire il mapping.
 * 
 * @param <E> Il tipo di elemento algebrico visualizzato.
 */
public interface Viewport<E extends AlgebraicElement<E>>
{    
    // Metodi per le dimensioni in pixel (usati dal Renderer e dal Mapper)
    int getPixelWidth();
    int getPixelHeight();

    // Metodi per i limiti matematici (usati dal Mapper)
    double getMinX();
    double getMaxX();
    double getMinY();
    double getMaxY();

    /**
     * Metodo pubblico principale: ottiene il punto matematico corrispondente al pixel (x, y).
     */
    E mapPixelToElement(int x, int y);

    /**
     * Imposta l'area matematica visualizzata, mantenendo l'aspect ratio dei pixel
     * e ritagliando (cropping) l'area per farla stare nella viewport.
     */
    void setMathematicalArea(double minX, double maxX, double minY, double maxY);
}
