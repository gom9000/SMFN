package net.gommagomma.smfn.math.linearalgebra.vectors;

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

@DisplayName("Vector<K>: comportamento dell'elemento")
class VectorTest
{
	private final RealField R = RealField.INSTANCE;
	private final VectorSemimodule<Real, RealField> V3 = new VectorSemimodule<>(R, 3);

	private Vector<Real> v(double... values) {
		Real[] data = new Real[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Real(values[i]);
		return V3.of(data);
	}

	@Test
	@DisplayName("get(index) restituisce il componente giusto")
	void getReturnsComponent() {
		Vector<Real> x = v(1.0, 2.0, 3.0);
		assertEquals(1.0, x.get(0).getValue());
		assertEquals(2.0, x.get(1).getValue());
		assertEquals(3.0, x.get(2).getValue());
	}

	@Test
	@DisplayName("get(index) fuori range lancia IndexOutOfBoundsException")
	void getOutOfRangeThrows() {
		Vector<Real> x = v(1.0, 2.0, 3.0);
		assertThrows(IndexOutOfBoundsException.class, () -> x.get(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> x.get(3));
	}

	@Test
	@DisplayName("get(int...) richiede esattamente un indice")
	void variadicGetRequiresOneIndex() {
		Vector<Real> x = v(1.0, 2.0, 3.0);
		assertEquals(x.get(1), x.get(new int[] { 1 }));
		assertThrows(IllegalArgumentException.class, () -> x.get(1, 2));
		assertThrows(IllegalArgumentException.class, () -> x.get((int[]) null));
	}

	@Test
	@DisplayName("TensorElement: rank 1, shape = [size]")
	void tensorProperties() {
		Vector<Real> x = v(1.0, 2.0, 3.0);
		assertEquals(1, x.rank());
		assertArrayEquals(new int[] { 3 }, x.getShape());
		assertEquals(3, x.size());
	}

	@Test
	@DisplayName("copy() restituisce una nuova istanza con lo stesso contenuto")
	void copy() {
		Vector<Real> x = v(1.0, 2.0, 3.0);
		Vector<Real> copy = x.copy();
		assertEquals(x, copy);
		assertNotSame(x, copy);
	}

	@Test
	@DisplayName("equals/hashCode")
	void equalityAndHashCode() {
		Vector<Real> x = v(1.0, 2.0, 3.0);
		Vector<Real> same = v(1.0, 2.0, 3.0);
		Vector<Real> different = v(1.0, 2.0, 4.0);

		assertEquals(x, same);
		assertEquals(x.hashCode(), same.hashCode());
		assertFalse(x.equals(different));
	}

	@Test
	@DisplayName("toString")
	void toStringFormat() {
		assertEquals("[1.0, 2.0, 3.0]", v(1.0, 2.0, 3.0).toString());
	}
}
