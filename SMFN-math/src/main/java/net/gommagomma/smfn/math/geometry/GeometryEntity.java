package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.Morphism;

/**
 * Interfaccia base che caratterizza un ente geometrico.
 * Rappresenta l'ente tramite la sua funzione implicita f(P) = 0.
 * * @param <D> Il tipo di input (es. Vector).
 * @param <C> Il tipo di output (es. Real o Scalar).
 */
public interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
extends Morphism<D, C>
{
    /**
     * Dimensione dello spazio ambiente (es. 3 per R^3).
     */
    int getAmbientDimension();

    /**
     * Dimensione intrinseca dell'ente (es. 1 per una curva, 2 per una superficie).
     */
    int getEntityDimension();

    /**
     * Controlla se un punto appartiene all'ente.
     * Implementazione di default basata sulla valutazione della funzione implicita.
     */
    boolean isOnEntity(D point);

    /**
     * Restituisce il valore della funzione implicita nel punto (es. x^2 + y^2 - r^2).
     */
    @Override
    default C apply(D point) {
        return implicitFunctionAt(point);
    }

    C implicitFunctionAt(D point);
}
