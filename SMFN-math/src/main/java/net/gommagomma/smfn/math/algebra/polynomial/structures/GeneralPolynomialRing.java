package net.gommagomma.smfn.math.algebra.polynomial.structures;

import java.util.TreeMap;
import java.util.Objects;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.polynomial.GeneralPolynomial;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractPolynomialRing;

/**
 * Rappresenta la struttura di Anello dei polinomi R[x].
 * Gestisce coefficienti che possiedono l'inverso additivo (RingElement).
 */
public final class GeneralPolynomialRing<K extends RingElement<K>> 
extends AbstractPolynomialRing<K, GeneralPolynomial<K>>
implements Ring<GeneralPolynomial<K>>
{
    public GeneralPolynomialRing(Ring<K> scalarRing) {
        super(Objects.requireNonNull(scalarRing));
    }

    /**
     * Specializzazione del tipo di ritorno (Covariant Return Type).
     * Restituisce un Ring invece di un generico Semiring.
     */
    @Override
    public Ring<K> getScalarStructure() {
        return (Ring<K>) this.scalarStructure;
    }

    // --- Implementazione NumericFactory (via Ring/Semiring) ---

    @Override public GeneralPolynomial<K> zero() { return additiveIdentity(); }
    @Override public GeneralPolynomial<K> one() { return multiplicativeIdentity(); }

    @Override
    public GeneralPolynomial<K> of(double value) {
        return constant(getScalarStructure().of(value));
    }

    @Override
    public GeneralPolynomial<K> of(long value) {
        return constant(getScalarStructure().of(value));
    }

    @Override
    public GeneralPolynomial<K> of(int value) {
        return constant(getScalarStructure().of(value));
    }

    // --- Metodi AlgebraicStructure ---

    @Override
    public String getName() {
        return "Polynomial Ring over " + getScalarStructure().getName();
    }

    @Override
    public boolean contains(GeneralPolynomial<K> p) {
        return p != null;
    }

    // --- Metodi Additive Monoid / Group ---

    @Override
    public GeneralPolynomial<K> additiveIdentity() {
        return new GeneralPolynomial<>(new TreeMap<>(), getScalarStructure());
    }


    //@Override
    public GeneralPolynomial<K> subtract(GeneralPolynomial<K> p1, GeneralPolynomial<K> p2) {
        // Implementazione standard p1 + (-p2)
        return p1.add(p2.negate());
    }

    // --- Metodi Multiplicative Monoid ---

    @Override
    public GeneralPolynomial<K> multiplicativeIdentity() {
        return constant(getScalarStructure().one());
    }

    // --- Factory Methods specifici ---

    public GeneralPolynomial<K> constant(K scalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        if (!scalar.isMathematicallyEqualTo(getScalarStructure().zero())) {
            map.put(0, scalar);
        }
        return new GeneralPolynomial<>(map, getScalarStructure());
    }

    @SafeVarargs
    public final GeneralPolynomial<K> of(K... coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        K zeroK = getScalarStructure().zero();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null && !coeffs[i].isMathematicallyEqualTo(zeroK)) {
                map.put(i, coeffs[i]);
            }
        }
        return new GeneralPolynomial<>(map, getScalarStructure());
    }
}