package net.gommagomma.smfn.math.algebra.polynomial.structures;

import java.util.TreeMap;
import java.util.Objects;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.polynomial.CommutativePolynomial;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractPolynomialRing;

/**
 * Rappresenta la struttura di Anello Commutativo dei polinomi C[x].
 * Utilizzata quando i coefficienti commutano nel prodotto (es. Integers, Reals).
 */
public final class CommutativePolynomialRing<K extends CommutativeRingElement<K>> 
    extends AbstractPolynomialRing<K, CommutativePolynomial<K>>
    implements CommutativeRing<CommutativePolynomial<K>> {

    public CommutativePolynomialRing(CommutativeRing<K> scalarRing) {
        super(Objects.requireNonNull(scalarRing));
    }

    /**
     * Specializzazione del ritorno: restituisce un CommutativeRing.
     */
    @Override
    public CommutativeRing<K> getScalarStructure() {
        return (CommutativeRing<K>) this.scalarStructure;
    }

    // --- Implementazione NumericFactory ---

    @Override public CommutativePolynomial<K> zero() { return additiveIdentity(); }
    @Override public CommutativePolynomial<K> one() { return multiplicativeIdentity(); }

    @Override
    public CommutativePolynomial<K> of(double value) {
        return constant(getScalarStructure().of(value));
    }

    @Override
    public CommutativePolynomial<K> of(long value) {
        return constant(getScalarStructure().of(value));
    }

    @Override
    public CommutativePolynomial<K> of(int value) {
        return constant(getScalarStructure().of(value));
    }

    // --- Metodi AlgebraicStructure ---

    @Override
    public String getName() {
        return "Commutative Polynomial Ring over " + getScalarStructure().getName();
    }

    @Override
    public boolean contains(CommutativePolynomial<K> p) {
        return p != null;
    }

    // --- Metodi Additive/Multiplicative Monoid ---

    @Override
    public CommutativePolynomial<K> additiveIdentity() {
        return new CommutativePolynomial<>(new TreeMap<>(), getScalarStructure());
    }

    @Override
    public CommutativePolynomial<K> multiplicativeIdentity() {
        return constant(getScalarStructure().one());
    }

    // --- Factory Methods ---

    public CommutativePolynomial<K> constant(K scalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        if (!scalar.isMathematicallyEqualTo(getScalarStructure().zero())) {
            map.put(0, scalar);
        }
        return new CommutativePolynomial<>(map, getScalarStructure());
    }

    @SafeVarargs
    public final CommutativePolynomial<K> of(K... coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        K zeroK = getScalarStructure().zero();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null && !coeffs[i].isMathematicallyEqualTo(zeroK)) {
                map.put(i, coeffs[i]);
            }
        }
        return new CommutativePolynomial<>(map, getScalarStructure());
    }
}