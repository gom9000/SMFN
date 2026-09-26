package net.gommagomma.smfn.math.linearalgebra.operators;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Decomposizione QR di una matrice complessa quadrata via riflessioni di Householder:
 * A = Q*R, con Q unitaria (Q^dagger*Q = I) e R triangolare superiore.
 * <p>
 * Estratta da QREigenvalueSolver, che la usava internamente ad ogni iterazione della sua
 * iterazione QR con shift, scartando pero' sempre R (l'aggiornamento per similarita'
 * Q^dagger*A*Q e' algebricamente identico a R*Q + shift*I ma non richiede mai di formare
 * R esplicitamente). Qui la decomposizione e' completa e pubblica, cosi' da essere
 * riutilizzabile ovunque serva una vera QR (sistemi lineari, minimi quadrati, altri
 * algoritmi spettrali), non solo dentro quell'unico algoritmo.
 * <p>
 * Solo K = Complex: le riflessioni di Householder complesse richiedono una nozione di fase
 * (x / |x|) oltre che di radice quadrata, che {@code Complex} offre e un generico
 * {@code ScalarElement<K>} no. Chi lavora su matrici reali puo' comunque usare questa classe
 * incorporando i valori in {@code Complex} con parte immaginaria nulla (e' esattamente cio'
 * che fa QREigenvalueSolver per K = Real).
 */
public final class HouseholderQRDecomposition
{
	private static final ComplexField C = ComplexField.INSTANCE;

	private final Complex[][] q;
	private final Complex[][] r;
	private final int n;

	public HouseholderQRDecomposition(SquareMatrix<Complex> matrix) {
		this.n = matrix.getN();
		Complex[][] a = toArray(matrix, n);
		this.r = copy(a, n);
		this.q = identity(n);
		decompose(r, q, n);
	}

	/** Q, matrice unitaria: Q^dagger * Q = I. */
	public SquareMatrix<Complex> getQ() {
		return wrap(q, n);
	}

	/** R, triangolare superiore, tale che Q*R = A. */
	public SquareMatrix<Complex> getR() {
		return wrap(r, n);
	}

	/**
	 * Calcola solo Q (m x m), scartando R: primitiva a basso livello su array grezzi per
	 * chi -- come QREigenvalueSolver -- lavora gia' su {@code Complex[][]} e ripete la
	 * decomposizione ad ogni iterazione, per cui impacchettare/spacchettare una
	 * SquareMatrix<Complex> ad ogni passo sarebbe puro overhead.
	 */
	public static Complex[][] computeQ(Complex[][] a, int m) {
		Complex[][] r = copy(a, m);
		Complex[][] q = identity(m);
		decompose(r, q, m);
		return q;
	}

	private static void decompose(Complex[][] r, Complex[][] q, int m) {
		for (int k = 0; k < m - 1; k++) {
			int len = m - k;
			Complex[] x = new Complex[len];
			double normX = 0.0;
			for (int i = 0; i < len; i++) {
				x[i] = r[k + i][k];
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

			applyHouseholderLeft(r, v, vNormSquared, k, m);
			applyHouseholderRight(q, v, vNormSquared, k, m);
		}
	}

	/** r[k..m-1][k..m-1] := (I - 2vv^dagger/v^dagger v) * r[k..m-1][k..m-1]. */
	private static void applyHouseholderLeft(Complex[][] r, Complex[] v, double vNormSquared, int k, int m) {
		int len = v.length;
		for (int col = k; col < m; col++) {
			Complex dot = C.zero();
			for (int i = 0; i < len; i++) {
				dot = C.add(dot, C.multiply(v[i].conjugate(), r[k + i][col]));
			}
			Complex factor = C.multiply(new Complex(2.0 / vNormSquared, 0.0), dot);
			for (int i = 0; i < len; i++) {
				r[k + i][col] = C.subtract(r[k + i][col], C.multiply(factor, v[i]));
			}
		}
	}

	/** q[:, k..m-1] := q[:, k..m-1] * (I - 2vv^dagger/v^dagger v). */
	private static void applyHouseholderRight(Complex[][] q, Complex[] v, double vNormSquared, int k, int m) {
		int len = v.length;
		for (int row = 0; row < m; row++) {
			Complex dot = C.zero();
			for (int i = 0; i < len; i++) {
				dot = C.add(dot, C.multiply(q[row][k + i], v[i]));
			}
			Complex factor = C.multiply(new Complex(2.0 / vNormSquared, 0.0), dot);
			for (int i = 0; i < len; i++) {
				q[row][k + i] = C.subtract(q[row][k + i], C.multiply(factor, v[i].conjugate()));
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
