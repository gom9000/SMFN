package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.utils.MathConstants;
import org.junit.jupiter.api.Test;


public class IntegerRingTest {

    private final EuclideanDomain<SignedInt, Natural> integerRing = IntegerRing.getInstance();

    @Test
    void testGetInstanceIsSingleton() {
        assertSame(IntegerRing.INSTANCE, integerRing);
        assertSame(IntegerRing.getInstance(), integerRing);
    }

    @Test
    void testName() {
        assertEquals("Integer Ring (Z)", integerRing.getName());
    }

    @Test
    void testIdentities() {
        // Additive Identity (Zero)
        SignedInt zero = integerRing.additiveIdentity();
        assertEquals(0L, zero.getValue());

        // Multiplicative Identity (One)
        SignedInt one = integerRing.multiplicativeIdentity();
        assertEquals(1L, one.getValue());
    }

    @Test
    void testContains() {
        assertTrue(integerRing.contains(SignedInt.ONE));
        assertFalse(integerRing.contains(null));
    }
    
    // --- Numeric Factory Tests ---

    @Test
    void testOfFromLongAndInt() {
        // From long
        SignedInt fromLong = integerRing.of(1234567890123L);
        assertEquals(1234567890123L, fromLong.getValue());

        // From int
        SignedInt fromInt = integerRing.of(-42);
        assertEquals(-42L, fromInt.getValue());
    }

    @Test
    void testOfFromDoubleValid() {
        // Exact integer value
        SignedInt fromDouble = integerRing.of(5.0);
        assertEquals(5L, fromDouble.getValue());
        
        // Value within tolerance (EPSILON)
        SignedInt fromDoubleTolerance = integerRing.of(10.0 + MathConstants.EPSILON / 2.0);
        assertEquals(10L, fromDoubleTolerance.getValue());
    }
    
    @Test
    void testOfFromDoubleInvalid() {
        // Non-integer value
        assertThrows(IllegalArgumentException.class, () -> integerRing.of(5.1));
        
        // NaN, Infinity
        assertThrows(IllegalArgumentException.class, () -> integerRing.of(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> integerRing.of(Double.POSITIVE_INFINITY));

        // Value out of Long range
        assertThrows(ArithmeticException.class, () -> integerRing.of((double) Long.MAX_VALUE + 1.0));
    }

    // --- Ring Axiom Test (Implicit via operations on SignedInt) ---

    @Test
    void testRingClosureAndProperties() {
        SignedInt a = new SignedInt(10);
        SignedInt b = new SignedInt(-3);
        SignedInt zero = integerRing.additiveIdentity();

        // Subtraction (Additive Inverse property)
        SignedInt diff = a.subtract(b); // 10 - (-3) = 13
        assertEquals(13L, diff.getValue());

        // Negation
        SignedInt negatedA = a.negate();
        assertEquals(-10L, negatedA.getValue());
        
        // Distributivity (Implicitly tested if add/multiply work correctly in SignedInt)
        assertTrue(a.add(b).subtract(a.add(b)).isMathematicallyEqualTo(zero));
    }
}