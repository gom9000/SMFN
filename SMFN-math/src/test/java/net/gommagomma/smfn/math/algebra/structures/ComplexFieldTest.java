package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.FieldAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Complex;

@DisplayName("ComplexField: assiomi di Campo (C)")
public class ComplexFieldTest extends FieldAxiomContract<Complex>
{
	private final ComplexField complexField = ComplexField.INSTANCE;

	@Override
	protected Field<Complex> structure() {
		return complexField;
	}

	@Override
	protected Complex a() { return new Complex(2.0, 3.0); }
	@Override
	protected Complex b() { return new Complex(0.5, -1.0); }
	@Override
	protected Complex c() { return new Complex(-1.0, 1.0); }

	@Test
	void isSingleton() {
		assertSame(ComplexField.INSTANCE, complexField);
	}

	@Test
	void name() {
		assertEquals("Complex Field (C)", complexField.getName());
	}

	@Test
	void numericFactoryMethods() {
		assertTrue(complexField.areEqual(complexField.of(3.14), new Complex(3.14, 0.0)));
		assertTrue(complexField.areEqual(complexField.of(123L), new Complex(123.0, 0.0)));
		assertTrue(complexField.areEqual(complexField.of(42), new Complex(42.0, 0.0)));
	}

	@Test
	void containsRejectsNonFiniteValues() {
		assertTrue(complexField.contains(new Complex(1.0, 2.0)));
		assertTrue(complexField.contains(complexField.zero()));

		assertFalse(complexField.contains(new Complex(Double.NaN, 1.0)));
		assertFalse(complexField.contains(new Complex(1.0, Double.POSITIVE_INFINITY)));
	}
}
