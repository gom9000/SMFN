package net.gommagomma.smfn.math.linearalgebra.matrices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.numerical.solvers.linear.GaussianEliminationSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

@DisplayName("Algebra lineare: la singolarita' e l'uguaglianza dipendono dalla scala della matrice")
class MatrixScaleTest
{
	private static final RealField R = RealField.INSTANCE;

	private static SquareMatrix<Real> scaledIdentity(int n, double s) {
		double[] v = new double[n * n];
		for (int i = 0; i < n; i++) v[i * n + i] = s;
		return SquareMatrixElementFactory.of(R, v);
	}

	@Test
	@DisplayName("Una matrice di piccola grandezza ma ben condizionata e' invertibile e ha inversa corretta")
	void smallScaleMatrixIsInvertible() {
		SquareMatrixAlgebra<Real, RealField> algebra = new SquareMatrixAlgebra<>(R, 4);
		SquareMatrix<Real> m = scaledIdentity(4, 1e-4);
		assertTrue(algebra.isInvertible(m));
		SquareMatrix<Real> inv = algebra.inverse(scaledIdentity(4, 1e-13));
		assertEquals(1e13, inv.get(2, 2).getValue(), 1e-3);
	}

	@Test
	@DisplayName("Una matrice numericamente singolare e' rilevata rispetto alla propria scala")
	void relativelySingularMatrixIsDetected() {
		SquareMatrixAlgebra<Real, RealField> algebra = new SquareMatrixAlgebra<>(R, 2);
		SquareMatrix<Real> m = SquareMatrixElementFactory.of(R, 1e8, 2e8, 1e8, 2e8 + 1e-6);
		assertFalse(algebra.isInvertible(m));
		assertThrows(ArithmeticException.class, () -> algebra.inverse(m));
		assertTrue(algebra.isInvertible(SquareMatrixElementFactory.of(R, 1.0, 2.0, 3.0, 4.0)));
	}

	@Test
	@DisplayName("Il rango e' relativo alla scala della matrice")
	void rankIsScaleInvariant() {
		MatrixSpace<Real, RealField> space = new MatrixSpace<>(R, 2, 2);
		for (double s : new double[] { 1e-12, 1.0, 1e12 }) {
			assertEquals(2, space.rank(space.of(new Real[] { new Real(s), new Real(0), new Real(0), new Real(s) })));
			assertEquals(1, space.rank(space.of(new Real[] { new Real(s), new Real(2 * s), new Real(s), new Real(2 * s) })));
		}
	}

	@Test
	@DisplayName("Il solutore lineare risolve sistemi di scala molto piccola e rifiuta quelli singolari")
	void solverIsScaleInvariant() {
		VectorSpace<Real, RealField> v2 = new VectorSpace<>(R, 2);
		GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 2);
		SquareMatrix<Real> a = SquareMatrixElementFactory.of(R, 1e-9, 0.0, 0.0, 2e-9);
		Vector<Real> x = solver.solve(a, VectorElementFactory.of(v2, 1e-9, 4e-9));
		assertEquals(1.0, x.get(0).getValue(), 1e-12);
		assertEquals(2.0, x.get(1).getValue(), 1e-12);
		SquareMatrix<Real> singular = SquareMatrixElementFactory.of(R, 1e-9, 2e-9, 1e-9, 2e-9);
		assertThrows(ArithmeticException.class, () -> solver.solve(singular, VectorElementFactory.of(v2, 1.0, 1.0)));
	}

	@Test
	@DisplayName("I termini noti molto grandi non rendono singolare la matrice dei coefficienti")
	void largeRightHandSideDoesNotAffectPivots() {
		VectorSpace<Real, RealField> v2 = new VectorSpace<>(R, 2);
		GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 2);
		SquareMatrix<Real> a = SquareMatrixElementFactory.of(R, 1.0, 0.0, 0.0, 1.0);
		Vector<Real> x = solver.solve(a, VectorElementFactory.of(v2, 1e20, 3.0));
		assertEquals(1e20, x.get(0).getValue(), 1e6);
		assertEquals(3.0, x.get(1).getValue(), 1e-12);
	}

	@Test
	@DisplayName("L'uguaglianza di vettori e matrici e' normwise: il rumore e' relativo alla grandezza dell'oggetto")
	void compositeEqualityIsNormwise() {
		VectorSpace<Real, RealField> v2 = new VectorSpace<>(R, 2);
		assertTrue(v2.areEqual(VectorElementFactory.of(v2, 1.0, 0.0), VectorElementFactory.of(v2, 1.0, 1e-14)));
		assertFalse(v2.areEqual(VectorElementFactory.of(v2, 1e-14, 0.0), VectorElementFactory.of(v2, 0.0, 1e-14)));
		SquareMatrixAlgebra<Real, RealField> algebra = new SquareMatrixAlgebra<>(R, 2);
		assertTrue(algebra.areEqual(SquareMatrixElementFactory.of(R, 1e6, 0.0, 0.0, 1e6),
			SquareMatrixElementFactory.of(R, 1e6, 1e-8, 0.0, 1e6)));
		assertFalse(algebra.areEqual(scaledIdentity(2, 1e-9), scaledIdentity(2, 2e-9)));
	}
}
