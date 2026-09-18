package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.Collections;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta un polinomio in un'indeterminata a coefficienti in un anello o campo scalare.
 * Implementa sia l'interfaccia di elemento composito sia quella di elemento scalare per permettere 
 * la trattazione ricorsiva e l'incapsulamento all'interno di strutture algebriche.
 * 
 * @param <K> il tipo degli elementi scalari (coefficienti) del polinomio
 */
public final class Polynomial<K extends ScalarElement<K>> 
implements CompositeElement<K, Polynomial<K>>, ScalarElement<Polynomial<K>>
{
    private final List<K> coefficients; // Ordinati per grado crescente: a0, a1, ... an
    private final ScalarStructure<Polynomial<K>> polynomialStructure;
    private final ScalarStructure<K> scalarStructure;


    /**
     * Costruisce un nuovo polinomio associato a una struttura polinomiale, a una struttura scalare 
     * e a una lista di coefficienti (normalizzati automaticamente rimuovendo i termini nulli di grado superiore).
     * 
     * @_param polynomialStructure la struttura algebrica dei polinomi di riferimento
     * @_param scalarStructure la struttura algebrica dei coefficienti scalari
     * @param coefficients la lista dei coefficienti ordinati per grado crescente
     */
    protected Polynomial(ScalarStructure<Polynomial<K>> polynomialStructure, ScalarStructure<K> scalarStructure, List<K> coefficients) {
        this.polynomialStructure = polynomialStructure;
        this.scalarStructure = scalarStructure;
        this.coefficients = normalize(scalarStructure, coefficients);
    }

    /**
     * Normalizza la lista dei coefficienti rimuovendo i coefficienti nulli finali per determinare il grado reale.
     * 
     * @param struct la struttura scalare per testare lo zero
     * @param coeffs la lista grezza dei coefficienti
     * @return una lista immutabile normalizzata dei coefficienti
     */
    private List<K> normalize(ScalarStructure<K> struct, List<K> coeffs) {
        if (coeffs.isEmpty()) return List.of();
        
        int lastNonZero = -1;
        for (int i = 0; i < coeffs.size(); i++) {
            if (!struct.isZero(coeffs.get(i))) {
                lastNonZero = i;
            }
        }
        
        if (lastNonZero == -1) return List.of();
        return List.copyOf(coeffs.subList(0, lastNonZero + 1));
    }

    /**
     * Restituisce il grado algebrico del polinomio.
     * 
     * @return il grado del polinomio, oppure $-1$ se il polinomio è il polinomio zero
     */
    public int degree() {
        return coefficients.isEmpty() ? -1 : coefficients.size() - 1;
    }

    /**
     * Restituisce il coefficiente corrispondente al grado specificato.
     * Se il grado supera la dimensione del polinomio, restituisce lo zero scalare.
     * 
     * @param degree l'esponente/grado del termine di cui si vuole il coefficiente
     * @return il coefficiente scalare associato
     */
    public K getCoefficient(int degree) {
        if (degree < 0 || degree >= coefficients.size()) {
            return scalarStructure.zero();
        }
        return coefficients.get(degree);
    }

    /**
     * Restituisce la lista non modificabile di tutti i coefficienti del polinomio ordinati per grado crescente.
     * 
     * @return la lista dei coefficienti
     */
    public List<K> getCoefficients() {
        return Collections.unmodifiableList(coefficients);
    }

    @Override
    public ScalarStructure<K> getScalarStructure() {
        return scalarStructure;
    }

    @Override
    public ScalarStructure<Polynomial<K>> getStructure() {
        return polynomialStructure;
    }

    @Override
    public Polynomial<K> copy() {
        // Essendo immutabile, possiamo restituire this: non c'è stato da duplicare.
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Polynomial)) return false;
        Polynomial<?> that = (Polynomial<?>) o;
        // Grazie alla normalizzazione, il confronto tra liste è sufficiente
        return coefficients.equals(that.coefficients);
    }

    @Override
    public int hashCode() {
        return coefficients.hashCode();
    }

    @Override
    public String toString() {
        if (coefficients.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = degree(); i >= 0; i--) {
            K coeff = getCoefficient(i);
            if (scalarStructure.isZero(coeff)) continue;
            
            if (sb.length() > 0) sb.append(" + ");
            
            sb.append("(").append(coeff).append(")");
            if (i > 0) sb.append("x");
            if (i > 1) sb.append("^").append(i);
        }
        return sb.toString();
    }
}