package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Autovalori e autovettori di una matrice complessa qualunque, senza
 * richiedere hermitianita': algoritmo QR con shift di Wilkinson (QRAlgorithm).
 */
public final class ComplexQREigenvalueSolver
implements EigenvalueSolver<Complex>
{
	private static final QRAlgorithm ALGORITHM = new QRAlgorithm();

	@Override
	public SolverResult<EigenDecomposition> solve(SquareMatrix<Complex> matrix, StoppingParameters params) {
		int n = matrix.getN();
		return ALGORITHM.solve(toComplexArray(matrix, n), n, params);
	}

	private Complex[][] toComplexArray(SquareMatrix<Complex> matrix, int n) {
		Complex[][] a = new Complex[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = matrix.get(i, j);
			}
		}
		return a;
	}
}
