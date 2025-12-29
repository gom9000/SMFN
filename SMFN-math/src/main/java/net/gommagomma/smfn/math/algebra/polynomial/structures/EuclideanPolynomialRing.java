package net.gommagomma.smfn.math.algebra.polynomial.structures;

import java.util.TreeMap;
import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.algorithms.AlgebraicAlgorithms;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.polynomial.EuclideanPolynomial;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractPolynomialRing;

/**
 * Rappresenta il Dominio Euclideo dei polinomi K[x] dove K è un Campo.
 */
public final class EuclideanPolynomialRing<K extends FieldElement<K>> 
extends AbstractPolynomialRing<K, EuclideanPolynomial<K>>
implements EuclideanDomain<EuclideanPolynomial<K>, Natural> 
{
    public EuclideanPolynomialRing(Field<K> scalarField) {
        super(Objects.requireNonNull(scalarField));
    }

    @Override
    public Field<K> getScalarStructure() {
        return (Field<K>) this.scalarStructure;
    }

    // --- Implementazione EuclideanDomain ---

    @Override
    public EuclideanPolynomial<K> remainder(EuclideanPolynomial<K> a, EuclideanPolynomial<K> b) {
        return a.remainder(b);
    }

    @Override
    public EuclideanPolynomial<K> quotient(EuclideanPolynomial<K> a, EuclideanPolynomial<K> b) {
        return a.quotient(b);
    }

    public EuclideanPolynomial<K> gcd(EuclideanPolynomial<K> a, EuclideanPolynomial<K> b) {
        return AlgebraicAlgorithms.gcd(a, b);
    }

    // --- Implementazione NumericFactory ---

    @Override public EuclideanPolynomial<K> zero() { return additiveIdentity(); }
    @Override public EuclideanPolynomial<K> one() { return multiplicativeIdentity(); }

    @Override
    public EuclideanPolynomial<K> of(double value) {
        return constant(getScalarStructure().of(value));
    }

    @Override
    public EuclideanPolynomial<K> of(long value) {
        return constant(getScalarStructure().of(value));
    }

    @Override
    public EuclideanPolynomial<K> of(int value) {
        return constant(getScalarStructure().of(value));
    }

    // --- Metodi AlgebraicStructure ---

    @Override
    public String getName() {
        return "Euclidean Polynomial Ring over " + getScalarStructure().getName();
    }

    @Override
    public boolean contains(EuclideanPolynomial<K> p) {
        return p != null;
    }

    @Override
    public EuclideanPolynomial<K> additiveIdentity() {
        return new EuclideanPolynomial<>(new TreeMap<>(), getScalarStructure());
    }

    @Override
    public EuclideanPolynomial<K> multiplicativeIdentity() {
        return constant(getScalarStructure().one());
    }

    // --- Factory Methods ---

    public EuclideanPolynomial<K> constant(K scalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        if (!scalar.isZero()) {
            map.put(0, scalar);
        }
        return new EuclideanPolynomial<>(map, getScalarStructure());
    }

    @SafeVarargs
    public final EuclideanPolynomial<K> of(K... coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        K zeroK = getScalarStructure().zero();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null && !coeffs[i].isMathematicallyEqualTo(zeroK)) {
                map.put(i, coeffs[i]);
            }
        }
        return new EuclideanPolynomial<>(map, getScalarStructure());
    }
}