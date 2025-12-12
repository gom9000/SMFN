package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.utils.MathConstants;
import org.junit.jupiter.api.Test;


public class RationalTest {

    private final Rational r_3_4 = new Rational(3, 4);
    private final Rational r_neg_1_2 = new Rational(-1, 2);
    private final Rational r_5 = new Rational(5, 1);
    
    // --- Constructor & Reduction (GCD) Tests ---

    @Test
    void testConstructorNormalization() {
        // Test reduction
        Rational r = new Rational(6, 8);
        assertEquals(3, r.getNumerator());
        assertEquals(4, r.getDenominator());

        // Test sign normalization (denominator always positive)
        Rational r2 = new Rational(3, -4);
        assertEquals(-3, r2.getNumerator());
        assertEquals(4, r2.getDenominator());
        
        // Test identity
        assertTrue(Rational.ZERO.isMathematicallyEqualTo(new Rational(0, 5)));
        assertTrue(Rational.ONE.isMathematicallyEqualTo(new Rational(10, 10)));
    }
    
    @Test
    void testConstructorZeroDenominator() {
        assertThrows(IllegalArgumentException.class, () -> new Rational(1, 0));
    }

    // --- Arithmetic Operations ---

    @Test
    void testAdd() {
        // 3/4 + (-1/2) = 3/4 - 2/4 = 1/4
        Rational sum = r_3_4.add(r_neg_1_2);
        assertTrue(sum.isMathematicallyEqualTo(new Rational(1, 4)));

        // 5 + 3/4 = 23/4
        Rational sum2 = r_5.add(r_3_4);
        assertTrue(sum2.isMathematicallyEqualTo(new Rational(23, 4)));
    }
    
    @Test
    void testAddOverflow() {
        // Test case designed to cause overflow in Math.multiplyExact within add()
        long max = Long.MAX_VALUE;
        Rational maxRational = new Rational(max, 1);
        Rational oneHalf = new Rational(1, 2);
        
        // max * 2 (in common denominator calculation) will overflow
        assertThrows(ArithmeticException.class, () -> maxRational.add(oneHalf)); 
    }

    @Test
    void testMultiply() {
        // 3/4 * (-1/2) = -3/8
        Rational product = r_3_4.multiply(r_neg_1_2);
        assertTrue(product.isMathematicallyEqualTo(new Rational(-3, 8)));

        // Multiplication resulting in reduction
        Rational r_2_3 = new Rational(2, 3);
        Rational r_3_5 = new Rational(3, 5);
        Rational productReduced = r_2_3.multiply(r_3_5); // 6/15 -> 2/5
        assertTrue(productReduced.isMathematicallyEqualTo(new Rational(2, 5)));
    }
    
    @Test
    void testMultiplyOverflow() {
        // Test case designed to cause overflow in Math.multiplyExact within multiply()
        long halfMax = Long.MAX_VALUE / 2 + 1;
        Rational largeRational = new Rational(halfMax, 1);
        
        assertThrows(ArithmeticException.class, () -> largeRational.multiply(new Rational(2, 1)));
    }

    @Test
    void testNegateAndSubtract() {
        // -(3/4)
        assertTrue(r_3_4.negate().isMathematicallyEqualTo(new Rational(-3, 4)));
        
        // 3/4 - (-1/2) = 5/4
        Rational diff = r_3_4.subtract(r_neg_1_2);
        assertTrue(diff.isMathematicallyEqualTo(new Rational(5, 4)));
    }

    @Test
    void testInverseAndDivide() {
        // Inverse of 3/4 is 4/3
        assertTrue(r_3_4.inverse().isMathematicallyEqualTo(new Rational(4, 3)));
        
        // Inverse of -1/2 is -2/1
        assertTrue(r_neg_1_2.inverse().isMathematicallyEqualTo(new Rational(-2, 1)));

        // Divide: (3/4) / (-1/2) = 3/4 * (-2/1) = -6/4 = -3/2
        Rational quotient = r_3_4.divide(r_neg_1_2);
        assertTrue(quotient.isMathematicallyEqualTo(new Rational(-3, 2)));

        assertThrows(ArithmeticException.class, () -> Rational.ZERO.inverse());
        assertThrows(ArithmeticException.class, () -> r_5.divide(Rational.ZERO));
    }
    
    // --- Exponentiation ---
    
    @Test
    void testPower() {
        // Positive exponent: (3/4)^2 = 9/16
        assertTrue(r_3_4.power(2).isMathematicallyEqualTo(new Rational(9, 16)));
        
        // Negative exponent: (3/4)^-2 = (4/3)^2 = 16/9
        assertTrue(r_3_4.power(-2).isMathematicallyEqualTo(new Rational(16, 9)));
        
        // Power zero
        assertTrue(r_3_4.power(0).isMathematicallyEqualTo(Rational.ONE));
        
        // Zero raised to negative power
        assertThrows(ArithmeticException.class, () -> Rational.ZERO.power(-1));
    }
    
    // --- Square Root (Rational Domain) ---

    @Test
    void testSqrtValid() {
        Rational r_perfect = new Rational(4, 9);
        Rational sqrt = r_perfect.sqrt();
        // sqrt(4/9) = 2/3
        assertTrue(sqrt.isMathematicallyEqualTo(new Rational(2, 3)));
        
        // Perfect integer square (25/1)
        assertTrue(r_5.multiply(r_5).sqrt().isMathematicallyEqualTo(r_5));
    }
    
    @Test
    void testSqrtNegative() {
        // sqrt(-1/2) -> ArithmeticException
        assertThrows(ArithmeticException.class, () -> r_neg_1_2.sqrt());
    }

    @Test
    void testSqrtIrrational() {
        // sqrt(2) is irrational -> 2/1.sqrt()
        Rational r_2 = new Rational(2, 1);
        assertThrows(ArithmeticException.class, () -> r_2.sqrt());
        
        // sqrt(3/4) -> sqrt(3)/2 is irrational
        assertThrows(ArithmeticException.class, () -> r_3_4.sqrt());
    }

    // --- Comparison and Modulus ---
    
    @Test
    void testCompareTo() {
        // 3/4 > -1/2
        assertTrue(r_3_4.compareTo(r_neg_1_2) > 0);
        // 5 > 3/4
        assertTrue(r_5.compareTo(r_3_4) > 0);
        // -1/2 < 3/4
        assertTrue(r_neg_1_2.compareTo(r_3_4) < 0);
        // Equality
        assertTrue(r_3_4.compareTo(new Rational(6, 8)) == 0);
    }
    
    @Test
    void testModulusAndNorm() {
        assertEquals(0.75, r_3_4.modulus(), MathConstants.EPSILON);
        assertEquals(0.5, r_neg_1_2.modulus(), MathConstants.EPSILON);
        
        // Norm returns Real element with modulus value
        assertEquals(5.0, r_5.norm().getValue(), MathConstants.EPSILON);
    }

    // --- Utility Tests ---
    
    @Test
    void testToString() {
        assertEquals("3/4", r_3_4.toString());
        assertEquals("-1/2", r_neg_1_2.toString());
        assertEquals("5", r_5.toString()); // Denominator is 1
        assertEquals("-1", new Rational(-1, 1).toString());
    }
}