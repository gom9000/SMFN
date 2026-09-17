package net.gommagomma.smfn.math.linearalgebra.matrices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.LinearSpaceAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

@DisplayName("MatrixSpace<Real>: assiomi di Spazio Vettoriale (scalari Field) + rango")
class MatrixSpaceTest extends LinearSpaceAxiomContract<Matrix<Real>, Real, RealField>
{
	private final RealField R = RealField.INSTANCE;
	private final MatrixSpace<Real, RealField> M22 = new MatrixSpace<>(R, 2, 2);
	private final MatrixSpace<Real, RealField> M23 = new MatrixSpace<>(R, 2, 3);

	private Matrix<Real> m(MatrixSpace<Real, RealField> space, double... values) {
		Real[] data = new Real[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Real(values[i]);
		return space.of(data);
	}

	@Override
	protected LinearSpace<Matrix<Real>, Real, RealField> structure() { return M22; }

	@Override
	protected Matrix<Real> a() { return m(M22, 1, -2, 3, 4); }
	@Override
	protected Matrix<Real> b() { return m(M22, -4, 0.5, 1, 2); }
	@Override
	protected Matrix<Real> c() { return m(M22, 2, 2, -2, 2); }
	@Override
	protected Real k1() { return new Real(-3.0); }
	@Override
	protected Real k2() { return new Real(0.5); }
	@Override
	protected Real nonZeroScalar() { return new Real(4.0); }

	@Test
	@DisplayName("Rango pieno di una matrice non singolare")
	void rankOfFullRankMatrix() {
		assertEquals(2, M22.rank(a()));
	}

	@Test
	@DisplayName("Rango di una matrice rango-deficiente (righe proporzionali)")
	void rankOfDeficientMatrix() {
		Matrix<Real> deficient = m(M23, 1, 2, 3, 2, 4, 6); // riga 2 = 2 * riga 1
		assertEquals(1, M23.rank(deficient));
	}

	@Test
	@DisplayName("Rango di una matrice nulla e' zero")
	void rankOfZeroMatrixIsZero() {
		assertEquals(0, M23.rank(M23.zero()));
	}

	@Test
	@DisplayName("divide(m, scalare) e' coerente con scale(1/scalare, m)")
	void divideConsistentWithScaleByInverse() {
		Real k = new Real(4.0);
		Matrix<Real> viaDivide = M22.divide(a(), k);
		Matrix<Real> viaScale = M22.scale(R.inverse(k), a());
		assertTrue(M22.areEqual(viaDivide, viaScale));
	}
}
