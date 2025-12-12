package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.utils.MathConstants;
import org.junit.jupiter.api.Test;


public class RealTest {

    private static final double EPSILON = MathConstants.EPSILON;
    private final Real r5 = new Real(5.0);
    private final Real r_neg3 = new Real(-3.0);
    private final Real r_half = new Real(0.5);
    
    // --- Constructor & Identity Tests ---

    @Test
    void testConstructorsAndGetters() {
        assertEquals(5.0, r5.getValue());
        assertEquals(-3.0, r_neg3.getValue());
        assertEquals(0.0, Real.ZERO.getValue());
        assertEquals(1.0, Real.ONE.getValue());
    }

    @Test
    void testIdentities() {
        assertTrue(Real.ZERO.isMathematicallyEqualTo(r5.getZero()));
        assertTrue(Real.ONE.isMathematicallyEqualTo(r5.getOne()));
    }
    
    // --- Mathematical Equality (EPSILON) Tests ---

    @Test
    void testMathematicalEquality() {
        Real r5_approx = new Real(5.0 + EPSILON / 2.0);
        Real r5_far = new Real(5.0 + 2 * EPSILON);

        // Near equality
        assertTrue(r5.isMathematicallyEqualTo(r5_approx));
        // Too far
        assertFalse(r5.isMathematicallyEqualTo(r5_far));
        // Exact equality
        assertTrue(r5.isMathematicallyEqualTo(r5));
        
        // NaN check (NaN should be equal to NaN mathematically)
        assertTrue(new Real(Double.NaN).isMathematicallyEqualTo(new Real(Double.NaN)));
        
        // Infinity check
        assertTrue(new Real(Double.POSITIVE_INFINITY).isMathematicallyEqualTo(new Real(Double.POSITIVE_INFINITY)));
        assertFalse(new Real(Double.POSITIVE_INFINITY).isMathematicallyEqualTo(new Real(Double.NEGATIVE_INFINITY)));
    }

    // --- Arithmetic Operations ---
    
    @Test
    void testAddAndSubtract() {
        Real sum = r5.add(r_neg3); // 5.0 + (-3.0) = 2.0
        assertTrue(sum.isMathematicallyEqualTo(new Real(2.0)));
        
        Real diff = r5.subtract(r_neg3); // 5.0 - (-3.0) = 8.0
        assertTrue(diff.isMathematicallyEqualTo(new Real(8.0)));
    }

    @Test
    void testMultiply() {
        Real product = r5.multiply(r_neg3); // 5.0 * -3.0 = -15.0
        assertTrue(product.isMathematicallyEqualTo(new Real(-15.0)));
        
        // Test multiplication by zero
        assertTrue(r5.multiply(Real.ZERO).isMathematicallyEqualTo(Real.ZERO));
    }

    @Test
    void testNegate() {
        assertTrue(r5.negate().isMathematicallyEqualTo(r5.negate().negate().negate()));
        assertTrue(r_neg3.isMathematicallyEqualTo(r_neg3.negate().negate()));
    }

    @Test
    void testInverseAndDivide() {
        // Inverse of 5.0 is 0.2
        assertTrue(r5.inverse().isMathematicallyEqualTo(new Real(0.2)));

        // Divide: 5.0 / 0.5 = 10.0
        Real quotient = r5.divide(r_half);
        assertTrue(quotient.isMathematicallyEqualTo(new Real(10.0)));
        
        // Exception for division by zero
        assertThrows(ArithmeticException.class, () -> Real.ZERO.inverse());
        assertThrows(ArithmeticException.class, () -> r5.divide(Real.ZERO));
    }

    // --- Special Mathematical Functions (Power, Sqrt) ---

    @Test
    void testPower() {
        // Positive exponent: 5^3 = 125
        assertTrue(r5.power(3).isMathematicallyEqualTo(new Real(125.0)));
        
        // Negative exponent: 5^-2 = 0.04
        assertTrue(r5.power(-2).isMathematicallyEqualTo(new Real(0.04)));
        
        // Zero exponent
        assertTrue(r5.power(0).isMathematicallyEqualTo(Real.ONE));
        
        // Zero raised to negative power
        assertThrows(ArithmeticException.class, () -> Real.ZERO.power(-1));
    }

    @Test
    void testSqrt() {
        // sqrt(9) = 3
        assertTrue(new Real(9.0).sqrt().isMathematicallyEqualTo(new Real(3.0)));

        // sqrt(0) = 0
        assertTrue(Real.ZERO.sqrt().isMathematicallyEqualTo(Real.ZERO));
        
        // Exception for square root of negative
        assertThrows(ArithmeticException.class, () -> r_neg3.sqrt());
    }

    // --- Comparison and Norm ---
    
    @Test
    void testCompareTo() {
        assertTrue(r5.compareTo(r_neg3) > 0);
        assertTrue(r_half.compareTo(r5) < 0);
        assertTrue(r5.compareTo(new Real(5.0)) == 0);
    }
    
    @Test
    void testModulusAndNorm() {
        assertEquals(5.0, r5.modulus(), EPSILON);
        assertEquals(3.0, r_neg3.modulus(), EPSILON);
        
        // Norm returns a Real element with the absolute value
        assertTrue(r_neg3.norm().isMathematicallyEqualTo(new Real(3.0)));
    }
    
    // --- Utility/Standard Tests ---

    @Test
    void testToString() {
        assertEquals("5.0", r5.toString());
        assertEquals("-3.0", r_neg3.toString());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // equals must check exact bit-for-bit equality
        Real r5_exact = new Real(5.0);
        assertEquals(r5, r5_exact);
        assertEquals(r5.hashCode(), r5_exact.hashCode());

        // Slight difference means unequal by standard equals()
        Real r5_diff = new Real(5.0 + 1e-15); 
        assertNotEquals(r5, r5_diff);
    }

    @Test
    void testEuclideanDomainElementImpls() {
        // Real numbers form a Field, so remainder is always 0
        assertTrue(r5.remainder(r_neg3).isMathematicallyEqualTo(Real.ZERO));
        
        // Quotient is the standard division
        assertTrue(r5.quotient(r_neg3).isMathematicallyEqualTo(r5.divide(r_neg3)));
        
        // NormValue is 1 for non-zero elements
        assertTrue(r5.normValue().isMathematicallyEqualTo(Natural.ONE));
        assertTrue(Real.ZERO.normValue().isMathematicallyEqualTo(Natural.ZERO));
    }
}