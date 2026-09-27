package net.gommagomma.smfn.math.linearalgebra.operators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Invarianti di HouseholderQRDecomposition e HessenbergReduction sul catalogo di
 * OperatorTestValues. Generalizza i *Test esistenti (che verificano le stesse proprieta' su una
 * singola matrice hardcoded) a un piccolo insieme di matrici di dimensioni e strutture diverse,
 * e aggiunge un invariante non gia' coperto: la similitudine di Hessenberg preserva traccia e
 * determinante (proprieta' note di qualunque trasformazione di similitudine, verificate contro
 * il calcolo indipendente di SquareMatrixAlgebra).
 */
@DisplayName("HouseholderQRDecomposition / HessenbergReduction: invarianti sul catalogo di matrici complesse")
class OperatorInvariantsTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private static final double EPSILON = 1e-9;

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

	private static void assertIsUnitary(SquareMatrix<Complex> q, String context) {
		SquareMatrix<Complex> identity = multiply(q.conjugateTranspose(), q);
		int n = identity.getN();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Complex expected = (i == j) ? C.one() : C.zero();
				assertTrue(C.subtract(identity.get(i, j), expected).modulus() < EPSILON, context);
			}
		}
	}

	@Test
	@DisplayName("HouseholderQRDecomposition: Q*R ricostruisce A, su tutto il catalogo")
	void qrReconstructsOriginalMatrix() {
		for (SquareMatrix<Complex> a : OperatorTestValues.matrices()) {
			HouseholderQRDecomposition qr = new HouseholderQRDecomposition(a);
			SquareMatrix<Complex> reconstructed = multiply(qr.getQ(), qr.getR());
			assertTrue(maxAbsDiff(a, reconstructed) < EPSILON, "matrice: " + a);
		}
	}

	@Test
	@DisplayName("HouseholderQRDecomposition: Q e' unitaria e R e' triangolare superiore, su tutto il catalogo")
	void qrProducesUnitaryQAndTriangularR() {
		for (SquareMatrix<Complex> a : OperatorTestValues.matrices()) {
			HouseholderQRDecomposition qr = new HouseholderQRDecomposition(a);
			assertIsUnitary(qr.getQ(), "matrice: " + a);

			SquareMatrix<Complex> r = qr.getR();
			int n = r.getN();
			for (int i = 1; i < n; i++) {
				for (int j = 0; j < i; j++) {
					assertTrue(r.get(i, j).modulus() < EPSILON, "R non triangolare per matrice: " + a);
				}
			}
		}
	}

	@Test
	@DisplayName("HessenbergReduction: Q^dagger*A*Q ricostruisce H (similitudine corretta), su tutto il catalogo")
	void hessenbergSimilarityHoldsExactly() {
		for (SquareMatrix<Complex> a : OperatorTestValues.matrices()) {
			HessenbergReduction reduction = new HessenbergReduction(a);
			SquareMatrix<Complex> reconstructed = multiply(multiply(reduction.getQ().conjugateTranspose(), a), reduction.getQ());
			assertTrue(maxAbsDiff(reduction.getH(), reconstructed) < EPSILON, "matrice: " + a);
		}
	}

	@Test
	@DisplayName("HessenbergReduction: Q e' unitaria e H e' nulla sotto la prima sottodiagonale, su tutto il catalogo")
	void hessenbergProducesUnitaryQAndUpperHessenbergH() {
		for (SquareMatrix<Complex> a : OperatorTestValues.matrices()) {
			HessenbergReduction reduction = new HessenbergReduction(a);
			assertIsUnitary(reduction.getQ(), "matrice: " + a);

			SquareMatrix<Complex> h = reduction.getH();
			int n = h.getN();
			for (int i = 2; i < n; i++) {
				for (int j = 0; j < i - 1; j++) {
					assertTrue(h.get(i, j).modulus() < EPSILON, "H non e' upper Hessenberg per matrice: " + a);
				}
			}
		}
	}

	@Test
	@DisplayName("HessenbergReduction preserva traccia e determinante (invarianti di ogni similitudine), su tutto il catalogo")
	void hessenbergPreservesTraceAndDeterminant() {
		for (SquareMatrix<Complex> a : OperatorTestValues.matrices()) {
			int n = a.getN();
			SquareMatrixAlgebra<Complex, ComplexField> ring = new SquareMatrixAlgebra<>(C, n);
			HessenbergReduction reduction = new HessenbergReduction(a);
			SquareMatrix<Complex> h = reduction.getH();

			Complex traceA = a.trace();
			Complex traceH = h.trace();
			assertTrue(C.subtract(traceA, traceH).modulus() < EPSILON, "traccia non preservata per matrice: " + a);

			Complex detA = ring.determinant(a);
			Complex detH = ring.determinant(h);
			assertTrue(C.subtract(detA, detH).modulus() < 1e-6, "determinante non preservato per matrice: " + a);
		}
	}
}
