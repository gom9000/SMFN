package net.gommagomma.smfn.math.algebra.polynomial.core;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

/**
 * Estensione per polinomi i cui coefficienti appartengono a un Anello.
 * Introduce l'operazione di negazione e sottrazione.
 */
public abstract class AbstractRingPolynomial<K extends RingElement<K>, P extends AbstractRingPolynomial<K, P>> 
extends AbstractPolynomial<K, P> 
implements RingElement<P>
{
    protected AbstractRingPolynomial(TreeMap<Integer, K> coefficients, Semiring<K> structure) {
        super(coefficients, structure);
    }

    @Override
    public P negate() {
        TreeMap<Integer, K> negatedMap = new TreeMap<>();
        this.coefficients.forEach((deg, val) -> negatedMap.put(deg, val.negate()));
        return create(negatedMap);
    }

    @Override
    public P subtract(P other) {
        return this.add(other.negate());
    }
}