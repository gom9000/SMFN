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
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

@DisplayName("GeneralEigenvalueSolver: dispatcher unico verso il solver giusto")
class GeneralEigenvalueSolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final ComplexField C = ComplexField.INSTANCE;
	private final StoppingParameters params = new StoppingParameters(new Real(1e-12), 100);

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
	@DisplayName("Matrice reale non simmetrica: ramo generico, instrada a QREigenvalueSolver -- autovalori (5+-sqrt(33))/2 calcolati a mano")
	void routesToQrForGeneralNonSymmetricCase() {
		SquareMatrix<Real> nonSym = SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0);

		EigenDecomposition direct = new QREigenvalueSolver<Real>().solve(nonSym, params).getValue();
		EigenDecomposition dispatched = new GeneralEigenvalueSolver<Real>().solve(nonSym, params).getValue();
		assertEquals(direct.getEigenvalues(), dispatched.getEigenvalues());

		double sqrt33 = Math.sqrt(33.0);
		double expectedLow = (5.0 - sqrt33) / 2.0;
		double expectedHigh = (5.0 + sqrt33) / 2.0;

		List<Complex> eigenvalues = dispatched.getEigenvalues();
		assertEquals(2, eigenvalues.size());
		double v0 = eigenvalues.get(0).getRe();
		double v1 = eigenvalues.get(1).getRe();
		assertTrue((Math.abs(v0 - expectedLow) < 1e-9 && Math.abs(v1 - expectedHigh) < 1e-9)
			|| (Math.abs(v0 - expectedHigh) < 1e-9 && Math.abs(v1 - expectedLow) < 1e-9));
	}
}
