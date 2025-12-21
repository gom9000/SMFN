package net.gommagomma.smfn.math.algebra.structures;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.polynomial.SemiringPolynomial;

/**
 * Rappresenta la struttura di Semianello dei polinomi S[x].
 * Utilizzata per coefficienti che non hanno l'inverso additivo (es. Natural).
 */
public class SemiringPolynomialRing<K extends SemiringElement<K>> 
    implements Semiring<SemiringPolynomial<K>> {

    private final Semiring<K> scalarSemiring;

    public SemiringPolynomialRing(Semiring<K> scalarSemiring) {
        this.scalarSemiring = scalarSemiring;
    }

    // --- Implementazione NumericFactory (via Semiring) ---

    @Override
    public SemiringPolynomial<K> zero() {
        return additiveIdentity();
    }

    @Override
    public SemiringPolynomial<K> one() {
        return multiplicativeIdentity();
    }

    @Override
    public SemiringPolynomial<K> of(double value) {
        return constant(scalarSemiring.of(value));
    }

    @Override
    public SemiringPolynomial<K> of(long value) {
        return constant(scalarSemiring.of(value));
    }

    @Override
    public SemiringPolynomial<K> of(int value) {
        return constant(scalarSemiring.of(value));
    }

    // --- Metodi AlgebraicStructure ---

    @Override
    public String getName() {
        return "Polynomial Semiring over " + scalarSemiring.getName();
    }

    @Override
    public boolean contains(SemiringPolynomial<K> p) {
        return p != null;
    }

    // --- Metodi Additive/Multiplicative Monoid ---

    @Override
    public SemiringPolynomial<K> additiveIdentity() {
        return new SemiringPolynomial<>(new TreeMap<>(), scalarSemiring);
    }

    @Override
    public SemiringPolynomial<K> multiplicativeIdentity() {
        return constant(scalarSemiring.one());
    }

    // --- Factory Methods specifici ---

    /** Crea un polinomio costante di grado 0 */
    public SemiringPolynomial<K> constant(K scalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        if (!scalar.isMathematicallyEqualTo(scalarSemiring.zero())) {
            map.put(0, scalar);
        }
        return new SemiringPolynomial<>(map, scalarSemiring);
    }

    /** Crea un polinomio da una serie di coefficienti (a0, a1, ...) */
    @SafeVarargs
    public final SemiringPolynomial<K> of(K... coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null && !coeffs[i].isMathematicallyEqualTo(scalarSemiring.zero())) {
                map.put(i, coeffs[i]);
            }
        }
        return new SemiringPolynomial<>(map, scalarSemiring);
    }
}