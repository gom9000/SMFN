package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta una struttura algebrica basata su elementi approssimati 
 * (es. in virgola mobile), gestendo tolleranze numeriche.
 *
 * @param <K> il tipo degli elementi scalari approssimati
 */
public interface ApproximateStructure<K extends ApproximateElement<K>>
extends ScalarStructure<K>
{
    @Override
    default boolean isExact() { return false; }

    /**
     * Restituisce la tolleranza (epsilon) utilizzata per i confronti 
     * e le valutazioni numeriche all'interno di questa struttura.
     * 
     * @return il valore di tolleranza epsilon
     */
    double epsilon();
}
