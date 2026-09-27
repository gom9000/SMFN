package net.gommagomma.smfn.math.analysis.numerical.solvers.linear;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.LinearSystemProblem;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Invarianti di GaussianEliminationSolver sul catalogo di LinearSystemTestValues, sullo stesso
 * spirito delle *InvariantsTest di algebra/linearalgebra/analysis.roots. A differenza delle leggi
 * algebriche pure, qui l'invariante centrale e' che la soluzione trovata soddisfi davvero il
 * sistema originale (A*x == b), verificato indipendentemente dal fatto che coincida con la
 * soluzione attesa calcolata a mano; e che una matrice singolare non produca mai un risultato
 * silenziosamente instabile, ma sollevi sempre ArithmeticException.
 */
@DisplayName("GaussianEliminationSolver: invarianti sul catalogo di sistemi lineari (ben condizionati e singolari)")
class LinearSystemInvariantsTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final double EPSILON = 1e-9;

	@Test
	@DisplayName("La soluzione trovata coincide con quella attesa, calcolata a mano, su tutto il catalogo")
	void solutionMatchesExpectedValue() {
		for (LinearSystemTestValues.WellConditionedCase testCase : LinearSystemTestValues.wellConditionedCases()) {
			int n = (int) testCase.b.size();
			GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, n);

			Vector<Real> x = solver.solve(testCase.a, testCase.b);

			for (int i = 0; i < n; i++) {
				assertEquals(testCase.expectedSolution.get(i).getValue(), x.get(i).getValue(), EPSILON, "caso: " + testCase.name);
			}
		}
	}

	@Test
	@DisplayName("A*x ricostruisce b -- verifica indipendente dalla soluzione attesa, su tutto il catalogo")
	void solutionSatisfiesOriginalSystem() {
		for (LinearSystemTestValues.WellConditionedCase testCase : LinearSystemTestValues.wellConditionedCases()) {
			int n = (int) testCase.b.size();
			GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, n);

			Vector<Real> x = solver.solve(testCase.a, testCase.b);
			Vector<Real> reconstructed = testCase.a.apply(x);

			for (int i = 0; i < n; i++) {
				assertEquals(testCase.b.get(i).getValue(), reconstructed.get(i).getValue(), EPSILON, "caso: " + testCase.name);
			}
		}
	}

	@Test
	@DisplayName("solve(LinearSystemProblem) e solve(A,b) restituiscono la stessa soluzione, su tutto il catalogo")
	void bothSolveOverloadsAgree() {
		for (LinearSystemTestValues.WellConditionedCase testCase : LinearSystemTestValues.wellConditionedCases()) {
			int n = (int) testCase.b.size();
			GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, n);

			Vector<Real> direct = solver.solve(testCase.a, testCase.b);
			Vector<Real> viaProblem = solver.solve(new LinearSystemProblem<>(testCase.a, testCase.b));

			for (int i = 0; i < n; i++) {
				assertEquals(direct.get(i).getValue(), viaProblem.get(i).getValue(), 0.0, "caso: " + testCase.name);
			}
		}
	}

	@Test
	@DisplayName("Una matrice singolare lancia sempre ArithmeticException, mai un risultato instabile silenzioso")
	void singularMatricesAlwaysThrow() {
		for (SquareMatrix<Real> singular : LinearSystemTestValues.singularCases()) {
			int n = singular.getRows();
			GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, n);
			Vector<Real> anyRhs = VectorElementFactory.of(new VectorSpace<>(R, n), new double[n]);

			assertThrows(ArithmeticException.class, () -> solver.solve(singular, anyRhs));
		}
	}
}
