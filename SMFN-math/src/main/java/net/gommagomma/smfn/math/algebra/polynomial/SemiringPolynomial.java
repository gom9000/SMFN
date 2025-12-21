package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractPolynomial;

/**
 * Implementazione finale e immutabile per polinomi su semianelli.
 * Utilizzata quando i coefficienti non supportano la sottrazione (es. Naturali).
 */
public final class SemiringPolynomial<K extends SemiringElement<K>> 
extends AbstractPolynomial<K, SemiringPolynomial<K>>
{
    public SemiringPolynomial(TreeMap<Integer, K> coefficients, Semiring<K> structure) {
        super(coefficients, structure);
    }

    @Override
    protected SemiringPolynomial<K> create(TreeMap<Integer, K> newCoeffs) {
        return new SemiringPolynomial<>(newCoeffs, structure);
    }

    @Override
    public SemiringPolynomial<K> copy() {
        TreeMap<Integer, K> clonedCoeffs = new TreeMap<>();
        this.coefficients.forEach((deg, val) -> clonedCoeffs.put(deg, val.copy()));
        return new SemiringPolynomial<>(clonedCoeffs, structure);
    }

    @Override
    public SemiringPolynomial<K> getOne() {
        TreeMap<Integer, K> map = new TreeMap<>();
        map.put(0, structure.one());
        return create(map);
    }
}