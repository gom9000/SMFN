package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

public final class Polynomials
{
    private Polynomials() {}

    /** Crea un polinomio commutativo (es. Integers, Reals) */
    @SafeVarargs
    public static <K extends CommutativeRingElement<K>> CommutativePolynomial<K> commutative(CommutativeRing<K> structure, K... coeffs) {
        return new CommutativePolynomial<>(buildMap(structure, coeffs), structure);
    }

    /** Crea un polinomio commutativo partendo da valori primitivi */
    public static <K extends CommutativeRingElement<K>> CommutativePolynomial<K> commutative(CommutativeRing<K> structure, double... values) {
        TreeMap<Integer, K> map = new TreeMap<>();
        K zero = structure.zero();
        for (int i = 0; i < values.length; i++) {
            K val = structure.of(values[i]);
            // Importante: inserire solo se diverso da zero
            if (!val.isMathematicallyEqualTo(zero)) {
                map.put(i, val);
            }
        }
        return new CommutativePolynomial<>(map, structure);
    }

    /** Crea un polinomio per anelli generici (es. Matrici) */
    @SafeVarargs
    public static <K extends RingElement<K>> GeneralPolynomial<K> ring(Ring<K> structure, K... coeffs) {
        return new GeneralPolynomial<>(buildMap(structure, coeffs), structure);
    }

    /** Crea un polinomio per semianelli (es. Naturali) */
    @SafeVarargs
    public static <K extends SemiringElement<K>> SemiringPolynomial<K> semiring(Semiring<K> structure, K... coeffs) {
        return new SemiringPolynomial<>(buildMap(structure, coeffs), structure);
    }

    /** * Helper interno per trasformare array in TreeMap.
     * Filtra i null e gli zeri matematici per mantenere corretti i calcoli sul grado.
     */
    private static <K extends SemiringElement<K>> TreeMap<Integer, K> buildMap(Semiring<K> structure, K[] coeffs) {
        TreeMap<Integer, K> map = new TreeMap<>();
        K zero = structure.zero();
        for (int i = 0; i < coeffs.length; i++) {
            if (coeffs[i] != null && !coeffs[i].isMathematicallyEqualTo(zero)) {
                map.put(i, coeffs[i]);
            }
        }
        return map;
    }

    @SafeVarargs
    public static <K extends FieldElement<K, ?>> EuclideanPolynomial<K> euclidean(Field<K, ?> structure, K... coeffs) {
        return new EuclideanPolynomial<>(buildMap(structure, coeffs), structure);
    }
}