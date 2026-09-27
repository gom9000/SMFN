package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Invarianti di GeneralEigenvalueSolver (che instrada a Jacobi/Hermitian per matrici
 * simmetriche/hermitiane e a QR per il caso generico) sul catalogo di EigenTestValues.
 * <p>
 * A differenza dei cataloghi di root-finding/sistemi lineari, qui non serve conoscere lo
 * spettro esatto a mano: gli invarianti "traccia == somma degli autovalori" e "determinante ==
 * prodotto degli autovalori" valgono per costruzione algebrica (sono i coefficienti del
 * polinomio caratteristico) e si verificano confrontando col determinante/traccia calcolati
 * indipendentemente da SquareMatrix/SquareMatrixAlgebra -- evitando cosi' l'errore gia' fatto
 * nel catalogo di sistemi lineari, dove una soluzione "attesa" scritta a mano si era rivelata
 * sbagliata. Per le matrici simmetriche/hermitiane si verifica in aggiunta l'equazione
 * caratteristica stessa, A*v == lambda*v.
 */
@DisplayName("GeneralEigenvalueSolver: invarianti algebrici (traccia, determinante, equazione agli autovettori) sul catalogo")
class EigenInvariantsTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final ComplexField C = ComplexField.INSTANCE;
	private static final double EPSILON = 1e-7;
	private static final StoppingParameters PARAMS = new StoppingParameters(new Real(1e-12), 500);

	private static Complex sum(List<Complex> values) {
		Complex total = C.zero();
		for (Complex v : values) {
			total = C.add(total, v);
		}
		return total;
	}

	private static Complex product(List<Complex> values) {
		Complex total = C.one();
		for (Complex v : values) {
			total = C.multiply(total, v);
		}
		return total;
	}

	@Test
	@DisplayName("Traccia == somma degli autovalori, su matrici reali simmetriche e non simmetriche")
	void traceEqualsSumOfEigenvaluesForRealMatrices() {
		GeneralEigenvalueSolver<Real> solver = new GeneralEigenvalueSolver<>();

		for (SquareMatrix<Real> a : concat(EigenTestValues.realSymmetricMatrices(), EigenTestValues.realGeneralMatrices())) {
			SolverResult<EigenDecomposition> result = solver.solve(a, PARAMS);
			Complex eigenvalueSum = sum(result.getValue().getEigenvalues());

			assertEquals(a.trace().getValue(), eigenvalueSum.getRe(), EPSILON, "matrice: " + a);
			assertEquals(0.0, eigenvalueSum.getIm(), EPSILON, "somma delle parti immaginarie non nulla per matrice reale: " + a);
		}
	}

	@Test
	@DisplayName("Determinante == prodotto degli autovalori, su matrici reali simmetriche e non simmetriche")
	void determinantEqualsProductOfEigenvaluesForRealMatrices() {
		GeneralEigenvalueSolver<Real> solver = new GeneralEigenvalueSolver<>();

		for (SquareMatrix<Real> a : concat(EigenTestValues.realSymmetricMatrices(), EigenTestValues.realGeneralMatrices())) {
			SolverResult<EigenDecomposition> result = solver.solve(a, PARAMS);
			Complex eigenvalueProduct = product(result.getValue().getEigenvalues());

			SquareMatrixAlgebra<Real, RealField> ring = new SquareMatrixAlgebra<>(R, a.getN());
			Real determinant = ring.determinant(a);

			assertEquals(determinant.getValue(), eigenvalueProduct.getRe(), EPSILON, "matrice: " + a);
			assertEquals(0.0, eigenvalueProduct.getIm(), EPSILON, "parte immaginaria del prodotto non nulla per matrice reale: " + a);
		}
	}

	@Test
	@DisplayName("Traccia == somma degli autovalori, su matrici complesse hermitiane e non hermitiane")
	void traceEqualsSumOfEigenvaluesForComplexMatrices() {
		GeneralEigenvalueSolver<Complex> solver = new GeneralEigenvalueSolver<>();

		for (SquareMatrix<Complex> a : concat(EigenTestValues.complexHermitianMatrices(), EigenTestValues.complexGeneralMatrices())) {
			SolverResult<EigenDecomposition> result = solver.solve(a, PARAMS);
			Complex eigenvalueSum = sum(result.getValue().getEigenvalues());
			Complex trace = a.trace();

			assertEquals(trace.getRe(), eigenvalueSum.getRe(), EPSILON, "matrice: " + a);
			assertEquals(trace.getIm(), eigenvalueSum.getIm(), EPSILON, "matrice: " + a);
		}
	}

	@Test
	@DisplayName("Determinante == prodotto degli autovalori, su matrici complesse hermitiane e non hermitiane")
	void determinantEqualsProductOfEigenvaluesForComplexMatrices() {
		GeneralEigenvalueSolver<Complex> solver = new GeneralEigenvalueSolver<>();

		for (SquareMatrix<Complex> a : concat(EigenTestValues.complexHermitianMatrices(), EigenTestValues.complexGeneralMatrices())) {
			SolverResult<EigenDecomposition> result = solver.solve(a, PARAMS);
			Complex eigenvalueProduct = product(result.getValue().getEigenvalues());

			SquareMatrixAlgebra<Complex, ComplexField> ring = new SquareMatrixAlgebra<>(C, a.getN());
			Complex determinant = ring.determinant(a);

			assertEquals(determinant.getRe(), eigenvalueProduct.getRe(), EPSILON, "matrice: " + a);
			assertEquals(determinant.getIm(), eigenvalueProduct.getIm(), EPSILON, "matrice: " + a);
		}
	}

	@Test
	@DisplayName("A*v == lambda*v per ciascuna coppia autovalore/autovettore, su matrici reali simmetriche")
	void eigenpairsSatisfyDefiningEquationForRealSymmetricMatrices() {
		JacobiEigenvalueSolver solver = new JacobiEigenvalueSolver();

		for (SquareMatrix<Real> a : EigenTestValues.realSymmetricMatrices()) {
			EigenDecomposition result = solver.solve(a, PARAMS).getValue();
			List<Complex> eigenvalues = result.getEigenvalues();
			List<Vector<Complex>> eigenvectors = result.getEigenvectors();

			for (int i = 0; i < eigenvalues.size(); i++) {
				Complex lambda = eigenvalues.get(i);
				Vector<Complex> v = eigenvectors.get(i);
				Vector<Real> vReal = toRealVector(v);
				Vector<Real> av = a.apply(vReal);

				for (int k = 0; k < av.size(); k++) {
					Complex expected = C.multiply(lambda, v.get(k));
					assertEquals(expected.getRe(), av.get(k).getValue(), EPSILON, "matrice: " + a);
				}
			}
		}
	}

	@Test
	@DisplayName("H*v == lambda*v per ciascuna coppia autovalore/autovettore, su matrici complesse hermitiane")
	void eigenpairsSatisfyDefiningEquationForHermitianMatrices() {
		HermitianEigenvalueSolver solver = new HermitianEigenvalueSolver();

		for (SquareMatrix<Complex> h : EigenTestValues.complexHermitianMatrices()) {
			EigenDecomposition result = solver.solve(h, PARAMS).getValue();
			List<Complex> eigenvalues = result.getEigenvalues();
			List<Vector<Complex>> eigenvectors = result.getEigenvectors();

			for (int i = 0; i < eigenvalues.size(); i++) {
				Complex lambda = eigenvalues.get(i);
				Vector<Complex> v = eigenvectors.get(i);
				Vector<Complex> hv = h.apply(v);

				for (int k = 0; k < hv.size(); k++) {
					Complex expected = C.multiply(lambda, v.get(k));
					assertEquals(expected.getRe(), hv.get(k).getRe(), EPSILON, "matrice: " + h);
					assertEquals(expected.getIm(), hv.get(k).getIm(), EPSILON, "matrice: " + h);
				}
			}
		}
	}

	@Test
	@DisplayName("Con budget adeguato lo status e' sempre CONVERGED, mai MAX_ITERATIONS_REACHED, su tutto il catalogo")
	void adequateBudgetAlwaysConverges() {
		GeneralEigenvalueSolver<Real> realSolver = new GeneralEigenvalueSolver<>();
		for (SquareMatrix<Real> a : concat(EigenTestValues.realSymmetricMatrices(), EigenTestValues.realGeneralMatrices())) {
			SolverResult<EigenDecomposition> result = realSolver.solve(a, PARAMS);
			assertEquals(TerminationStatus.CONVERGED, result.getStatus(), "matrice: " + a);
			assertTrue(result.getIterationsExecuted() < PARAMS.maxIterations, "matrice: " + a);
		}

		GeneralEigenvalueSolver<Complex> complexSolver = new GeneralEigenvalueSolver<>();
		for (SquareMatrix<Complex> a : concat(EigenTestValues.complexHermitianMatrices(), EigenTestValues.complexGeneralMatrices())) {
			SolverResult<EigenDecomposition> result = complexSolver.solve(a, PARAMS);
			assertEquals(TerminationStatus.CONVERGED, result.getStatus(), "matrice: " + a);
			assertTrue(result.getIterationsExecuted() < PARAMS.maxIterations, "matrice: " + a);
		}
	}

	private static Vector<Real> toRealVector(Vector<Complex> v) {
		int n = (int) v.size();
		net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace<Real, RealField> space =
			new net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace<>(R, n);
		Real[] data = new Real[n];
		for (int i = 0; i < n; i++) {
			data[i] = new Real(v.get(i).getRe());
		}
		return net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory.of(space, List.of(data));
	}

	@SafeVarargs
	private static <T> List<T> concat(List<T>... lists) {
		List<T> result = new java.util.ArrayList<>();
		for (List<T> list : lists) {
			result.addAll(list);
		}
		return result;
	}
}
