package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.BasicSolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Autovalori e autovettori di una matrice reale simmetrica, via rotazioni
 * di Jacobi. Converge sempre per matrici simmetriche.
 */
public final class JacobiEigenvalueSolver
implements EigenvalueSolver<Real>
{
	private static final ComplexField C = ComplexField.INSTANCE;

	@Override
	public SolverResult<EigenDecomposition> solve(SquareMatrix<Real> matrix, StoppingParameters params) {
		if (!matrix.isHermitian()) {
			// For Real, isHermitian() coincides with "symmetric" (no conjugation needed).
			throw new IllegalArgumentException("JacobiEigenvalueSolver requires a symmetric matrix.");
		}

		int n = matrix.getN();
		double[][] a = toRawArray(matrix, n);
		double[][] v = identityArray(n);
		double tolerance = params.tolerance.getValue();

		for (int iteration = 0; iteration < params.maxIterations; iteration++) {
			int p = 0, q = 1;
			double maxOffDiagonal = 0.0;
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					if (Math.abs(a[i][j]) > maxOffDiagonal) {
						maxOffDiagonal = Math.abs(a[i][j]);
						p = i;
						q = j;
					}
				}
			}

			if (n < 2 || maxOffDiagonal < tolerance) {
				return new BasicSolverResult<>(buildResult(a, v, n), TerminationStatus.CONVERGED, iteration);
			}

			rotate(a, v, n, p, q);
		}

		return new BasicSolverResult<>(buildResult(a, v, n), TerminationStatus.MAX_ITERATIONS_REACHED, params.maxIterations);
	}

	private void rotate(double[][] a, double[][] v, int n, int p, int q) {
		double theta = (a[q][q] - a[p][p]) / (2.0 * a[p][q]);
		double t = (theta >= 0 ? 1.0 : -1.0) / (Math.abs(theta) + Math.sqrt(theta * theta + 1.0));
		double c = 1.0 / Math.sqrt(t * t + 1.0);
		double s = t * c;

		double app = a[p][p];
		double aqq = a[q][q];
		double apq = a[p][q];

		a[p][p] = app - t * apq;
		a[q][q] = aqq + t * apq;
		a[p][q] = 0.0;
		a[q][p] = 0.0;

		for (int i = 0; i < n; i++) {
			if (i != p && i != q) {
				double aip = a[i][p];
				double aiq = a[i][q];
				a[i][p] = c * aip - s * aiq;
				a[p][i] = a[i][p];
				a[i][q] = s * aip + c * aiq;
				a[q][i] = a[i][q];
			}
		}

		for (int i = 0; i < n; i++) {
			double vip = v[i][p];
			double viq = v[i][q];
			v[i][p] = c * vip - s * viq;
			v[i][q] = s * vip + c * viq;
		}
	}

	private EigenDecomposition buildResult(double[][] a, double[][] v, int n) {
		List<Complex> eigenvalues = new ArrayList<>(n);
		List<Vector<Complex>> eigenvectors = new ArrayList<>(n);
		VectorSpace<Complex, ComplexField> space = new VectorSpace<>(C, n);

		for (int j = 0; j < n; j++) {
			eigenvalues.add(new Complex(a[j][j], 0.0));
			Complex[] column = new Complex[n];
			for (int i = 0; i < n; i++) {
				column[i] = new Complex(v[i][j], 0.0);
			}
			eigenvectors.add(VectorElementFactory.of(space, List.of(column)));
		}

		return new EigenDecomposition(eigenvalues, eigenvectors);
	}

	private double[][] toRawArray(SquareMatrix<Real> matrix, int n) {
		double[][] a = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = matrix.get(i, j).getValue();
			}
		}
		return a;
	}

	private double[][] identityArray(int n) {
		double[][] v = new double[n][n];
		for (int i = 0; i < n; i++) {
			v[i][i] = 1.0;
		}
		return v;
	}
}
