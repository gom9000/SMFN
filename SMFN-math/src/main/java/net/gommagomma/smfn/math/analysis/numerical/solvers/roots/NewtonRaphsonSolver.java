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
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;
import net.gommagomma.smfn.math.analysis.core.solvers.RootFindingSolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;

/**
 * Solutore Newton-Raphson: x_{k+1} = x_k - f(x_k)/f'(x_k).
 *
 * Se il problema implementa DifferentiableScalarProblem, usa la derivata
 * analitica fornita; altrimenti ricade su un differenziatore numerico.
 * <p>
 * Il mancato raggiungimento della convergenza entro il numero massimo di iterazioni,
 * cosi' come l'incontro di una derivata nulla lungo il cammino, non sono trattati come
 * eccezioni: sono esiti numerici possibili e attesi di un metodo iterativo, riportati
 * come {@link ConvergenceStatus#MAX_ITERATIONS_REACHED} e {@link ConvergenceStatus#NUMERICAL_ERROR}
 * nel {@link SolverResult} restituito, insieme all'ultima approssimazione disponibile.
 * E' compito di chi chiama {@link #solve} decidere se un esito diverso da CONVERGED
 * e' accettabile per il proprio caso d'uso (es. non lo e' per una ricerca di radici,
 * lo e' per un algoritmo che usa il conteggio delle iterazioni come dato, es. i frattali di Newton).
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
	public SolverResult<R> solve(ScalarRootFindingProblem<R> problem, R initialGuess,
	               ConvergenceCriteria criteria, ConvergenceParameters params,
	               MetricSpace<R> space) {

		Mapping<R, R> derivative = (problem instanceof DifferentiableScalarProblem)
			? ((DifferentiableScalarProblem<R>) problem).getDerivative()
			: fallbackDifferentiator.apply(problem);

		R current = initialGuess;
		Real stepDistance = space.distance(current, current);

		for (int k = 0; k < params.maxIterations; k++) {
			R previous = current;

			R fOfX = problem.apply(current);
			R fPrimeOfX = derivative.apply(current);

			if (field.isZero(fPrimeOfX)) {
				Real residual = space.distance(fOfX, field.zero());
				return new RootFindingSolverResult<>(current, ConvergenceStatus.NUMERICAL_ERROR, k, stepDistance, residual);
			}

			R step = field.divide(fOfX, fPrimeOfX);
			current = field.subtract(current, step);
			stepDistance = space.distance(current, previous);

			if (criteria.isConverged(stepDistance, params, k + 1)) {
				Real residual = space.distance(problem.apply(current), field.zero());
				return new RootFindingSolverResult<>(current, ConvergenceStatus.CONVERGED, k + 1, stepDistance, residual);
			}
		}

		Real finalResidual = space.distance(problem.apply(current), field.zero());
		return new RootFindingSolverResult<>(current, ConvergenceStatus.MAX_ITERATIONS_REACHED, params.maxIterations, stepDistance, finalResidual);
	}
}
