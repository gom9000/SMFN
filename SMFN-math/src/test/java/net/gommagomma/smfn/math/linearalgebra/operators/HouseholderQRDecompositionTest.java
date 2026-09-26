package net.gommagomma.smfn.math.linearalgebra.operators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

@DisplayName("HouseholderQRDecomposition: A = Q*R su matrici complesse, Q unitaria, R triangolare superiore")
class HouseholderQRDecompositionTest
{
	private static final ComplexField C = ComplexField.INSTANCE;

	private static Complex c(double re, double im) {
		return new Complex(re, im);
	}

	/** max_ij |a-b|, per confronti a tolleranza fissa senza ripetere Math.hypot ovunque. */
	private static double maxAbsDiff(SquareMatrix<Complex> a, SquareMatrix<Complex> b) {
		int n = a.getN();
		double max = 0.0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Complex d = C.subtract(a.get(i, j), b.get(i, j));
				max = Math.max(max, d.modulus());
			}
		}
		return max;
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

	@Test
	@DisplayName("Matrice complessa 3x3 generica: Q*R ricostruisce A")
	void reconstructsOriginalMatrix() {
		SquareMatrix<Complex> A = SquareMatrixElementFactory.of(C,
			c(2, 1), c(0, -1), c(1, 0),
			c(1, 0), c(3, 0), c(0, 2),
			c(0, 1), c(1, -1), c(4, 0)
		);

		HouseholderQRDecomposition qr = new HouseholderQRDecomposition(A);
		SquareMatrix<Complex> reconstructed = multiply(qr.getQ(), qr.getR());

		assertTrue(maxAbsDiff(A, reconstructed) < 1e-9);
	}

	@Test
	@DisplayName("Q e' unitaria: Q^dagger * Q = I")
	void qIsUnitary() {
		SquareMatrix<Complex> A = SquareMatrixElementFactory.of(C,
			c(2, 1), c(0, -1), c(1, 0),
			c(1, 0), c(3, 0), c(0, 2),
			c(0, 1), c(1, -1), c(4, 0)
		);

		HouseholderQRDecomposition qr = new HouseholderQRDecomposition(A);
		SquareMatrix<Complex> q = qr.getQ();
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
	@DisplayName("R e' triangolare superiore: tutto sotto la diagonale e' zero")
	void rIsUpperTriangular() {
		SquareMatrix<Complex> A = SquareMatrixElementFactory.of(C,
			c(2, 1), c(0, -1), c(1, 0),
			c(1, 0), c(3, 0), c(0, 2),
			c(0, 1), c(1, -1), c(4, 0)
		);

		HouseholderQRDecomposition qr = new HouseholderQRDecomposition(A);
		SquareMatrix<Complex> r = qr.getR();

		int n = r.getN();
		for (int i = 1; i < n; i++) {
			for (int j = 0; j < i; j++) {
				assertEquals(0.0, r.get(i, j).modulus(), 1e-9);
			}
		}
	}

	@Test
	@DisplayName("Matrice gia' reale (parte immaginaria nulla): stessa QR di una fattorizzazione reale nota")
	void handlesRealMatrixEmbeddedInComplex() {
		SquareMatrix<Complex> A = SquareMatrixElementFactory.of(C,
			c(0, 0), c(-1, 0),
			c(1, 0), c(0, 0)
		);

		HouseholderQRDecomposition qr = new HouseholderQRDecomposition(A);
		SquareMatrix<Complex> reconstructed = multiply(qr.getQ(), qr.getR());

		assertTrue(maxAbsDiff(A, reconstructed) < 1e-9);
	}

	@Test
	@DisplayName("computeQ su array grezzi produce la stessa Q dell'API su SquareMatrix<Complex>")
	void rawArrayFastPathAgreesWithObjectApi() {
		Complex[][] raw = {
			{ c(2, 1), c(0, -1), c(1, 0) },
			{ c(1, 0), c(3, 0), c(0, 2) },
			{ c(0, 1), c(1, -1), c(4, 0) }
		};
		SquareMatrix<Complex> A = SquareMatrixElementFactory.of(C,
			raw[0][0], raw[0][1], raw[0][2],
			raw[1][0], raw[1][1], raw[1][2],
			raw[2][0], raw[2][1], raw[2][2]
		);

		Complex[][] qRaw = HouseholderQRDecomposition.computeQ(raw, 3);
		SquareMatrix<Complex> qObject = new HouseholderQRDecomposition(A).getQ();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				assertTrue(C.subtract(qRaw[i][j], qObject.get(i, j)).modulus() < 1e-12);
			}
		}
	}
}
