package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;

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

    /**
     * Lo zero di una struttura approssimata e' lo zero esatto. Un valore piccolo ma non nullo e'
     * un valore valido (come divisore, come coefficiente di testa di un polinomio, come lunghezza):
     * dire se sia o meno trascurabile richiede una scala.
     */
    @Override
    default boolean isZero(K e) {
        return magnitude(e).getValue() == 0.0;
    }

    /**
     * {@code |e| <= epsilon * scale}.
     */
    @Override
    default boolean isNegligible(K e, Real scale) {
        return magnitude(e).getValue() <= epsilon() * scale.getValue();
    }
}
