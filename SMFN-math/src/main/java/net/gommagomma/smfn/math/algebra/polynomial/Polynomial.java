package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.algebra.core.elements.additive.GroupElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;

/**
 * Rappresenta un Polinomio P(x) in una indeterminata su un Semianello K.
 * Implementa la natura formale algebrica (Anello K[x]) e la natura funzionale (valutazione P(x)).
 * <p>
 * Implementazione "Sparsa": utilizza una TreeMap per memorizzare solo i coefficienti non nulli.
 *
 * @param <K> Il tipo dei coefficienti (estende almeno SemiringElement).
 */
public final class Polynomial<K extends SemiringElement<K>>
implements CommutativeRingElement<Polynomial<K>>, MathFunction<K, K>
{
    private final TreeMap<Integer, K> coefficients;
    private final K zeroScalar;

    // --- Costruttori (Privati, usare le Factory) ---

    private Polynomial(TreeMap<Integer, K> coefficients, K zeroScalar) {
        this.coefficients = coefficients;
        this.zeroScalar = zeroScalar;
    }

    // --- Factory Methods ---

    /**
     * Factory: Crea un polinomio da un array di coefficienti [c0, c1, ..., cn].
     */
    public static <K extends SemiringElement<K>> Polynomial<K> of(K[] coeffs, K zeroScalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            // Salva solo i non-zero
            if (!coeffs[i].isMathematicallyEqualTo(zeroScalar)) {
                map.put(i, coeffs[i]); 
            }
        }
        return new Polynomial<>(map, zeroScalar);
    }

    public static <K extends SemiringElement<K>> Polynomial<K> zero(K zeroScalar) {
        return new Polynomial<>(new TreeMap<>(), zeroScalar);
    }

    public static <K extends SemiringElement<K>> Polynomial<K> one(K oneScalar, K zeroScalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        map.put(0, oneScalar);
        return new Polynomial<>(map, zeroScalar);
    }

    public static <K extends SemiringElement<K>> Polynomial<K> monomial(K coefficient, int degree, K zeroScalar) {
        TreeMap<Integer, K> map = new TreeMap<>();
        if (!coefficient.isMathematicallyEqualTo(zeroScalar)) {
            map.put(degree, coefficient);
        }
        return new Polynomial<>(map, zeroScalar);
    }

    // --- Accessori e Proprietà ---

    public int degree() {
        if (coefficients.isEmpty()) return -1;
        return coefficients.lastKey();
    }

    public K getCoefficient(int power) {
        return coefficients.getOrDefault(power, zeroScalar);
    }

    // --- MathFunction Implementation (Algoritmo di Horner Sparso) ---

    @Override
    public K evaluate(K x) {
        if (coefficients.isEmpty()) {
            return zeroScalar;
        }

        if (x.isMathematicallyEqualTo(zeroScalar)) {
            return getCoefficient(0);
        }
        
        // Horner Sparso:
        int currentDegree = coefficients.lastKey();
        K result = coefficients.get(currentDegree);

        for (Map.Entry<Integer, K> entry : coefficients.descendingMap().entrySet()) {
            int nextDegree = entry.getKey();
            if (nextDegree == currentDegree) continue;

            int powerDiff = currentDegree - nextDegree;
            
            // Moltiplica 'result' per x^powerDiff
            for (int i = 0; i < powerDiff; i++) {
                result = result.multiply(x);
            }

            result = result.add(entry.getValue());
            currentDegree = nextDegree;
        }

        // Gestione della coda finale (moltiplicazione per l'ultima x^currentDegree)
        if (currentDegree > 0) {
            for (int i = 0; i < currentDegree; i++) {
                result = result.multiply(x);
            }
        }

        return result;
    }

    // --- AlgebraicElement Implementation ---

    @Override
    public boolean isMathematicallyEqualTo(Polynomial<K> other) {
        if (this == other) return true;
        if (other == null) return false;
        
        // Verifica se le mappe sparse sono identiche
        if (this.coefficients.size() != other.coefficients.size()) return false;
        
        for (Map.Entry<Integer, K> entry : this.coefficients.entrySet()) {
            K otherVal = other.coefficients.get(entry.getKey());
            // Se la chiave esiste nell'altra, il valore non può essere null
            if (otherVal == null || !entry.getValue().isMathematicallyEqualTo(otherVal)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Polynomial<K> copy() {
        // Assume che K.copy() sia una deep copy se K è mutabile
        TreeMap<Integer, K> newMap = new TreeMap<>();
        for (Map.Entry<Integer, K> entry : this.coefficients.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().copy());
        }
        // Nota: zeroScalar è immutabile (es. SignedInt), quindi non richiede .copy()
        return new Polynomial<>(newMap, this.zeroScalar); 
    }

    // --- Ring Operations ---

    @Override
    public Polynomial<K> add(Polynomial<K> other) {
        TreeMap<Integer, K> newCoeffs = new TreeMap<>(this.coefficients);
        
        for (Map.Entry<Integer, K> entry : other.coefficients.entrySet()) {
            int power = entry.getKey();
            K otherVal = entry.getValue();
            
            // Se la somma è isMathematicallyEqualTo(zero), la chiave viene rimossa (null)
            newCoeffs.merge(power, otherVal, (v1, v2) -> {
                K sum = v1.add(v2);
                return sum.isMathematicallyEqualTo(zeroScalar) ? null : sum;
            });
        }
        return new Polynomial<>(newCoeffs, this.zeroScalar);
    }

    @Override
    public Polynomial<K> multiply(Polynomial<K> other) {
        if (this.isZero() || other.isZero()) {
            return getZero();
        }

        TreeMap<Integer, K> newCoeffs = new TreeMap<>();

        for (Map.Entry<Integer, K> thisEntry : this.coefficients.entrySet()) {
            for (Map.Entry<Integer, K> otherEntry : other.coefficients.entrySet()) {
                int newPower = thisEntry.getKey() + otherEntry.getKey();
                K product = thisEntry.getValue().multiply(otherEntry.getValue());

                if (!product.isMathematicallyEqualTo(zeroScalar)) {
                    // Accumulo dei termini con lo stesso grado
                    newCoeffs.merge(newPower, product, (v1, v2) -> {
                        K sum = v1.add(v2);
                        return sum.isMathematicallyEqualTo(zeroScalar) ? null : sum;
                    });
                }
            }
        }
        return new Polynomial<>(newCoeffs, this.zeroScalar);
    }
    
    // Si basa sul fatto che K implementi GroupElement
    @Override
    @SuppressWarnings("unchecked")
    public Polynomial<K> negate() {
        TreeMap<Integer, K> newCoeffs = new TreeMap<>();
        
        for (Map.Entry<Integer, K> entry : this.coefficients.entrySet()) {
            K val = entry.getValue();
            if (val instanceof GroupElement) {
                K negated = (K) ((GroupElement<?>) val).negate();
                newCoeffs.put(entry.getKey(), negated);
            } else {
                throw new UnsupportedOperationException("Coefficient type " + val.getClass().getSimpleName() + 
                                                      " must be a GroupElement to support negation.");
            }
        }
        return new Polynomial<>(newCoeffs, this.zeroScalar);
    }

    @Override
    public Polynomial<K> getZero() {
        return zero(this.zeroScalar);
    }

    @Override
    public boolean isZero() {
        return coefficients.isEmpty();
    }
    
    @Override
    public Polynomial<K> getOne() {
        // Come discusso, per coerenza, richiediamo che l'identità moltiplicativa sia fornita dalla Struttura.
        throw new UnsupportedOperationException("Use PolynomialRing.multiplicativeIdentity() to get the multiplicative identity element.");
    }

    // --- Divisione Euclidea (Capability: Field) ---

    @SuppressWarnings("unchecked")
    public PolynomialQuotientRemainder<K> euclideanDivide(Polynomial<K> divisor) {
        if (divisor.isZero()) {
            throw new ArithmeticException("Division by zero polynomial.");
        }
        
        if (!(zeroScalar instanceof FieldElement)) {
             throw new UnsupportedOperationException("Polynomial division requires coefficients to be a Field (e.g., Rational, Real, Complex).");
        }

        Polynomial<K> quotient = getZero();
        Polynomial<K> remainder = this.copy();
        
        K divisorLeadCoeff = divisor.getCoefficient(divisor.degree());
        int divisorDegree = divisor.degree();
        
        // Calcolo inverso del coefficiente di testa del divisore
        K invDivisorLead = (K) ((FieldElement<?, ?>) divisorLeadCoeff).inverse(); 

        while (!remainder.isZero() && remainder.degree() >= divisorDegree) {
            int degDiff = remainder.degree() - divisorDegree;
            K leadCoeffRem = remainder.getCoefficient(remainder.degree());
            
            K factor = leadCoeffRem.multiply(invDivisorLead);
            
            Polynomial<K> term = monomial(factor, degDiff, zeroScalar);
            
            quotient = quotient.add(term);
            // r = r - term * divisor (sottrazione = addizione dell'opposto)
            remainder = remainder.add(term.multiply(divisor).negate());
        }
        
        // Uso la classe di supporto Java 11
        return new PolynomialQuotientRemainder<>(quotient, remainder);
    }

    // --- Standard Object ---

    @Override
    public String toString() {
        if (isZero()) return "0";

        StringBuilder sb = new StringBuilder();
        // Itera in ordine inverso (dal grado più alto)
        Iterator<Map.Entry<Integer, K>> it = coefficients.descendingMap().entrySet().iterator();
        
        boolean first = true;
        while (it.hasNext()) {
            Map.Entry<Integer, K> entry = it.next();
            int deg = entry.getKey();
            K coeff = entry.getValue();
            
            if (!first) {
                // Semplificazione: assume che K.toString() includa il segno se negativo.
                sb.append(" + "); 
            }
            
            sb.append(coeff.toString());
            
            if (deg > 0) {
                sb.append("x");
                if (deg > 1) {
                    sb.append("^").append(deg);
                }
            }
            first = false;
        }
        return sb.toString();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(coefficients);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Polynomial)) return false;
        // Non usiamo isMathematicallyEqualTo per le final equals/hashCode, ma l'uguaglianza profonda
        Polynomial<?> that = (Polynomial<?>) obj;
        return Objects.equals(this.zeroScalar, that.zeroScalar) &&
               Objects.equals(this.coefficients, that.coefficients);
    }
}