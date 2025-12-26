package net.gommagomma.smfn.math.algebra.polynomial.core;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

/**
 * Base astratta per tutti i polinomi.
 * Gestisce la memorizzazione dei coefficienti e le operazioni di base del Semianello.
 * * @param <K> Il tipo dei coefficienti (deve essere un SemiringElement).
 * @param <P> Il tipo del polinomio stesso (pattern CRTP).
 */
public abstract class AbstractPolynomial<K extends SemiringElement<K>, P extends AbstractPolynomial<K, P>> 
implements SemiringElement<P>, Morphism<K, K>
{
    protected final TreeMap<Integer, K> coefficients;
    protected final Semiring<K> structure;

    protected AbstractPolynomial(TreeMap<Integer, K> coefficients, Semiring<K> structure) {
        this.structure = Objects.requireNonNull(structure, "Algebraic structure cannot be null");
        this.coefficients = new TreeMap<>();
        
        // Pulizia automatica: non memorizziamo coefficienti matematicamente nulli
        K zeroK = structure.zero();
        coefficients.forEach((deg, val) -> {
            if (val != null && !val.isMathematicallyEqualTo(zeroK)) {
                this.coefficients.put(deg, val.copy());
            }
        });
    }

    public final Semiring<K> getScalarStructure() {
        return this.structure;
    }

    /** Crea una nuova istanza della sottoclasse specifica. */
    protected abstract P create(TreeMap<Integer, K> map);

    // --- Proprietà Fondamentali ---

    public int degree() {
        return coefficients.isEmpty() ? -1 : coefficients.lastKey();
    }

    public boolean isZero() {
        return coefficients.isEmpty();
    }

    public K getCoefficient(int degree) {
        return coefficients.getOrDefault(degree, structure.zero());
    }

    /** Restituisce una vista non modificabile dei coefficienti. */
    public Map<Integer, K> getCoefficients() {
        return Collections.unmodifiableMap(coefficients);
    }

    // --- Implementazione SemiringElement ---

    @Override
    public P add(P other) {
        TreeMap<Integer, K> newMap = new TreeMap<>(this.coefficients);
        other.coefficients.forEach((deg, val) -> {
            K current = newMap.getOrDefault(deg, structure.zero());
            newMap.put(deg, current.add(val));
        });
        return create(newMap);
    }

    @Override
    public P multiply(P other) {
        if (this.isZero() || other.isZero()) return getZero();

        TreeMap<Integer, K> newMap = new TreeMap<>();
        this.coefficients.forEach((deg1, val1) -> {
            other.coefficients.forEach((deg2, val2) -> {
                int newDeg = deg1 + deg2;
                K product = val1.multiply(val2);
                K current = newMap.getOrDefault(newDeg, structure.zero());
                newMap.put(newDeg, current.add(product));
            });
        });
        return create(newMap);
    }

    @Override
    public boolean isMathematicallyEqualTo(P other) {
        if (this == other) return true;
        if (other == null || this.degree() != other.degree()) return false;
        return this.coefficients.equals(other.coefficients);
    }

    // --- Valutazione (MathFunction) ---

    @Override
    public K apply(K x) {
        if (isZero()) return structure.zero();
        
        int deg = degree();
        K result = getCoefficient(deg);
        
        // Algoritmo di Horner: (...((a_n*x + a_{n-1})*x + ...)*x + a_0)
        for (int i = deg - 1; i >= 0; i--) {
            result = result.multiply(x).add(getCoefficient(i));
        }
        return result;
    }

    // --- Identità ---

    @Override
    public P getZero() {
        return create(new TreeMap<>());
    }

    @Override
    public String toString() {
        if (isZero()) return "0";
        StringBuilder sb = new StringBuilder();
        coefficients.descendingMap().forEach((deg, val) -> {
            if (sb.length() > 0) sb.append(" + ");
            sb.append("(").append(val).append(")");
            if (deg > 0) sb.append("x");
            if (deg > 1) sb.append("^").append(deg);
        });
        return sb.toString();
    }
}