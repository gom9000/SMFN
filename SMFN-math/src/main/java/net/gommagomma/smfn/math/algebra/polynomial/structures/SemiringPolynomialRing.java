package net.gommagomma.smfn.math.algebra.polynomial.structures;

import java.util.TreeMap;
import java.util.Objects;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.polynomial.SemiringPolynomial;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractPolynomialRing;

public final class SemiringPolynomialRing<K extends SemiringElement<K>> 
extends AbstractPolynomialRing<K, SemiringPolynomial<K>>
implements Semiring<SemiringPolynomial<K>>
{
    public SemiringPolynomialRing(Semiring<K> scalarSemiring) {
        super(Objects.requireNonNull(scalarSemiring));
    }

    // --- Implementazione NumericFactory (via Semiring) ---

    @Override public SemiringPolynomial<K> zero() { return additiveIdentity(); }
    @Override public SemiringPolynomial<K> one() { return multiplicativeIdentity(); }

    @Override
    public SemiringPolynomial<K> of(double value) {
        return constant(scalarStructure.of(value));
    }

    @Override
    public SemiringPolynomial<K> of(long value) {
        return constant(scalarStructure.of(value));
    }

    @Override
    public SemiringPolynomial<K> of(int value) {
        return constant(scalarStructure.of(value));
    }

    // --- Metodi AlgebraicStructure ---

    @Override
    public String getName() {
        return "Polynomial Semiring over " + scalarStructure.getName();
    }

    @Override
    public boolean contains(SemiringPolynomial<K> p) {
        return p != null; // Si potrebbe aggiungere il controllo sulla struttura interna se necessario
    }

    // --- Metodi Additive/Multiplicative Monoid ---

    @Override
    public SemiringPolynomial<K> additiveIdentity() {
        return new SemiringPolynomial<>(new TreeMap<>(), scalarStructure);
    }

    @Override
    public SemiringPolynomial<K> multiplicativeIdentity() {
        return constant(scalarStructure.one());
    }

    // --- Factory Methods specifici ---

    public SemiringPolynomial<K> constant(K scalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        if (!scalar.isMathematicallyEqualTo(scalarStructure.zero())) {
            map.put(0, scalar);
        }
        return new SemiringPolynomial<>(map, scalarStructure);
    }

    @SafeVarargs
    public final SemiringPolynomial<K> of(K... coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null && !coeffs[i].isMathematicallyEqualTo(scalarStructure.zero())) {
                map.put(i, coeffs[i]);
            }
        }
        return new SemiringPolynomial<>(map, scalarStructure);
    }
}