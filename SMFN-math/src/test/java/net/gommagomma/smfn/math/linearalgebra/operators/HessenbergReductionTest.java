package net.gommagomma.smfn.math.linearalgebra.operators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

@DisplayName("HessenbergReduction: Q^dagger*A*Q = H, Q unitaria, H nulla sotto la prima sottodiagonale")
class HessenbergReductionTest
{
	private static final ComplexField C = ComplexField.INSTANCE;

	private static Complex c(double re, double im) {
		return new Complex(re, im);
	}

	private static SquareMatrix<Complex> multiply(SquareMatrix<Complex> a, SquareMatrix<Complex> b) {
		int n = a.getN();
		Complex[] data = new Complex[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Complex sum = C.zero();
				for (int k = 0; k < n; k++) {
					sum = C.add(sum, C.multiply(a.get(i, k), b.get(k, j)));
				}
				data[i * n + j] = sum;
			}
		}
		return SquareMatrixElementFactory.of(C, java.util.List.of(data));
	}

	private static double maxAbsDiff(SquareMatrix<Complex> a, SquareMatrix<Complex> b) {
		int n = a.getN();
		double max = 0.0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				max = Math.max(max, C.subtract(a.get(i, j), b.get(i, j)).modulus());
			}
		}
		return max;
	}

	private static SquareMatrix<Complex> exampleMatrix() {
		return SquareMatrixElementFactory.of(C,
			c(4, 1), c(1, -1), c(0, 2), c(2, 0),
			c(1, 0), c(3, 2), c(1, 1), c(0, -1),
			c(2, -1), c(0, 1), c(5, 0), c(1, 0),
			c(0, 1), c(2, 0), c(1, -2), c(2, 1)
		);
	}

	@Test
	@DisplayName("Q^dagger * A * Q ricostruisce H (similitudine corretta)")
	void similarityHoldsExactly() {
		SquareMatrix<Complex> A = exampleMatrix();
		HessenbergReduction reduction = new HessenbergReduction(A);
		SquareMatrix<Complex> q = reduction.getQ();
		SquareMatrix<Complex> h = reduction.getH();

		SquareMatrix<Complex> reconstructed = multiply(multiply(q.conjugateTranspose(), A), q);
		assertTrue(maxAbsDiff(h, reconstructed) < 1e-9);
	}

	@Test
	@DisplayName("H e' nulla ovunque sotto la prima sottodiagonale")
	void hIsUpperHessenberg() {
		SquareMatrix<Complex> h = new HessenbergReduction(exampleMatrix()).getH();
		int n = h.getN();
		for (int i = 2; i < n; i++) {
			for (int j = 0; j < i - 1; j++) {
				assertTrue(h.get(i, j).modulus() < 1e-9,
					"h[" + i + "][" + j + "] dovrebbe essere ~0, e' " + h.get(i, j));
			}
		}
	}

	@Test
	@DisplayName("Q e' unitaria: Q^dagger * Q = I")
	void qIsUnitary() {
		SquareMatrix<Complex> q = new HessenbergReduction(exampleMatrix()).getQ();
		SquareMatrix<Complex> identity = multiply(q.conjugateTranspose(), q);
		int n = identity.getN();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Complex expected = (i == j) ? C.one() : C.zero();
				assertTrue(C.subtract(identity.get(i, j), expected).modulus() < 1e-9);
			}
		}
	}

	@Test
	@DisplayName("Matrice gia' upper Hessenberg: la riduzione produce comunque una similitudine valida")
	void alreadyHessenbergMatrixStaysValid() {
		// La riflessione di Householder non e' unica (la scelta di fase puo' produrre un'altra
		// Hessenberg valida anche a partire da un input gia' in quella forma): non ci si aspetta
		// H identica ad A byte per byte, solo che resti una similitudine corretta e Hessenberg.
		SquareMatrix<Complex> A = SquareMatrixElementFactory.of(C,
			c(2, 0), c(1, 1), c(0, -1),
			c(3, 0), c(4, 0), c(1, 0),
			c(0, 0), c(2, 0), c(5, 0)
		);
		HessenbergReduction reduction = new HessenbergReduction(A);
		SquareMatrix<Complex> reconstructed = multiply(multiply(reduction.getQ().conjugateTranspose(), A), reduction.getQ());
		assertTrue(maxAbsDiff(reduction.getH(), reconstructed) < 1e-9);
		assertTrue(reduction.getH().get(2, 0).modulus() < 1e-9);
	}

	@Test
	@DisplayName("computeQ/reduce su array grezzi produce lo stesso risultato dell'API su SquareMatrix<Complex>")
	void rawArrayApiAgreesWithObjectApi() {
		SquareMatrix<Complex> A = exampleMatrix();
		int n = A.getN();
		Complex[][] raw = new Complex[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				raw[i][j] = A.get(i, j);
			}
		}

		HessenbergReduction.Result rawResult = HessenbergReduction.reduce(raw, n);
		HessenbergReduction objectResult = new HessenbergReduction(A);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				assertTrue(C.subtract(rawResult.h[i][j], objectResult.getH().get(i, j)).modulus() < 1e-12);
				assertTrue(C.subtract(rawResult.q[i][j], objectResult.getQ().get(i, j)).modulus() < 1e-12);
			}
		}
	}
}
