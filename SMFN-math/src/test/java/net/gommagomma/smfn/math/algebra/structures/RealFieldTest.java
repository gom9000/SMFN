package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import org.junit.jupiter.api.Test;


public class RealFieldTest {

    private final Field<Real> realField = RealField.getInstance();

    @Test
    void testGetInstanceIsSingleton() {
        assertSame(RealField.INSTANCE, realField);
        assertSame(RealField.getInstance(), realField);
    }

    @Test
    void testName() {
        assertEquals("Real Field (R)", realField.getName());
    }

    @Test
    void testIdentities() {
        // Additive Identity (Zero)
        assertTrue(realField.additiveIdentity().isMathematicallyEqualTo(Real.ZERO));

        // Multiplicative Identity (One)
        assertTrue(realField.multiplicativeIdentity().isMathematicallyEqualTo(Real.ONE));
    }
    
    @Test
    void testNumericFactoryMethods() {
        // From double
        Real fromDouble = realField.of(3.14159);
        assertTrue(fromDouble.isMathematicallyEqualTo(new Real(3.14159)));

        // From long
        Real fromLong = realField.of(1234567890123L);
        assertTrue(fromLong.isMathematicallyEqualTo(new Real(1234567890123.0)));

        // From int
        Real fromInt = realField.of(-42);
        assertTrue(fromInt.isMathematicallyEqualTo(new Real(-42.0)));
    }
    
    @Test
    void testContains() {
        // Valid real numbers
        assertTrue(realField.contains(new Real(1.0)));
        assertTrue(realField.contains(Real.ZERO));
        assertTrue(realField.contains(new Real(-99.9)));

        // Invalid cases (NaN and Infinity)
        assertFalse(realField.contains(new Real(Double.NaN)));
        assertFalse(realField.contains(new Real(Double.POSITIVE_INFINITY)));
        assertFalse(realField.contains(new Real(Double.NEGATIVE_INFINITY)));
        assertFalse(realField.contains(null));
    }

    @Test
    void testFieldAxioms() {
        Real a = new Real(10.0);
        Real b = new Real(-2.0);
        Real zero = realField.additiveIdentity();
        Real one = realField.multiplicativeIdentity();

        // Additive Inverse: a + (-a) = 0
        assertTrue(a.add(a.negate()).isMathematicallyEqualTo(zero));

        // Multiplicative Inverse: a * a^-1 = 1
        assertTrue(a.multiply(a.inverse()).isMathematicallyEqualTo(one));

        // Division Test: a / b = a * b^-1
        Real divisionResult = a.divide(b);
        Real inverseMultiplyResult = a.multiply(b.inverse());
        assertTrue(divisionResult.isMathematicallyEqualTo(inverseMultiplyResult));

        // Zero test for division
        assertThrows(ArithmeticException.class, () -> a.divide(zero));
    }
}