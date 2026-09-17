package net.gommagomma.smfn.math.linearalgebra.matrices;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

@DisplayName("Matrix<K>: comportamento dell'elemento, incluso Mapping<Vector<K>,Vector<K>>")
class MatrixTest
{
	private final RealField R = RealField.INSTANCE;
	private final MatrixSemimodule<Real, RealField> M23 = new MatrixSemimodule<>(R, 2, 3);
	private final VectorSemimodule<Real, RealField> V3 = new VectorSemimodule<>(R, 3);
	private final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

	private Matrix<Real> matrix(double... values) {
		Real[] data = new Real[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Real(values[i]);
		return M23.of(data);
	}

	private Vector<Real> vector(VectorSemimodule<Real, RealField> space, double... values) {
		Real[] data = new Real[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Real(values[i]);
		return space.of(data);
	}

	@Test
	@DisplayName("get(r,c) restituisce l'elemento giusto")
	void get() {
		Matrix<Real> a = matrix(1, 2, 0, 0, 1, -1);
		assertEquals(1.0, a.get(0, 0).getValue());
		assertEquals(2.0, a.get(0, 1).getValue());
		assertEquals(-1.0, a.get(1, 2).getValue());
	}

	@Test
	@DisplayName("TensorElement: rank 2, shape = [rows, cols]")
	void tensorProperties() {
		Matrix<Real> a = matrix(1, 2, 0, 0, 1, -1);
		assertEquals(2, a.rank());
		assertArrayEquals(new int[] { 2, 3 }, a.getShape());
		assertEquals(6, a.size());
	}

	@Test
	@DisplayName("apply(): prodotto matrice-vettore m x n -> m")
	void applyComputesMatrixVectorProduct() {
		// A = [[1,0,2],[0,1,-1]], x = (1,2,3) -> A*x = (7,-1)
		Matrix<Real> a = matrix(1, 0, 2, 0, 1, -1);
		Vector<Real> x = vector(V3, 1, 2, 3);

		Vector<Real> y = a.apply(x);
		assertTrue(R.areEqual(y.get(0), new Real(7.0)));
		assertTrue(R.areEqual(y.get(1), new Real(-1.0)));
	}

	@Test
	@DisplayName("apply(): dimensione incompatibile lancia IllegalArgumentException")
	void applyRejectsWrongDimension() {
		Matrix<Real> a = matrix(1, 0, 2, 0, 1, -1);
		Vector<Real> wrongSize = vector(V2, 1, 1);
		assertThrows(IllegalArgumentException.class, () -> a.apply(wrongSize));
	}

	@Test
	@DisplayName("copy() restituisce una nuova istanza con lo stesso contenuto")
	void copy() {
		Matrix<Real> a = matrix(1, 2, 0, 0, 1, -1);
		Matrix<Real> copy = a.copy();
		assertEquals(a, copy);
		assertNotSame(a, copy);
	}

	@Test
	@DisplayName("equals/hashCode")
	void equalityAndHashCode() {
		Matrix<Real> a = matrix(1, 2, 0, 0, 1, -1);
		Matrix<Real> same = matrix(1, 2, 0, 0, 1, -1);
		Matrix<Real> different = matrix(1, 2, 0, 0, 1, 99);

		assertEquals(a, same);
		assertEquals(a.hashCode(), same.hashCode());
		assertFalse(a.equals(different));
	}
}
