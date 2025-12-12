package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import org.junit.jupiter.api.Test;


public class RationalFieldTest {

    private final Field<Rational, ?> rationalField = RationalField.getInstance();

    @Test
    void testGetInstanceIsSingleton() {
        assertSame(RationalField.INSTANCE, rationalField);
        assertSame(RationalField.getInstance(), rationalField);
    }

    @Test
    void testName() {
        assertEquals("Rational Field (Q)", rationalField.getName());
    }

    @Test
    void testIdentities() {
        // Additive Identity (Zero)
        assertTrue(rationalField.additiveIdentity().isMathematicallyEqualTo(Rational.ZERO));

        // Multiplicative Identity (One)
        assertTrue(rationalField.multiplicativeIdentity().isMathematicallyEqualTo(Rational.ONE));
    }
    
    @Test
    void testContains() {
        assertTrue(rationalField.contains(new Rational(1, 2)));
        assertFalse(rationalField.contains(null));
    }

    // --- Numeric Factory Tests ---
    
    @Test
    void testOfFromLongAndInt() {
        // From long
        Rational fromLong = rationalField.of(123L);
        assertTrue(fromLong.isMathematicallyEqualTo(new Rational(123, 1)));

        // From int
        Rational fromInt = rationalField.of(-42);
        assertTrue(fromInt.isMathematicallyEqualTo(new Rational(-42, 1)));
    }
    
    @Test
    void testOfFromDoubleIntegerValues() {
        // Integer value
        Rational r1 = rationalField.of(10.0);
        assertTrue(r1.isMathematicallyEqualTo(new Rational(10, 1)));
        
        // Large integer value (testing long conversion)
        Rational r2 = rationalField.of(1234567890123.0);
        assertTrue(r2.isMathematicallyEqualTo(new Rational(1234567890123L, 1)));
        
        // Zero
        assertTrue(rationalField.of(0.0).isMathematicallyEqualTo(Rational.ZERO));
    }

    @Test
    void testOfFromDoubleFractionalValues() {
        // 0.5 (1/2)
        Rational r_half = rationalField.of(0.5);
        assertTrue(r_half.isMathematicallyEqualTo(new Rational(1, 2)), 
                   "0.5 conversion failed: " + r_half.toString());

        // 0.75 (3/4)
        Rational r_3_4 = rationalField.of(0.75);
        assertTrue(r_3_4.isMathematicallyEqualTo(new Rational(3, 4)),
                   "0.75 conversion failed: " + r_3_4.toString());

        // -0.125 (-1/8)
        Rational r_neg_1_8 = rationalField.of(-0.125);
        assertTrue(r_neg_1_8.isMathematicallyEqualTo(new Rational(-1, 8)),
                   "-0.125 conversion failed: " + r_neg_1_8.toString());

        // Value that requires full IEEE 754 logic and reduction
        // 0.1 (decimal) is 0.0001100110011... (binary), represented as 3602879701896397/36028797018963968
        // We only check if it is reduced and correctly derived from the double's floating point representation.
        double d_0_1 = 0.1;
        Rational r_0_1 = rationalField.of(d_0_1);
        assertEquals(3602879701896397L, r_0_1.getNumerator());
        assertEquals(36028797018963968L, r_0_1.getDenominator());
        // Note: The denominator should be 2^55, but the reduction is already done in the constructor.
        // The check here is just for the specific value representation of 0.1
    }
    
    @Test
    void testOfFromDoubleInvalid() {
        // NaN, Infinity
        assertThrows(IllegalArgumentException.class, () -> rationalField.of(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> rationalField.of(Double.POSITIVE_INFINITY));

        // Overflow due to large exponent in IEEE 754 representation
        // The implementation has a limit on exponent magnitude for long denominator shift (powerOfTwo > 62)
        // This is a difficult test to trigger precisely without manipulating bits, but we check the exception type.
        // A value close to Double.MIN_VALUE requires a huge power of two in the denominator.
        assertThrows(ArithmeticException.class, () -> rationalField.of(Double.MIN_VALUE / 1000.0)); 
    }
}