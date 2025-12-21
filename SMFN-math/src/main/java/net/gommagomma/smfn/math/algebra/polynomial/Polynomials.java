package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

/**
 * Utility factory per la creazione rapida di polinomi.
 * Utilizza la 'structure' per convertire primitivi in elementi algebrici.
 */
public final class Polynomials {

    private Polynomials() {} // Classe utility, non istanziabile

    /**
     * Crea un polinomio commutativo da un array di coefficienti scalari.
     * I coefficienti sono ordinati per grado crescente: [a0, a1, a2, ...] -> a0 + a1*x + a2*x^2
     */
    @SafeVarargs
    public static <K extends CommutativeRingElement<K>> CommutativePolynomial<K> commutative(CommutativeRing<K> structure, K... coeffs) {
        return new CommutativePolynomial<>(buildMap(coeffs), structure);
    }

    /**
     * Crea un polinomio commutativo partendo da valori double (usa la factory della struttura).
     */
    public static <K extends CommutativeRingElement<K>> CommutativePolynomial<K> commutative(CommutativeRing<K> structure, double... values) {
        TreeMap<Integer, K> map = new TreeMap<>();
        for (int i = 0; i < values.length; i++) {
            map.put(i, structure.of(values[i]));
        }
        return new CommutativePolynomial<>(map, structure);
    }

    /**
     * Crea un polinomio generale (non commutativo) da coefficienti scalari (es. Matrici).
     */
    @SafeVarargs
    public static <K extends RingElement<K>> GeneralPolynomial<K> ring(Ring<K> structure, K... coeffs) {
        return new GeneralPolynomial<>(buildMap(coeffs), structure);
    }

    /**
     * Crea un polinomio per un Semianello (es. Naturali).
     */
    @SafeVarargs
    public static <K extends SemiringElement<K>> SemiringPolynomial<K> semiring(Semiring<K> structure, K... coeffs) {
        return new SemiringPolynomial<>(buildMap(coeffs), structure);
    }

    /** Helper interno per trasformare array in TreeMap */
    private static <K extends SemiringElement<K>> TreeMap<Integer, K> buildMap(K[] coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null) {
                map.put(i, coeffs[i]);
            }
        }
        return map;
    }
}