package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.ResidualAware;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.StepDistanceAware;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

@DisplayName("QREigenvalueSolver: autovalori/autovettori di matrici quadrate qualunque, senza simmetria/hermitianita'")
class QREigenvalueSolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private final QREigenvalueSolver<Real> solver = new QREigenvalueSolver<>();
	private final StoppingParameters params = new StoppingParameters(new Real(1e-12), 500);

	/** Verifica indipendente A*v == lambda*v, senza fidarsi del solver stesso. */
	private static double maxDefiningEquationError(SquareMatrix<Real> A, EigenDecomposition d) {
		int n = A.getN();
		List<Complex> eigenvalues = d.getEigenvalues();
		List<Vector<Complex>> eigenvectors = d.getEigenvectors();
		double maxErr = 0.0;

		for (int idx = 0; idx < eigenvalues.size(); idx++) {
			Complex lambda = eigenvalues.get(idx);
			Vector<Complex> v = eigenvectors.get(idx);
			for (int i = 0; i < n; i++) {
				double sumRe = 0.0, sumIm = 0.0;
				for (int j = 0; j < n; j++) {
					double aij = A.get(i, j).getValue();
					Complex vj = v.get(j);
					sumRe += aij * vj.getRe();
					sumIm += aij * vj.getIm();
				}
				Complex vi = v.get(i);
				double expectedRe = lambda.getRe() * vi.getRe() - lambda.getIm() * vi.getIm();
				double expectedIm = lambda.getRe() * vi.getIm() + lambda.getIm() * vi.getRe();
				double err = Math.hypot(sumRe - expectedRe, sumIm - expectedIm);
				maxErr = Math.max(maxErr, err);
			}
		}
		return maxErr;
	}

	@Test
	@DisplayName("Caso 2x2 reale noto: A=[[1,2],[3,4]], autovalori (5+-sqrt(33))/2 calcolati a mano")
	void solvesKnownRealTwoByTwoCase() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0);
		EigenDecomposition result = solver.solve(A, params).getValue();

		double sqrt33 = Math.sqrt(33.0);
		double expectedLow = (5.0 - sqrt33) / 2.0;
		double expectedHigh = (5.0 + sqrt33) / 2.0;

		List<Complex> eigenvalues = result.getEigenvalues();
		assertEquals(2, eigenvalues.size());
		double v0 = eigenvalues.get(0).getRe();
		double v1 = eigenvalues.get(1).getRe();
		assertTrue((Math.abs(v0 - expectedLow) < 1e-9 && Math.abs(v1 - expectedHigh) < 1e-9)
			|| (Math.abs(v0 - expectedHigh) < 1e-9 && Math.abs(v1 - expectedLow) < 1e-9));

		assertTrue(maxDefiningEquationError(A, result) < 1e-9);
	}

	@Test
	@DisplayName("Caso 2x2 reale con autovalori complessi coniugati: A=[[0,-1],[1,0]], autovalori +-i calcolati a mano")
	void solvesRealMatrixWithComplexConjugateEigenvalues() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 0.0, -1.0, 1.0, 0.0);
		EigenDecomposition result = solver.solve(A, params).getValue();

		List<Complex> eigenvalues = result.getEigenvalues();
		assertEquals(2, eigenvalues.size());
		for (Complex lambda : eigenvalues) {
			assertEquals(0.0, lambda.getRe(), 1e-9);
			assertEquals(1.0, Math.abs(lambda.getIm()), 1e-9);
		}
		assertTrue((eigenvalues.get(0).getIm() > 0) != (eigenvalues.get(1).getIm() > 0));

		assertTrue(maxDefiningEquationError(A, result) < 1e-9);
	}

	@Test
	@DisplayName("Caso 3x3 generico con un autovalore reale e una coppia complessa coniugata -- verificati con numpy")
	void solvesThreeByThreeCaseWithMixedRealAndComplexPair() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
			2.0, -1.0, 0.0,
			0.0, 2.0, -1.0,
			1.0, 0.0, 3.0
		);
		EigenDecomposition result = solver.solve(A, params).getValue();

		List<Complex> eigenvalues = result.getEigenvalues();
		assertEquals(3, eigenvalues.size());

		long realCount = eigenvalues.stream().filter(c -> Math.abs(c.getIm()) < 1e-6).count();
		assertEquals(1, realCount);

		boolean hasRealEigenvalue = eigenvalues.stream()
			.anyMatch(c -> Math.abs(c.getIm()) < 1e-6 && Math.abs(c.getRe() - 3.4655712319) < 1e-6);
		boolean hasComplexPair = eigenvalues.stream()
			.anyMatch(c -> Math.abs(c.getRe() - 1.7672143841) < 1e-6 && Math.abs(c.getIm() - 0.7925519925) < 1e-6)
			&& eigenvalues.stream()
			.anyMatch(c -> Math.abs(c.getRe() - 1.7672143841) < 1e-6 && Math.abs(c.getIm() + 0.7925519925) < 1e-6);
		assertTrue(hasRealEigenvalue);
		assertTrue(hasComplexPair);

		assertTrue(maxDefiningEquationError(A, result) < 1e-8);
	}

	@Test
	@DisplayName("Matrice simmetrica: stessi autovalori di JacobiEigenvalueSolver, anche senza sfruttare la simmetria")
	void agreesWithJacobiOnSymmetricMatrix() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
			4.0, 1.0, -2.0,
			1.0, 5.0, 1.0,
			-2.0, 1.0, 6.0
		);
		EigenDecomposition qrResult = solver.solve(A, params).getValue();
		EigenDecomposition jacobiResult = new JacobiEigenvalueSolver().solve(A, params).getValue();

		List<Double> qrValues = new java.util.ArrayList<>();
		for (Complex c : qrResult.getEigenvalues()) {
			qrValues.add(c.getRe());
		}
		List<Double> jacobiValues = new java.util.ArrayList<>();
		for (Complex c : jacobiResult.getEigenvalues()) {
			jacobiValues.add(c.getRe());
		}
		java.util.Collections.sort(qrValues);
		java.util.Collections.sort(jacobiValues);

		for (int i = 0; i < qrValues.size(); i++) {
			assertEquals(jacobiValues.get(i), qrValues.get(i), 1e-8);
		}
	}

	@Test
	@DisplayName("Matrice difettiva (blocco di Jordan) A=[[1,1],[0,1]]: autovalore doppio, nessuna eccezione")
	void handlesDefectiveMatrixWithoutThrowing() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 1.0, 1.0, 0.0, 1.0);
		SolverResult<EigenDecomposition> result = solver.solve(A, params);

		List<Complex> eigenvalues = result.getValue().getEigenvalues();
		assertEquals(2, eigenvalues.size());
		for (Complex lambda : eigenvalues) {
			assertEquals(1.0, lambda.getRe(), 1e-9);
			assertEquals(0.0, lambda.getIm(), 1e-9);
		}
	}

	@Test
	@DisplayName("Caso convergente: SolverResult riporta CONVERGED e non e' ne' StepDistanceAware ne' ResidualAware")
	void convergedResultHasNoOptionalCapabilities() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0);
		SolverResult<EigenDecomposition> result = solver.solve(A, params);

		assertEquals(TerminationStatus.CONVERGED, result.getStatus());
		assertTrue(result.getIterationsExecuted() >= 0);
		assertTrue(result.getIterationsExecuted() < params.maxIterations);
		assertEquals(false, result instanceof StepDistanceAware);
		assertEquals(false, result instanceof ResidualAware);
	}

	@Test
	@DisplayName("Budget di iterazioni insufficiente: MAX_ITERATIONS_REACHED con la migliore decomposizione disponibile, non un'eccezione")
	void insufficientIterationBudgetReportsMaxIterationsReached() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0);
		StoppingParameters tightBudget = new StoppingParameters(new Real(1e-12), 1);

		SolverResult<EigenDecomposition> result = solver.solve(A, tightBudget);

		assertEquals(TerminationStatus.MAX_ITERATIONS_REACHED, result.getStatus());
		assertEquals(1, result.getIterationsExecuted());
		assertEquals(2, result.getValue().getEigenvalues().size());
	}
}
