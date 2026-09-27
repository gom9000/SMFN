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

	@Test
	@DisplayName("inverse() su un modulo prossimo al massimo rappresentabile non collassa a zero")
	void inverseHandlesExtremeMagnitudeWithoutUnderflowingToZero() {
		Complex z = new Complex(1e308, 1e308);
		Complex inv = complexField.inverse(z);

		assertFalse(inv.getRe() == 0.0 && inv.getIm() == 0.0,
			"1/z non puo' essere zero per uno z finito e non nullo");

		Complex product = complexField.multiply(z, inv);
		assertTrue(Math.abs(product.getRe() - 1.0) < 1e-6, "z * inverse(z) deve restituire (circa) 1");
		assertTrue(Math.abs(product.getIm()) < 1e-6, "z * inverse(z) deve restituire (circa) 1");
	}

	@Test
	@DisplayName("inverse() resta accurato per moduli ordinari")
	void inverseIsAccurateForOrdinaryMagnitudes() {
		Complex z = new Complex(3.0, 4.0);
		Complex inv = complexField.inverse(z);

		assertEquals(0.12, inv.getRe(), 1e-12);
		assertEquals(-0.16, inv.getIm(), 1e-12);
	}
}
