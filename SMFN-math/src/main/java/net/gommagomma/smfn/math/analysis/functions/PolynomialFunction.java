package net.gommagomma.smfn.math.analysis.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.analysis.core.functionals.EvaluationFunctional;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator.CoefficientSequence;

public class PolynomialFunction<K extends ScalarElement<K>, S extends Ring<K>>
implements Mapping<K, K>, CoefficientSequence<K>
{
	private final Polynomial<K> polynomial;
    private final S structure;
    private final EvaluationFunctional<PolynomialFunction<K, S>, K, K> evaluator;


    public PolynomialFunction(Polynomial<K> polynomial, S structure, EvaluationFunctional<PolynomialFunction<K, S>, K, K> evaluator) {
    	this.polynomial = polynomial;
    	this.structure = structure;
    	this.evaluator = evaluator;
    }

    @Override
    public K apply(K x) { return evaluator.evaluate(this, x); }

	@Override
	public int degree() { return polynomial.degree(); }

	@Override
	public K getCoefficient(int index) { return polynomial.getCoefficient(index); }

	public Polynomial<K> getPolynomial() { return polynomial; }
    public S getStructure() { return structure; }
}
