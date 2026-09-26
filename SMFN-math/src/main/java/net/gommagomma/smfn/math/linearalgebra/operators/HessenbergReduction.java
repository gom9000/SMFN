package net.gommagomma.smfn.math.linearalgebra.operators;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Riduzione di una matrice complessa quadrata a forma di Hessenberg superiore per
 * similarita' unitaria: Q^dagger * A * Q = H, con H nulla ovunque sotto la prima
 * sottodiagonale e Q unitaria (quindi H ha esattamente gli stessi autovalori di A).
 * <p>
 * E' il passo preparatorio standard prima di un algoritmo QR iterativo (usato da
 * QREigenvalueSolver): costa O(n^3) una tantum, ma rende ogni passo QR successivo
 * O(n^2) invece di O(n^3), perche' ogni colonna di una matrice di Hessenberg ha un
 * solo elemento non nullo sotto la diagonale -- una singola riflessione a due righe
 * (equivalente a una rotazione di Givens) basta per azzerarlo, invece di una
 * riflessione piena su tutta la sottocolonna come servirebbe per una matrice densa.
 * Vedi {@link HouseholderQRDecomposition} per la decomposizione QR piena, usata
 * altrove quando questa struttura non serve o non c'e'.
 */
public final class HessenbergReduction
{
	private static final ComplexField C = ComplexField.INSTANCE;

	private final Complex[][] h;
	private final Complex[][] q;
	private final int n;

	public HessenbergReduction(SquareMatrix<Complex> matrix) {
		this.n = matrix.getN();
		Complex[][] a = toArray(matrix, n);
		Result result = reduce(a, n);
		this.h = result.h;
		this.q = result.q;
	}

	/** H, forma di Hessenberg superiore: nulla ovunque sotto la prima sottodiagonale. */
	public SquareMatrix<Complex> getH() {
		return wrap(h, n);
	}

	/** Q, matrice unitaria tale che Q^dagger * A * Q = H. */
	public SquareMatrix<Complex> getQ() {
		return wrap(q, n);
	}

	/** Coppia (H, Q) su array grezzi, per chi -- come QREigenvalueSolver -- lavora gia' su Complex[][]. */
	public static final class Result
	{
		public final Complex[][] h;
		public final Complex[][] q;

		private Result(Complex[][] h, Complex[][] q) {
			this.h = h;
			this.q = q;
		}
	}

	/**
	 * Riduzione su array grezzi: restituisce sia H sia la Q accumulata, perche' chi la usa
	 * (QREigenvalueSolver) deve poi continuare a comporre altre trasformazioni sulla stessa Q
	 * fino alla decomposizione finale -- a differenza di HouseholderQRDecomposition.computeQ,
	 * qui scartare una delle due non avrebbe senso: servono entrambe.
	 */
	public static Result reduce(Complex[][] a, int n) {
		Complex[][] h = copy(a, n);
		Complex[][] q = identity(n);

		for (int k = 0; k < n - 2; k++) {
			int len = n - k - 1;
			Complex[] x = new Complex[len];
			double normX = 0.0;
			for (int i = 0; i < len; i++) {
				x[i] = h[k + 1 + i][k];
				normX += x[i].modulusSquared();
			}
			normX = Math.sqrt(normX);
			if (normX < 1e-300) {
				continue;
			}

			Complex x0 = x[0];
			double x0Modulus = x0.modulus();
			Complex phase = x0Modulus < 1e-300 ? C.one() : new Complex(x0.getRe() / x0Modulus, x0.getIm() / x0Modulus);
			Complex alpha = C.negate(C.multiply(phase, new Complex(normX, 0.0)));

			Complex[] v = new Complex[len];
			v[0] = C.subtract(x0, alpha);
			for (int i = 1; i < len; i++) {
				v[i] = x[i];
			}
			double vNormSquared = 0.0;
			for (int i = 0; i < len; i++) {
				vNormSquared += v[i].modulusSquared();
			}
			if (vNormSquared < 1e-300) {
				continue;
			}

			// A := P_k * A * P_k (P_k Hermitiana e unitaria: P_k^dagger = P_k), applicata prima
			// da sinistra alle righe k+1..n-1 (azzera la colonna k sotto la sottodiagonale),
			// poi da destra alle colonne k+1..n-1 (mantiene la similitudine). Q accumula la
			// stessa riflessione sulle sue colonne k+1..n-1.
			applyLeft(h, v, vNormSquared, k, n);
			applyRight(h, v, vNormSquared, k, n);
			applyRight(q, v, vNormSquared, k, n);
		}
		return new Result(h, q);
	}

	/** h[k+1..n-1][k..n-1] := (I - 2vv^dagger/v^dagger v) * h[k+1..n-1][k..n-1]. */
	private static void applyLeft(Complex[][] h, Complex[] v, double vNormSquared, int k, int n) {
		int len = v.length;
		int rowOffset = k + 1;
		for (int col = k; col < n; col++) {
			Complex dot = C.zero();
			for (int i = 0; i < len; i++) {
				dot = C.add(dot, C.multiply(v[i].conjugate(), h[rowOffset + i][col]));
			}
			Complex factor = C.multiply(new Complex(2.0 / vNormSquared, 0.0), dot);
			for (int i = 0; i < len; i++) {
				h[rowOffset + i][col] = C.subtract(h[rowOffset + i][col], C.multiply(factor, v[i]));
			}
		}
	}

	/** mat[:][k+1..n-1] := mat[:][k+1..n-1] * (I - 2vv^dagger/v^dagger v), su tutte le righe di mat. */
	private static void applyRight(Complex[][] mat, Complex[] v, double vNormSquared, int k, int n) {
		int len = v.length;
		int colOffset = k + 1;
		int rows = mat.length;
		for (int row = 0; row < rows; row++) {
			Complex dot = C.zero();
			for (int i = 0; i < len; i++) {
				dot = C.add(dot, C.multiply(mat[row][colOffset + i], v[i]));
			}
			Complex factor = C.multiply(new Complex(2.0 / vNormSquared, 0.0), dot);
			for (int i = 0; i < len; i++) {
				mat[row][colOffset + i] = C.subtract(mat[row][colOffset + i], C.multiply(factor, v[i].conjugate()));
			}
		}
	}

	private static Complex[][] identity(int n) {
		Complex[][] m = new Complex[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				m[i][j] = (i == j) ? C.one() : C.zero();
			}
		}
		return m;
	}

	private static Complex[][] copy(Complex[][] a, int n) {
		Complex[][] result = new Complex[n][n];
		for (int i = 0; i < n; i++) {
			result[i] = a[i].clone();
		}
		return result;
	}

	private static Complex[][] toArray(SquareMatrix<Complex> matrix, int n) {
		Complex[][] a = new Complex[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = matrix.get(i, j);
			}
		}
		return a;
	}

	private static SquareMatrix<Complex> wrap(Complex[][] a, int n) {
		Complex[] flat = new Complex[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				flat[i * n + j] = a[i][j];
			}
		}
		return SquareMatrixElementFactory.of(C, List.of(flat));
	}
}
