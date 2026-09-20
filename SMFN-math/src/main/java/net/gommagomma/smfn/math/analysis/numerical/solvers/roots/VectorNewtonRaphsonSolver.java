package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableVectorProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.linear.GaussianEliminationSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Newton-Raphson multidimensionale.
 *
 * Ad ogni iterazione risolve il sistema lineare
 *
 *     J(x_n) * step = F(x_n)
 *
 * e aggiorna la soluzione secondo
 *
 *     x_{n+1} = x_n - step.
 */
public class VectorNewtonRaphsonSolver<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
{
	private final GaussianEliminationSolver<K, S> linearSolver;
	private final Module<Vector<K>, K, S> vectorSpace;

	public VectorNewtonRaphsonSolver(GaussianEliminationSolver<K, S> linearSolver, Module<Vector<K>, K, S> vectorSpace) {
		this.linearSolver = linearSolver;
		this.vectorSpace = vectorSpace;
	}

	public Vector<K> solve(DifferentiableVectorProblem<K> problem, Vector<K> initialGuess,
	                       ConvergenceCriteria criteria, ConvergenceParameters params,
	                       MetricSpace<Vector<K>> space) {

		Vector<K> current = initialGuess;

		for (int k = 0; k < params.maxIterations; k++) {
			Vector<K> previous = current;

			Vector<K> fValue = problem.apply(current);
			SquareMatrix<K> jacobian = problem.getJacobian().apply(current);

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
