package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Conjugable;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
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
	@SuppressWarnings("unchecked")
	public EigenDecomposition solve(SquareMatrix<K> matrix, ConvergenceParameters params) {
		if (matrix.isHermitian()) {
			K sample = matrix.get(0, 0);

			if (sample instanceof Conjugable) {
				return new HermitianEigenvalueSolver().solve((SquareMatrix<Complex>) matrix, params);
			}
			if (sample instanceof Real) {
				return new JacobiEigenvalueSolver().solve((SquareMatrix<Real>) matrix, params);
			}
			throw new UnsupportedOperationException(
				"No symmetric solver for this scalar type is implemented yet.");
		}

		throw new UnsupportedOperationException(
			"No solver for general (non symmetric/Hermitian) matrices is implemented yet "
			+ "(QREigenvalueSolver, a QR-with-shifts algorithm, would be needed).");
	}
}
