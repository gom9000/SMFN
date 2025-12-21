package net.gommagomma.smfn.math.algebra.structures;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.polynomial.GeneralPolynomial;

/**
 * Rappresenta la struttura algebrica dell'anello dei polinomi R[x] 
 * dove R è un anello non necessariamente commutativo (es. Matrici).
 */
public class GeneralPolynomialRing<K extends RingElement<K>> 
    implements Ring<GeneralPolynomial<K>> {

    private final Ring<K> scalarRing;

    public GeneralPolynomialRing(Ring<K> scalarRing) {
        this.scalarRing = scalarRing;
    }

    // --- Implementazione NumericFactory ---

    @Override
    public GeneralPolynomial<K> zero() {
        return additiveIdentity();
    }

    @Override
    public GeneralPolynomial<K> one() {
        return multiplicativeIdentity();
    }

    @Override
    public GeneralPolynomial<K> of(double value) {
        return constant(scalarRing.of(value));
    }

    @Override
    public GeneralPolynomial<K> of(long value) {
        return constant(scalarRing.of(value));
    }

    @Override
    public GeneralPolynomial<K> of(int value) {
        return constant(scalarRing.of(value));
    }

    // --- Metodi dell'interfaccia Ring ---

    @Override
    public String getName() {
        return "General Polynomial Ring over " + scalarRing.getName();
    }

    @Override
    public boolean contains(GeneralPolynomial<K> p) {
        // Un polinomio appartiene a questo anello se la sua 'structure' 
        // è compatibile con scalarRing.
        return p != null; 
    }

    @Override
    public GeneralPolynomial<K> additiveIdentity() {
        return new GeneralPolynomial<>(new TreeMap<>(), scalarRing);
    }

    @Override
    public GeneralPolynomial<K> multiplicativeIdentity() {
        return constant(scalarRing.one());
    }

    // --- Factory Methods specifici ---

    /** Crea un polinomio costante di grado 0 */
    public GeneralPolynomial<K> constant(K scalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        if (!scalar.isMathematicallyEqualTo(scalarRing.zero())) {
            map.put(0, scalar);
        }
        return new GeneralPolynomial<>(map, scalarRing);
    }

    /** Crea un polinomio da un array di coefficienti scalari */
    @SafeVarargs
    public final GeneralPolynomial<K> of(K... coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null && !coeffs[i].isMathematicallyEqualTo(scalarRing.zero())) {
                map.put(i, coeffs[i]);
            }
        }
        return new GeneralPolynomial<>(map, scalarRing);
    }
}