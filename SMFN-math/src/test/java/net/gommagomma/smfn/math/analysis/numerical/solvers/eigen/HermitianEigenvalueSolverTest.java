package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

@DisplayName("HermitianEigenvalueSolver: autovalori/autovettori di matrici complesse hermitiane")
class HermitianEigenvalueSolverTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private final HermitianEigenvalueSolver solver = new HermitianEigenvalueSolver();
	private final ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);

	private SquareMatrix<Complex> knownMatrix() {
		// H = [[2, 1+i],[1-i, 3]] -- autovalori 1 e 4, calcolati a mano e
		// verificati indipendentemente con numpy.linalg.eigh prima di scrivere
		// il codice.
		return SquareMatrixElementFactory.of(C,
			new Complex(2, 0), new Complex(1, 1),
			new Complex(1, -1), new Complex(3, 0)
		);
	}

	@Test
	@DisplayName("Caso 2x2 noto: autovalori 1 e 4, verificati indipendentemente con numpy")
	void solvesKnownTwoByTwoHermitianCase() {
		EigenDecomposition result = solver.solve(knownMatrix(), params);

		List<Complex> eigenvalues = result.getEigenvalues();
		assertEquals(2, eigenvalues.size());
		double v0 = eigenvalues.get(0).getRe();
		double v1 = eigenvalues.get(1).getRe();
		assertTrue((Math.abs(v0 - 1.0) < 1e-9 && Math.abs(v1 - 4.0) < 1e-9)
			|| (Math.abs(v0 - 4.0) < 1e-9 && Math.abs(v1 - 1.0) < 1e-9));
	}

	@Test
	@DisplayName("Gli autovalori sono reali per costruzione (parte immaginaria nulla)")
	void eigenvaluesAreReal() {
		EigenDecomposition result = solver.solve(knownMatrix(), params);
		for (Complex lambda : result.getEigenvalues()) {
			assertEquals(0.0, lambda.getIm(), 1e-9);
		}
	}

	@Test
	@DisplayName("H*v == lambda*v per ciascuna coppia -- verifica indipendente, aritmetica complessa")
	void eigenpairsSatisfyDefiningEquation() {
		SquareMatrix<Complex> H = knownMatrix();
		EigenDecomposition result = solver.solve(H, params);

		List<Complex> eigenvalues = result.getEigenvalues();
		List<Vector<Complex>> eigenvectors = result.getEigenvectors();

		for (int i = 0; i < eigenvalues.size(); i++) {
			Complex lambda = eigenvalues.get(i);
			Vector<Complex> v = eigenvectors.get(i);
			Vector<Complex> Hv = H.apply(v);
			for (int k = 0; k < 2; k++) {
				Complex expected = C.multiply(lambda, v.get(k));
				assertEquals(expected.getRe(), Hv.get(k).getRe(), 1e-9);
				assertEquals(expected.getIm(), Hv.get(k).getIm(), 1e-9);
			}
		}
	}

	@Test
	@DisplayName("toRealDecomposition() rifiuta: gli autovettori sono genuinamente complessi")
	void eigenvectorsAreNotReal() {
		EigenDecomposition result = solver.solve(knownMatrix(), params);
		assertThrows(IllegalStateException.class, () -> result.toRealDecomposition(new Real(1e-9)));
	}

	@Test
	@DisplayName("Matrice non hermitiana: rifiutata con IllegalArgumentException")
	void rejectsNonHermitianMatrix() {
		SquareMatrix<Complex> nonHermitian = SquareMatrixElementFactory.of(C,
			new Complex(1, 0), new Complex(2, 0),
			new Complex(3, 0), new Complex(4, 0)
		);
		assertThrows(IllegalArgumentException.class, () -> solver.solve(nonHermitian, params));
	}
}
