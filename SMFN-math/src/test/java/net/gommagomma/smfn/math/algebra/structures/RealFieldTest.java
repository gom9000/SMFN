package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.FieldAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Real;

@DisplayName("RealField: assiomi di Campo (R)")
public class RealFieldTest extends FieldAxiomContract<Real>
{
	private final RealField realField = RealField.INSTANCE;

	@Override
	protected Field<Real> structure() {
		return realField;
	}

	@Override
	protected Real a() { return new Real(10.0); }
	@Override
	protected Real b() { return new Real(-2.0); }
	@Override
	protected Real c() { return new Real(4.0); }

	@Test
	void isSingleton() {
		assertSame(RealField.INSTANCE, realField);
	}

	@Test
	void name() {
		assertEquals("Real Field (R)", realField.getName());
	}

	@Test
	void numericFactoryMethods() {
		assertTrue(realField.areEqual(realField.of(3.14159), new Real(3.14159)));
		assertTrue(realField.areEqual(realField.of(1234567890123L), new Real(1234567890123.0)));
		assertTrue(realField.areEqual(realField.of(-42), new Real(-42.0)));
	}

	@Test
	void containsRejectsNonFiniteValues() {
		assertTrue(realField.contains(new Real(1.0)));
		assertTrue(realField.contains(realField.zero()));

		assertFalse(realField.contains(new Real(Double.NaN)));
		assertFalse(realField.contains(new Real(Double.POSITIVE_INFINITY)));
		assertFalse(realField.contains(new Real(Double.NEGATIVE_INFINITY)));
	}
}
