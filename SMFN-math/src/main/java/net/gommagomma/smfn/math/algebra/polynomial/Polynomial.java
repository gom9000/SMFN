package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.Collections;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.EvaluationProvider;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public final class Polynomial<K extends ScalarElement<K>> 
implements CompositeElement<K, Polynomial<K>>, ScalarElement<Polynomial<K>>, Morphism<K, K>
{
	private final List<K> coefficients; // Ordinati per grado crescente: a0, a1, ... an
	private final ScalarStructure<Polynomial<K>> polynomialStructure;
	private final ScalarStructure<K> scalarStructure;


	protected Polynomial(ScalarStructure<Polynomial<K>> polynomialStructure, ScalarStructure<K> scalarStructure, List<K> coefficients) {
		this.polynomialStructure = polynomialStructure;
        this.scalarStructure = scalarStructure;
        this.coefficients = normalize(scalarStructure, coefficients);
    }

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

    public int degree() {
        return coefficients.isEmpty() ? -1 : coefficients.size() - 1;
    }

    public K getCoefficient(int degree) {
        if (degree < 0 || degree >= coefficients.size()) {
            return scalarStructure.zero();
        }
        return coefficients.get(degree);
    }

    public List<K> getCoefficients() {
        return Collections.unmodifiableList(coefficients);
    }

    @Override // Morphism impls
    public K apply(K input) {
    	if (polynomialStructure instanceof EvaluationProvider) {
            return ((EvaluationProvider<Polynomial<K>, K, K>) polynomialStructure).evaluate(this, input);
        }
        throw new UnsupportedOperationException("Questa struttura non supporta la valutazione del polinomio.");
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
        return new Polynomial<>(polynomialStructure, scalarStructure, coefficients);
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
