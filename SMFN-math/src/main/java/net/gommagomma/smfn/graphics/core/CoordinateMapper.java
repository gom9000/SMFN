package net.gommagomma.smfn.graphics.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

/**
 * Interfaccia che definisce la strategia per la mappatura tra coordinate pixel intere
 * e un elemento algebrico continuo (es. Complex, Real, Vector).
 * 
 * Questa è una dipendenza di basso livello utilizzata dalla Viewport.
 * 
 * @param <E> Il tipo di elemento algebrico (Dominio della funzione).
 */
public interface CoordinateMapper<E extends AlgebraicElement<E>>
{
    /**
     * Mappa da coordinate pixel a un elemento algebrico, usando i dati della viewport.
     */
    E mapPixelToElement(int x, int y, Viewport<E> viewport);
}
