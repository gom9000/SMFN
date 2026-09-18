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
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Newton-Raphson multidimensionale: x_{n+1} = x_n - J^-1(x_n) * F(x_n).
 *
 * Richiede la Jacobiana analitica (DifferentiableVectorProblem) -- a
 * differenza della versione scalare, qui non c'e' ancora un fallback
 * numerico (richiederebbe n valutazioni di F per ogni passo, una per
 * colonna della Jacobiana, con conversione del passo h in K): lasciato
 * per un'estensione futura, se servira' davvero.
 */
public class VectorNewtonRaphsonSolver<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
{
	private final SquareMatrixAlgebra<K, S> matrixAlgebra;
	private final Module<Vector<K>, K, S> vectorSpace;

	public VectorNewtonRaphsonSolver(SquareMatrixAlgebra<K, S> matrixAlgebra, Module<Vector<K>, K, S> vectorSpace) {
		this.matrixAlgebra = matrixAlgebra;
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
			SquareMatrix<K> jacobianInverse = matrixAlgebra.inverse(jacobian);

			Vector<K> step = jacobianInverse.apply(fValue); // J^-1 * F(x), riusando SquareMatrix come LinearOperator
			current = vectorSpace.subtract(current, step);

			Real distance = space.distance(current, previous);
			if (criteria.isConverged(distance, params, k + 1)) {
				return current;
			}
		}

		throw new IllegalStateException("Convergenza fallita dopo " + params.maxIterations + " iterazioni.");
	}
}
