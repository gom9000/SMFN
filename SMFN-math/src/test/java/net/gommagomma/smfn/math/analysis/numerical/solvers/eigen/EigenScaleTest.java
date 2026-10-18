package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

@DisplayName("Autovalori: i criteri di arresto sono relativi alla scala della matrice")
class EigenScaleTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final ComplexField C = ComplexField.INSTANCE;
	private final StoppingParameters params = new StoppingParameters(new Real(1e-12), 200);

	private static double[] sortedReal(List<Complex> values) {
		double[] v = values.stream().mapToDouble(Complex::getRe).sorted().toArray();
		return v;
	}

	@Test
	@DisplayName("Jacobi risolve una matrice di scala 1e-20 con la stessa accuratezza relativa che a scala 1")
	void jacobiIsScaleInvariant() {
		for (double s : new double[] { 1e-20, 1.0, 1e20 }) {
			SquareMatrix<Real> a = SquareMatrixElementFactory.of(R, 2.0 * s, 1.0 * s, 1.0 * s, 2.0 * s);
			double[] e = sortedReal(new JacobiEigenvalueSolver().solve(a, params).getValue().getEigenvalues());
			assertEquals(1.0, e[0] / s, 1e-9);
			assertEquals(3.0, e[1] / s, 1e-9);
		}
	}

	@Test
	@DisplayName("Hermitiano: stessa accuratezza relativa a scale diverse")
	void hermitianIsScaleInvariant() {
		for (double s : new double[] { 1e-20, 1.0, 1e20 }) {
			SquareMatrix<Complex> a = SquareMatrixElementFactory.of(C,
				new Complex(2 * s, 0), new Complex(0, s), new Complex(0, -s), new Complex(2 * s, 0));
			double[] e = sortedReal(new HermitianEigenvalueSolver().solve(a, params).getValue().getEigenvalues());
			assertEquals(1.0, e[0] / s, 1e-9);
			assertEquals(3.0, e[1] / s, 1e-9);
		}
	}

	@Test
	@DisplayName("QR: gli autovalori di una matrice di scala 1e-20 sono corretti")
	void qrIsScaleInvariant() {
		double s = 1e-20;
		SquareMatrix<Real> a = SquareMatrixElementFactory.of(R, 2.0 * s, 1.0 * s, 0.0, 3.0 * s);
		double[] e = sortedReal(new RealQREigenvalueSolver().solve(a, params).getValue().getEigenvalues());
		assertEquals(2.0, e[0] / s, 1e-9);
		assertEquals(3.0, e[1] / s, 1e-9);
	}

	@Test
	@DisplayName("Matrice nulla: Jacobi converge subito")
	void zeroMatrixConverges() {
		SquareMatrix<Real> a = SquareMatrixElementFactory.of(R, 0.0, 0.0, 0.0, 0.0);
		double[] e = sortedReal(new JacobiEigenvalueSolver().solve(a, params).getValue().getEigenvalues());
		assertEquals(0.0, e[0], 0.0);
		assertEquals(0.0, e[1], 0.0);
	}

	@Test
	@DisplayName("La tolleranza sulla parte immaginaria e' relativa al massimo autovalore")
	void realEigenvaluesToleranceIsRelative() {
		EigenDecomposition small = new EigenDecomposition(
			List.of(new Complex(1e-10, 1e-24), new Complex(2e-10, 0.0)), List.of());
		assertEquals(2, small.getRealEigenvalues(new Real(1e-9)).size());
		EigenDecomposition complex = new EigenDecomposition(
			List.of(new Complex(1e-10, 1e-10), new Complex(1e-10, -1e-10)), List.of());
		assertThrows(IllegalStateException.class, () -> complex.getRealEigenvalues(new Real(1e-9)));
	}
}
