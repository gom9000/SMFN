package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * Autovalori e autovettori di una matrice reale qualunque, senza richiedere
 * simmetria: algoritmo QR con shift di Wilkinson, interamente in aritmetica
 * complessa internamente (QRAlgorithm).
 */
public final class RealQREigenvalueSolver
implements EigenvalueSolver<Real>
{
	private static final QRAlgorithm ALGORITHM = new QRAlgorithm();

	@Override
	public SolverResult<EigenDecomposition> solve(SquareMatrix<Real> matrix, StoppingParameters params) {
		int n = matrix.getN();
		return ALGORITHM.solve(toComplexArray(matrix, n), n, params);
	}

	private Complex[][] toComplexArray(SquareMatrix<Real> matrix, int n) {
		Complex[][] a = new Complex[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = new Complex(matrix.get(i, j).getValue(), 0.0);
			}
		}
		return a;
	}
}
