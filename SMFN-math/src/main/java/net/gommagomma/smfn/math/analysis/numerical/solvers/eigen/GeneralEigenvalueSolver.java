package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Conjugable;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Punto di ingresso unico: sceglie il solver giusto in base a cosa la
 * matrice garantisce (simmetrica/hermitiana o generica; K conjugabile o
 * no), senza che il chiamante debba sapere quale algoritmo concreto usare.
 */
public final class GeneralEigenvalueSolver<K extends ScalarElement<K>>
implements EigenvalueSolver<K>
{
	@Override
	public SolverResult<EigenDecomposition> solve(SquareMatrix<K> matrix, StoppingParameters params) {
		if (matrix.isHermitian()) {
			return solveSymmetricOrHermitian(matrix, params);
		}
		return new QREigenvalueSolver<K>().solve(matrix, params);
	}

	@SuppressWarnings("unchecked")
	private SolverResult<EigenDecomposition> solveSymmetricOrHermitian(SquareMatrix<K> matrix, StoppingParameters params) {
		K sample = matrix.get(0, 0);

		if (sample instanceof Conjugable) {
			return new HermitianEigenvalueSolver().solve((SquareMatrix<Complex>) matrix, params);
		}
		if (sample instanceof Real) {
			return new JacobiEigenvalueSolver().solve((SquareMatrix<Real>) matrix, params);
		}
		throw new UnsupportedOperationException("Nessun solver simmetrico/hermitiano per " + sample.getClass().getSimpleName());
	}
}
