package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

@DisplayName("JacobiEigenvalueSolver: autovalori/autovettori di matrici reali simmetriche")
class JacobiEigenvalueSolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private final JacobiEigenvalueSolver solver = new JacobiEigenvalueSolver();
	private final ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);

	@Test
	@DisplayName("Caso 2x2 noto: A=[[2,1],[1,2]], autovalori 1 e 3, calcolati a mano")
	void solvesKnownTwoByTwoCase() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 2.0);
		EigenDecomposition result = solver.solve(A, params);

		List<Complex> eigenvalues = result.getEigenvalues();
		assertEquals(2, eigenvalues.size());
		// L'ordine dipende dall'algoritmo: verifichiamo l'insieme, non la posizione
		double v0 = eigenvalues.get(0).getRe();
		double v1 = eigenvalues.get(1).getRe();
		assertTrue((Math.abs(v0 - 1.0) < 1e-9 && Math.abs(v1 - 3.0) < 1e-9)
			|| (Math.abs(v0 - 3.0) < 1e-9 && Math.abs(v1 - 1.0) < 1e-9));
	}

	@Test
	@DisplayName("A*v == lambda*v per ciascuna coppia autovalore/autovettore -- verifica indipendente")
	void eigenpairsSatisfyDefiningEquation() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 2.0);
		EigenDecomposition result = solver.solve(A, params);
		RealEigenDecomposition real = result.toRealDecomposition(new Real(1e-9));

		List<Real> eigenvalues = real.getEigenvalues();
		List<Vector<Real>> eigenvectors = real.getEigenvectors();

		for (int i = 0; i < eigenvalues.size(); i++) {
			Real lambda = eigenvalues.get(i);
			Vector<Real> v = eigenvectors.get(i);
			Vector<Real> Av = A.apply(v);
			for (int k = 0; k < 2; k++) {
				double expected = lambda.getValue() * v.get(k).getValue();
				assertEquals(expected, Av.get(k).getValue(), 1e-9);
			}
		}
	}

	@Test
	@DisplayName("Matrice diagonale 3x3: autovalori ovvi, nessuna rotazione necessaria")
	void diagonalMatrixGivesTrivialResult() {
		SquareMatrix<Real> D = SquareMatrixElementFactory.of(R,
			5.0, 0.0, 0.0,
			0.0, 2.0, 0.0,
			0.0, 0.0, 9.0
		);
		EigenDecomposition result = solver.solve(D, params);

		List<Double> values = new java.util.ArrayList<>();
		for (Complex c : result.getEigenvalues()) {
			values.add(c.getRe());
		}
		java.util.Collections.sort(values);

		assertEquals(List.of(2.0, 5.0, 9.0), values);
	}

	@Test
	@DisplayName("Matrice non simmetrica: rifiutata con IllegalArgumentException")
	void rejectsNonSymmetricMatrix() {
		SquareMatrix<Real> nonSym = SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0);
		assertThrows(IllegalArgumentException.class, () -> solver.solve(nonSym, params));
	}
}
