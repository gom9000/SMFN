package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

@DisplayName("GeneralEigenvalueSolver: dispatcher unico verso il solver giusto")
class GeneralEigenvalueSolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final ComplexField C = ComplexField.INSTANCE;
	private final ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);

	@Test
	@DisplayName("Matrice reale simmetrica: stesso risultato di JacobiEigenvalueSolver usato direttamente")
	void routesToJacobiForRealSymmetricMatrix() {
		SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 2.0);

		EigenDecomposition direct = new JacobiEigenvalueSolver().solve(A, params).getValue();
		EigenDecomposition dispatched = new GeneralEigenvalueSolver<Real>().solve(A, params).getValue();

		assertEquals(direct.getEigenvalues(), dispatched.getEigenvalues());
	}

	@Test
	@DisplayName("Matrice complessa hermitiana: stesso risultato di HermitianEigenvalueSolver usato direttamente")
	void routesToHermitianForComplexHermitianMatrix() {
		SquareMatrix<Complex> H = SquareMatrixElementFactory.of(C,
			new Complex(2, 0), new Complex(1, 1),
			new Complex(1, -1), new Complex(3, 0)
		);

		EigenDecomposition direct = new HermitianEigenvalueSolver().solve(H, params).getValue();
		EigenDecomposition dispatched = new GeneralEigenvalueSolver<Complex>().solve(H, params).getValue();

		assertEquals(direct.getEigenvalues(), dispatched.getEigenvalues());
	}

	@Test
	@DisplayName("Matrice reale non simmetrica: ramo generico, non ancora implementato, lancia")
	void throwsForGeneralNonSymmetricCase() {
		SquareMatrix<Real> nonSym = SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0);
		assertThrows(UnsupportedOperationException.class, () -> new GeneralEigenvalueSolver<Real>().solve(nonSym, params));
	}
}
