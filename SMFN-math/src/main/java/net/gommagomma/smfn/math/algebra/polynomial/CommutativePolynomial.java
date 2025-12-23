package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.TreeMap;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractRingPolynomial;

public final class CommutativePolynomial<K extends CommutativeRingElement<K>> 
extends AbstractRingPolynomial<K, CommutativePolynomial<K>> 
implements CommutativeRingElement<CommutativePolynomial<K>>
{
    public CommutativePolynomial(TreeMap<Integer, K> coefficients, Semiring<K> structure) {
        super(coefficients, structure);
    }

    @Override
    protected CommutativePolynomial<K> create(TreeMap<Integer, K> newCoeffs) {
        return new CommutativePolynomial<>(newCoeffs, structure);
    }

    @Override
    public CommutativePolynomial<K> copy() {
        TreeMap<Integer, K> clonedCoeffs = new TreeMap<>();
        this.coefficients.forEach((deg, val) -> clonedCoeffs.put(deg, val.copy()));
        return new CommutativePolynomial<>(clonedCoeffs, structure);
    }

    @Override
    public CommutativePolynomial<K> getOne() {
        TreeMap<Integer, K> map = new TreeMap<>();
        map.put(0, structure.one());
        return create(map);
    }
}