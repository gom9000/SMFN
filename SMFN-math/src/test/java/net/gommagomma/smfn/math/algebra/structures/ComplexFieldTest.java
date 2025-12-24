package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import org.junit.jupiter.api.Test;


public class ComplexFieldTest {

    private final Field<Complex> complexField = ComplexField.getInstance();

    @Test
    void testGetInstanceIsSingleton() {
        assertSame(ComplexField.INSTANCE, complexField);
        assertSame(ComplexField.getInstance(), complexField);
    }

    @Test
    void testName() {
        assertEquals("Complex Field (C)", complexField.getName());
    }

    @Test
    void testIdentities() {
        // Additive Identity (Zero)
        Complex zero = complexField.additiveIdentity();
        assertTrue(zero.isMathematicallyEqualTo(Complex.ZERO));

        // Multiplicative Identity (One)
        Complex one = complexField.multiplicativeIdentity();
        assertTrue(one.isMathematicallyEqualTo(Complex.ONE));
    }
    
    @Test
    void testNumericFactoryMethods() {
        // From double
        Complex fromDouble = complexField.of(3.14);
        assertTrue(fromDouble.isMathematicallyEqualTo(new Complex(3.14, 0.0)));

        // From long
        Complex fromLong = complexField.of(123L);
        assertTrue(fromLong.isMathematicallyEqualTo(new Complex(123.0, 0.0)));

        // From int
        Complex fromInt = complexField.of(42);
        assertTrue(fromInt.isMathematicallyEqualTo(new Complex(42.0, 0.0)));
    }
    
    @Test
    void testContains() {
        // Valid complex numbers
        assertTrue(complexField.contains(new Complex(1.0, 2.0)));
        assertTrue(complexField.contains(Complex.ZERO));
        assertTrue(complexField.contains(Complex.ONE));

        // Invalid cases (NaN and Infinity)
        assertFalse(complexField.contains(new Complex(Double.NaN, 1.0)));
        assertFalse(complexField.contains(new Complex(1.0, Double.POSITIVE_INFINITY)));
        assertFalse(complexField.contains(null));
    }
    
    @Test
    void testFieldAxioms() {
        Complex a = new Complex(2.0, 3.0);
        Complex b = new Complex(0.5, -1.0);
        Complex zero = complexField.additiveIdentity();
        Complex one = complexField.multiplicativeIdentity();

        // Additive Inverse: a + (-a) = 0
        assertTrue(a.add(a.negate()).isMathematicallyEqualTo(zero));

        // Multiplicative Inverse: a * a^-1 = 1 (for non-zero a)
        assertTrue(a.multiply(a.inverse()).isMathematicallyEqualTo(one));

        // Division Test: a / b = a * b^-1
        Complex divisionResult = a.divide(b);
        Complex inverseMultiplyResult = a.multiply(b.inverse());
        assertTrue(divisionResult.isMathematicallyEqualTo(inverseMultiplyResult));

        // Zero test for division
        assertThrows(ArithmeticException.class, () -> a.divide(zero));
    }
}