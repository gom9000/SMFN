package net.gommagomma.smfn.math.algebra.structures;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.polynomial.CommutativePolynomial;

/**
 * Rappresenta la struttura algebrica dell'anello dei polinomi R[x].
 * Gestisce l'insieme dei polinomi commutativi su uno scalare K.
 */
public class CommutativePolynomialRing<K extends CommutativeRingElement<K>> 
    implements CommutativeRing<CommutativePolynomial<K>> {

    private final CommutativeRing<K> scalarRing;

    public CommutativePolynomialRing(CommutativeRing<K> scalarRing) {
        this.scalarRing = scalarRing;
    }

    /** Helper interno per creare un polinomio costante di grado 0 */
    private CommutativePolynomial<K> constant(K scalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        // Non aggiungiamo alla mappa se lo scalare è lo zero della struttura
        if (!scalar.isMathematicallyEqualTo(scalarRing.zero())) {
            map.put(0, scalar);
        }
        return new CommutativePolynomial<>(map, scalarRing);
    }

    @Override
    public String getName() {
        return "Polynomial Ring " + scalarRing.getName() + "[x]";
    }

    @Override
    public boolean contains(CommutativePolynomial<K> p) {
        // Un polinomio appartiene a questo anello se tutti i suoi coefficienti 
        // appartengono all'anello scalare di base.
        return p != null && p.getCoefficients().values().stream().allMatch(scalarRing::contains);
    }

    @Override
    public CommutativePolynomial<K> additiveIdentity() {
        return new CommutativePolynomial<>(new TreeMap<>(), scalarRing);
    }

    @Override
    public CommutativePolynomial<K> multiplicativeIdentity() {
        TreeMap<Integer, K> map = new TreeMap<>();
        map.put(0, scalarRing.one());
        return new CommutativePolynomial<>(map, scalarRing);
    }

    /**
     * Factory method interno alla struttura per creare polinomi 
     * appartenenti a questo specifico anello.
     */
    @SafeVarargs
    public final CommutativePolynomial<K> of(K... coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            map.put(i, coeffs[i]);
        }
        return new CommutativePolynomial<>(map, scalarRing);
    }

    @Override
    public CommutativePolynomial<K> of(double value) {
        // Crea un polinomio costante: P(x) = scalarRing.of(value)
        return constant(scalarRing.of(value));
    }

    @Override
    public CommutativePolynomial<K> of(long value) {
        return constant(scalarRing.of(value));
    }

    @Override
    public CommutativePolynomial<K> of(int value) {
        return constant(scalarRing.of(value));
    }
}