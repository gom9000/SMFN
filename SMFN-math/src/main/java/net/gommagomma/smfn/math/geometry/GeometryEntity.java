package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.analysis.core.MathFunction;

/**
 * Interfaccia base che caratterizza un ente geometrico.
 * Estende MathFunction per standardizzare la rappresentazione implicita/esplicita.
 * 
 * @param <D> Il tipo di input (dominio, tipicamente RealVector).
 * @param <C> Il tipo di output (codominio, tipicamente Real).
 */
public interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>> 
extends MathFunction<D, C> 
{
    /**
     * Restituisce la dimensione dello spazio in cui esiste l'ente (es. 2 per cerchio, 3 per piano).
     * @return La dimensione.
     */
    int getDimension();

    /**
     * Controlla se un punto specificato si trova esattamente sull'ente geometrico.
     * @param point Il punto da controllare.
     * @return true se il punto è sull'ente (entro una tolleranza).
     */
    boolean isOnEntity(D point);

    // Potresti aggiungere:
    // AABB getBoundingBox();
    // Real getArea(); (Per enti 2D)
    // Real getVolume(); (Per enti 3D)
}
