package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Autovalori e autovettori di una matrice complessa hermitiana, via
 * rotazioni di Jacobi, con un passo in piu':
 * l'elemento fuori diagonale da azzerare e' complesso, non reale, quindi
 * ogni rotazione si scompone in due passi:
 *
 *   1. Assorbimento di fase: A[p][q] = r*e^{iB} (forma polare). Una
 *      trasformazione diagonale unitaria (colonna q *= e^{-iB}, riga q
 *      *= e^{iB}) rende A[p][q] reale e non-negativo (== r), senza
 *      toccare la diagonale ne' rompere l'hermitianita'.
 *   2. La STESSA rotazione reale del caso simmetrico (formula stabile,
 *      solo divisione/segno/radice), applicata all'elemento ora reale.
 *
 * Gli autovalori di una matrice hermitiana sono sempre reali per costruzione
 * matematica ma restano rappresentati come Complex con parte immaginaria nulla.
 */
public final class HermitianEigenvalueSolver
implements EigenvalueSolver<Complex>
{
	private static final ComplexField C = ComplexField.INSTANCE;

	@Override
	public EigenDecomposition solve(SquareMatrix<Complex> matrix, ConvergenceParameters params) {
		if (!matrix.isHermitian()) {
			throw new IllegalArgumentException("HermitianEigenvalueSolver requires a Hermitian matrix.");
		}

		int n = matrix.getN();
		double[][] aRe = new double[n][n];
		double[][] aIm = new double[n][n];
		double[][] vRe = new double[n][n];
		double[][] vIm = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Complex value = matrix.get(i, j);
				aRe[i][j] = value.getRe();
				aIm[i][j] = value.getIm();
			}
			vRe[i][i] = 1.0;
		}

		double tolerance = params.tolerance.getValue();

		for (int iteration = 0; iteration < params.maxIterations; iteration++) {
			int p = 0, q = 1;
			double maxOffDiagonal = 0.0;
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					double magnitude = Math.hypot(aRe[i][j], aIm[i][j]);
					if (magnitude > maxOffDiagonal) {
						maxOffDiagonal = magnitude;
						p = i;
						q = j;
					}
				}
			}

			if (n < 2 || maxOffDiagonal < tolerance) {
				return buildResult(aRe, vRe, vIm, n);
			}

			rotate(aRe, aIm, vRe, vIm, n, p, q);
		}

		throw new IllegalStateException("Convergence failed after " + params.maxIterations + " iterations.");
	}

	private void rotate(double[][] aRe, double[][] aIm, double[][] vRe, double[][] vIm, int n, int p, int q) {
		// --- Passo 1: assorbimento di fase, rende a[p][q] reale e non-negativo ---
		double r = Math.hypot(aRe[p][q], aIm[p][q]);
		double phaseRe = aRe[p][q] / r;
		double phaseIm = aIm[p][q] / r;

		for (int i = 0; i < n; i++) {
			if (i != q) {
				double re = aRe[i][q], im = aIm[i][q];
				aRe[i][q] = re * phaseRe + im * phaseIm;   // * conj(phase)
				aIm[i][q] = im * phaseRe - re * phaseIm;

				double re2 = aRe[q][i], im2 = aIm[q][i];
				aRe[q][i] = re2 * phaseRe - im2 * phaseIm; // * phase
				aIm[q][i] = re2 * phaseIm + im2 * phaseRe;
			}
		}
		for (int i = 0; i < n; i++) {
			double re = vRe[i][q], im = vIm[i][q];
			vRe[i][q] = re * phaseRe + im * phaseIm;       // * conj(phase)
			vIm[i][q] = im * phaseRe - re * phaseIm;
		}

		// --- Passo 2: rotazione reale, identica a JacobiEigenvalueSolver, ---
		// --- sull'elemento (p,q) ora reale (== r) ---
		double app = aRe[p][p];
		double aqq = aRe[q][q];
		double theta = (aqq - app) / (2.0 * r);
		double t = (theta >= 0 ? 1.0 : -1.0) / (Math.abs(theta) + Math.sqrt(theta * theta + 1.0));
		double c = 1.0 / Math.sqrt(t * t + 1.0);
		double s = t * c;

		aRe[p][p] = app - t * r;
		aRe[q][q] = aqq + t * r;
		aRe[p][q] = 0.0; aIm[p][q] = 0.0;
		aRe[q][p] = 0.0; aIm[q][p] = 0.0;

		for (int i = 0; i < n; i++) {
			if (i != p && i != q) {
				double aipRe = aRe[i][p], aipIm = aIm[i][p];
				double aiqRe = aRe[i][q], aiqIm = aIm[i][q];

				double newAipRe = c * aipRe - s * aiqRe;
				double newAipIm = c * aipIm - s * aiqIm;
				double newAiqRe = s * aipRe + c * aiqRe;
				double newAiqIm = s * aipIm + c * aiqIm;

				aRe[i][p] = newAipRe; aIm[i][p] = newAipIm;
				aRe[p][i] = newAipRe; aIm[p][i] = -newAipIm; // coniugato: A[p][i] = conj(A[i][p])
				aRe[i][q] = newAiqRe; aIm[i][q] = newAiqIm;
				aRe[q][i] = newAiqRe; aIm[q][i] = -newAiqIm; // coniugato
			}
		}

		for (int i = 0; i < n; i++) {
			double vipRe = vRe[i][p], vipIm = vIm[i][p];
			double viqRe = vRe[i][q], viqIm = vIm[i][q];

			vRe[i][p] = c * vipRe - s * viqRe;
			vIm[i][p] = c * vipIm - s * viqIm;
			vRe[i][q] = s * vipRe + c * viqRe;
			vIm[i][q] = s * vipIm + c * viqIm;
		}
	}

	private EigenDecomposition buildResult(double[][] aRe, double[][] vRe, double[][] vIm, int n) {
		List<Complex> eigenvalues = new ArrayList<>(n);
		List<Vector<Complex>> eigenvectors = new ArrayList<>(n);
		VectorSpace<Complex, ComplexField> space = new VectorSpace<>(C, n);

		for (int j = 0; j < n; j++) {
			eigenvalues.add(new Complex(aRe[j][j], 0.0));
			Complex[] column = new Complex[n];
			for (int i = 0; i < n; i++) {
				column[i] = new Complex(vRe[i][j], vIm[i][j]);
			}
			eigenvectors.add(VectorElementFactory.of(space, List.of(column)));
		}

		return new EigenDecomposition(eigenvalues, eigenvectors);
	}
}
