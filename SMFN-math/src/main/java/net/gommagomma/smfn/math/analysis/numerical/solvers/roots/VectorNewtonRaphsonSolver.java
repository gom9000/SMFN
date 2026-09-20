package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableVectorProblem;
import net.gommagomma.smfn.math.analysis.core.problems.VectorRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceJacobianEstimator;
import net.gommagomma.smfn.math.analysis.numerical.solvers.linear.GaussianEliminationSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Newton-Raphson multidimensionale: x_{n+1} = x_n - J^-1(x_n) * F(x_n).
 *
 * La Jacobiana e' una capacita' opzionale, verificata con instanceof --
 * stesso schema di NewtonRaphsonSolver: se il problema implementa
 * DifferentiableVectorProblem, si usa getJacobian() (esatta); altrimenti si
 * ricade su CentralDifferenceJacobianEstimator (numerica, n valutazioni
 * extra di F per passo, non n^2 -- una colonna alla volta da la' intera
 * colonna per tutte le righe in una sola valutazione).
 *
 * Il passo si ottiene risolvendo J*step = F(x) con GaussianEliminationSolver,
 * non calcolando J^-1 per poi applicarla una volta sola: piu' veloce e
 * numericamente piu' stabile, dato che l'inversa non serve mai per se stessa.
 */
public class VectorNewtonRaphsonSolver<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
{
	private final GaussianEliminationSolver<K, S> linearSolver;
	private final Module<Vector<K>, K, S> vectorSpace;
	private final CentralDifferenceJacobianEstimator<K, S> numericFallback;

	public VectorNewtonRaphsonSolver(GaussianEliminationSolver<K, S> linearSolver, Module<Vector<K>, K, S> vectorSpace,
	                                  CentralDifferenceJacobianEstimator<K, S> numericFallback) {
		this.linearSolver = linearSolver;
		this.vectorSpace = vectorSpace;
		this.numericFallback = numericFallback;
	}

	public Vector<K> solve(VectorRootFindingProblem<K> problem, Vector<K> initialGuess,
	                       ConvergenceCriteria criteria, ConvergenceParameters params,
	                       MetricSpace<Vector<K>> space) {

		Vector<K> current = initialGuess;

		for (int k = 0; k < params.maxIterations; k++) {
			Vector<K> previous = current;

			Vector<K> fValue = problem.apply(current);
			SquareMatrix<K> jacobian = (problem instanceof DifferentiableVectorProblem)
				? ((DifferentiableVectorProblem<K>) problem).getJacobian().apply(current)
				: numericFallback.estimateAt(problem, current);

			Vector<K> step = linearSolver.solve(jacobian, fValue);
			current = vectorSpace.subtract(current, step);

			Real distance = space.distance(current, previous);
			if (criteria.isConverged(distance, params, k + 1)) {
				return current;
			}
		}

		throw new IllegalStateException("Convergenza fallita dopo " + params.maxIterations + " iterazioni.");
	}
}
