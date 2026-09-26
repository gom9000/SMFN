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
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.ResidualAware;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.StepDistanceAware;
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
		EigenDecomposition result = solver.solve(knownMatrix(), params).getValue();

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
		EigenDecomposition result = solver.solve(knownMatrix(), params).getValue();
		for (Complex lambda : result.getEigenvalues()) {
			assertEquals(0.0, lambda.getIm(), 1e-9);
		}
	}

	@Test
	@DisplayName("H*v == lambda*v per ciascuna coppia -- verifica indipendente, aritmetica complessa")
	void eigenpairsSatisfyDefiningEquation() {
		SquareMatrix<Complex> H = knownMatrix();
		EigenDecomposition result = solver.solve(H, params).getValue();

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
		EigenDecomposition result = solver.solve(knownMatrix(), params).getValue();
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

	@Test
	@DisplayName("Caso convergente: SolverResult riporta CONVERGED e non e' ne' StepDistanceAware ne' ResidualAware")
	void convergedResultHasNoOptionalCapabilities() {
		SolverResult<EigenDecomposition> result = solver.solve(knownMatrix(), params);

		assertEquals(ConvergenceStatus.CONVERGED, result.getStatus());
		assertTrue(result.getIterationsExecuted() < params.maxIterations);
		assertEquals(false, result instanceof StepDistanceAware);
		assertEquals(false, result instanceof ResidualAware);
	}

	@Test
	@DisplayName("Budget di iterazioni insufficiente: MAX_ITERATIONS_REACHED con la migliore decomposizione disponibile, non un'eccezione")
	void insufficientIterationBudgetReportsMaxIterationsReached() {
		ConvergenceParameters tightBudget = new ConvergenceParameters(new Real(1e-12), 1);

		SolverResult<EigenDecomposition> result = solver.solve(knownMatrix(), tightBudget);

		assertEquals(ConvergenceStatus.MAX_ITERATIONS_REACHED, result.getStatus());
		assertEquals(1, result.getIterationsExecuted());
		assertEquals(2, result.getValue().getEigenvalues().size());
	}
}
