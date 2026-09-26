package net.gommagomma.smfn.math.analysis.numerical.solvers.linear;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.LinearSystemProblem;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

@DisplayName("GaussianEliminationSolver: risoluzione diretta di Ax = b")
class GaussianEliminationSolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private final VectorSpace<Real, RealField> V3 = new VectorSpace<>(R, 3);

	@Test
	@DisplayName("Sistema 3x3 noto: soluzione esatta, calcolata a mano indipendentemente dal codice")
	void solvesKnownThreeByThreeSystem() {
		// 3x + 2y -  z =  1
		// 2x - 2y + 4z = -2
		// -x + 0.5y - z =  0
		// Soluzione attesa: x=1, y=-2, z=-2 (verificata per sostituzione)
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
			3.0,  2.0, -1.0,
			2.0, -2.0,  4.0,
		   -1.0,  0.5, -1.0
		);
		Vector<Real> b = VectorElementFactory.of(V3, 1.0, -2.0, 0.0);

		GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 3);
		Vector<Real> x = solver.solve(A, b);

		assertEquals(1.0, x.get(0).getValue(), 1e-9);
		assertEquals(-2.0, x.get(1).getValue(), 1e-9);
		assertEquals(-2.0, x.get(2).getValue(), 1e-9);
	}

	@Test
	@DisplayName("A*x ricostruisce b -- verifica indipendente dal metodo di risoluzione")
	void solutionSatisfiesOriginalSystem() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
			3.0,  2.0, -1.0,
			2.0, -2.0,  4.0,
		   -1.0,  0.5, -1.0
		);
		Vector<Real> b = VectorElementFactory.of(V3, 1.0, -2.0, 0.0);

		GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 3);
		Vector<Real> x = solver.solve(A, b);
		Vector<Real> reconstructed = A.apply(x);

		for (int i = 0; i < 3; i++) {
			assertEquals(b.get(i).getValue(), reconstructed.get(i).getValue(), 1e-9);
		}
	}

	@Test
	@DisplayName("Matrice singolare lancia ArithmeticException, non un risultato instabile")
	void singularMatrixThrows() {
		// Terza riga = combinazione lineare delle prime due
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
			1.0, 2.0, 3.0,
			2.0, 4.0, 6.0,
			1.0, 0.0, 1.0
		);
		Vector<Real> b = VectorElementFactory.of(V3, 1.0, 2.0, 3.0);

		GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 3);
		assertThrows(ArithmeticException.class, () -> solver.solve(A, b));
	}

	@Test
	@DisplayName("Sistema 2x2 semplice, soluzione intera esatta")
	void solvesSimpleTwoByTwoSystem() {
		// x + y = 3
		// x - y = 1
		// Soluzione: x=2, y=1
		VectorSpace<Real, RealField> V2 = new VectorSpace<>(R, 2);
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 1.0, 1.0, 1.0, -1.0);
		Vector<Real> b = VectorElementFactory.of(V2, 3.0, 1.0);

		GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 2);
		Vector<Real> x = solver.solve(A, b);

		assertEquals(2.0, x.get(0).getValue(), 1e-9);
		assertEquals(1.0, x.get(1).getValue(), 1e-9);
	}

	@Test
	@DisplayName("solve(LinearSystemProblem) e solve(matrice, rhs) restituiscono la stessa soluzione")
	void solvesViaLinearSystemProblem() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
			3.0,  2.0, -1.0,
			2.0, -2.0,  4.0,
		   -1.0,  0.5, -1.0
		);
		Vector<Real> b = VectorElementFactory.of(V3, 1.0, -2.0, 0.0);

		GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 3);
		LinearSystemProblem<Real> problem = new LinearSystemProblem<>(A, b);
		Vector<Real> x = solver.solve(problem);

		assertEquals(1.0, x.get(0).getValue(), 1e-9);
		assertEquals(-2.0, x.get(1).getValue(), 1e-9);
		assertEquals(-2.0, x.get(2).getValue(), 1e-9);
	}

	@Test
	@DisplayName("LinearSystemProblem rifiuta matrice o rhs nulli")
	void linearSystemProblemRejectsNullFields() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 1.0, 1.0, 1.0, -1.0);
		Vector<Real> b = VectorElementFactory.of(new VectorSpace<>(R, 2), 3.0, 1.0);

		assertThrows(NullPointerException.class, () -> new LinearSystemProblem<>(null, b));
		assertThrows(NullPointerException.class, () -> new LinearSystemProblem<>(A, null));
	}
}
