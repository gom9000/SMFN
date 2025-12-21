package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

public final class GeneralPolynomial<K extends RingElement<K>> 
extends AbstractRingPolynomial<K, GeneralPolynomial<K>>
{
    public GeneralPolynomial(TreeMap<Integer, K> coefficients, Semiring<K> structure) {
        super(coefficients, structure);
    }

    @Override
    protected GeneralPolynomial<K> create(TreeMap<Integer, K> newCoeffs) {
        return new GeneralPolynomial<>(newCoeffs, structure);
    }

    @Override
    public GeneralPolynomial<K> copy() {
        TreeMap<Integer, K> clonedCoeffs = new TreeMap<>();
        this.coefficients.forEach((deg, val) -> clonedCoeffs.put(deg, val.copy()));
        return new GeneralPolynomial<>(clonedCoeffs, structure);
    }

    @Override
    public GeneralPolynomial<K> getOne() {
        TreeMap<Integer, K> map = new TreeMap<>();
        map.put(0, structure.one());
        return create(map);
    }
}