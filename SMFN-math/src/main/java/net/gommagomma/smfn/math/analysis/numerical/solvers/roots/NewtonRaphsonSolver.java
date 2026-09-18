package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.core.problems.ScalarRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;

/**
 * Solutore Newton-Raphson: x_{k+1} = x_k - f(x_k)/f'(x_k).
 *
 * Se il problema implementa DifferentiableScalarProblem, usa la derivata
 * analitica fornita; altrimenti ricade su un differenziatore numerico
 * (SymbolicOperator<R,R,Mapping<R,R>>, es. CentralDifferenceDifferentiator)
 * -- verificato con instanceof, non con un default che lancia eccezione.
 */
public class NewtonRaphsonSolver<R extends ScalarElement<R>>
implements IterativeSolver<ScalarRootFindingProblem<R>, R, R>
{
	private final Field<R> field;
	private final Mapping<Mapping<R, R>, Mapping<R, R>> fallbackDifferentiator;

	public NewtonRaphsonSolver(Field<R> field, Mapping<Mapping<R, R>, Mapping<R, R>> fallbackDifferentiator) {
		this.field = field;
		this.fallbackDifferentiator = fallbackDifferentiator;
	}

	@Override
	public R solve(ScalarRootFindingProblem<R> problem, R initialGuess,
	               ConvergenceCriteria criteria, ConvergenceParameters params,
	               MetricSpace<R> space) {

		Mapping<R, R> derivative = (problem instanceof DifferentiableScalarProblem)
			? ((DifferentiableScalarProblem<R>) problem).getDerivative()
			: fallbackDifferentiator.apply(problem);

		R current = initialGuess;

		for (int k = 0; k < params.maxIterations; k++) {
			R previous = current;

			R fOfX = problem.apply(current);
			R fPrimeOfX = derivative.apply(current);

			if (field.isZero(fPrimeOfX)) {
				throw new ArithmeticException("Derivata nulla all'iterazione " + k);
			}

			R step = field.divide(fOfX, fPrimeOfX);
			current = field.subtract(current, step);

			Real distance = space.distance(current, previous);
			if (criteria.isConverged(distance, params, k + 1)) {
				return current;
			}
		}

		throw new IllegalStateException("Convergenza fallita dopo " + params.maxIterations + " iterazioni.");
	}
}
