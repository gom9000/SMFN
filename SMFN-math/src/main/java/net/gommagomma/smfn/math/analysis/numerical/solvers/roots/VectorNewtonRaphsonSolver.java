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
 * Newton-Raphson multidimensionale: x_{n+1} = x_n - J^-1(x_n) * F(x_n).
 *
 * Richiede la Jacobiana analitica (DifferentiableVectorProblem) -- a
 * differenza della versione scalare, qui non c'e' ancora un fallback
 * numerico (richiederebbe n valutazioni di F per ogni passo, una per
 * colonna della Jacobiana, con conversione del passo h in K): lasciato
 * per un'estensione futura, se servira' davvero.
 *
 * Il passo si ottiene risolvendo J*step = F(x) con GaussianEliminationSolver,
 * non calcolando J^-1 per poi applicarla una volta sola: piu' veloce e
 * numericamente piu' stabile, dato che l'inversa non serve mai per se stessa.
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
