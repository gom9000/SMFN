package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.utils.MathConstants;


public class ComplexTest {

    private static final double EPSILON = MathConstants.EPSILON;
    private final Complex i = new Complex(0.0, 1.0);
    private final Complex a = new Complex(3.0, 4.0); // Modulus 5
    private final Complex b = new Complex(1.0, -2.0);
    private final Complex c = new Complex(-1.0, 1.0); // Purely for power/sqrt test
    private final Complex realVal = new Complex(5.0);

    // --- Constructor & Identity Tests ---

    @Test
    void testConstructorsAndGetters() {
        assertEquals(3.0, a.getRe());
        assertEquals(4.0, a.getIm());
        assertEquals(0.0, i.getRe());
        assertEquals(1.0, i.getIm());
        assertEquals(5.0, realVal.getRe());
        assertEquals(0.0, realVal.getIm());
    }

    @Test
    void testIdentities() {
        assertEquals(0.0, Complex.ZERO.getRe());
        assertEquals(0.0, Complex.ZERO.getIm());
        assertEquals(1.0, Complex.ONE.getRe());
        assertEquals(0.0, Complex.ONE.getIm());
        assertTrue(Complex.ZERO.isMathematicallyEqualTo(a.getZero()));
        assertTrue(Complex.ONE.isMathematicallyEqualTo(a.getOne()));
    }
    
    // --- Basic Properties (Modulus, Conjugate, Argument) ---

    @Test
    void testModulus() {
        assertEquals(5.0, a.modulus(), EPSILON); // 3^2 + 4^2 = 25, sqrt(25) = 5
        assertEquals(Math.sqrt(5.0), b.modulus(), EPSILON); // 1^2 + (-2)^2 = 5
        assertEquals(1.0, i.modulus(), EPSILON);
        assertEquals(25.0, a.modulusSquared(), EPSILON);
    }
    
    @Test
    void testConjugate() {
        Complex conjugateA = a.conjugate();
        assertEquals(3.0, conjugateA.getRe());
        assertEquals(-4.0, conjugateA.getIm());
        assertTrue(i.negate().isMathematicallyEqualTo(i.conjugate()));
        assertTrue(realVal.isMathematicallyEqualTo(realVal.conjugate()));
    }

    @Test
    void testArgument() {
        // a = 3 + 4i (approx 0.927 rad)
        assertEquals(Math.atan2(4.0, 3.0), a.argument(), EPSILON);
        // i = 0 + 1i
        assertEquals(Math.PI / 2.0, i.argument(), EPSILON);
        // -1 + 0i
        assertEquals(Math.PI, new Complex(-1.0, 0.0).argument(), EPSILON);
        // Pure real positive
        assertEquals(0.0, realVal.argument(), EPSILON);
    }

    // --- Arithmetic Operations ---
    
    @Test
    void testAddition() {
        Complex sum = a.add(b); // (3+4i) + (1-2i) = 4 + 2i
        assertTrue(sum.isMathematicallyEqualTo(new Complex(4.0, 2.0)));
    }

    @Test
    void testNegateAndSubtract() {
        Complex negatedB = b.negate(); // -1 + 2i
        assertTrue(negatedB.isMathematicallyEqualTo(new Complex(-1.0, 2.0)));
        
        Complex diff = a.subtract(b); // (3+4i) - (1-2i) = 2 + 6i
        assertTrue(diff.isMathematicallyEqualTo(new Complex(2.0, 6.0)));
    }

    @Test
    void testMultiplication() {
        Complex product = a.multiply(b); // (3+4i)(1-2i) = 3 - 6i + 4i - 8i^2 = 3 - 2i + 8 = 11 - 2i
        assertTrue(product.isMathematicallyEqualTo(new Complex(11.0, -2.0)));
        
        // i * i = -1
        assertTrue(i.multiply(i).isMathematicallyEqualTo(new Complex(-1.0, 0.0)));
        // Test multiplication by ONE
        assertTrue(a.multiply(Complex.ONE).isMathematicallyEqualTo(a));
    }

    @Test
    void testInverse() {
        Complex inverseA = a.inverse(); // (3-4i) / 25
        Complex expected = new Complex(3.0 / 25.0, -4.0 / 25.0);
        assertTrue(inverseA.isMathematicallyEqualTo(expected));

        Complex product = a.multiply(inverseA);
        assertTrue(product.isMathematicallyEqualTo(Complex.ONE));

        assertThrows(ArithmeticException.class, () -> Complex.ZERO.inverse());
    }

    @Test
    void testDivide() {
        Complex quotient = a.divide(b); // (3+4i) / (1-2i)
        // (3+4i)(1+2i) / (1-2i)(1+2i) = (3+6i+4i+8i^2) / (1+4) = (-5 + 10i) / 5 = -1 + 2i
        Complex expected = new Complex(-1.0, 2.0);
        assertTrue(quotient.isMathematicallyEqualTo(expected));
        
        assertThrows(ArithmeticException.class, () -> a.divide(Complex.ZERO));
    }

    // --- Complex-specific Methods (Power & Sqrt) ---

    @Test
    void testPower() {
        // Positive exponent: i^3 = -i
        assertTrue(i.power(3).isMathematicallyEqualTo(new Complex(0.0, -1.0)));
        
        // Negative exponent: i^-2 = 1/i^2 = 1/-1 = -1
        assertTrue(i.power(-2).isMathematicallyEqualTo(new Complex(-1.0, 0.0)));
        
        // Zero exponent
        assertTrue(a.power(0).isMathematicallyEqualTo(Complex.ONE));
        
        // Test non-trivial power (De Moivre's formula check on -1+i, |z|=sqrt(2), arg=3pi/4)
        // c^4 = (-1+i)^4 = (|z|^4) * e^(i * 4*arg) = 4 * e^(i * 3pi) = 4 * (-1) = -4
        Complex cPower4 = c.power(4);
        assertTrue(cPower4.isMathematicallyEqualTo(new Complex(-4.0, 0.0)));
        
        // Exception test
        assertThrows(ArithmeticException.class, () -> Complex.ZERO.power(-1));
    }

    @Test
    void testSqrt() {
        // sqrt(-1) = i
        Complex minusOne = new Complex(-1.0, 0.0);
        assertTrue(minusOne.sqrt().isMathematicallyEqualTo(i));

        // sqrt(a) = sqrt(3+4i) = 2+i (using the formula: |z|=5, realPart=sqrt((5+3)/2)=2, imagPart=sqrt((5-3)/2)=1)
        Complex sqrtA = a.sqrt();
        assertTrue(sqrtA.isMathematicallyEqualTo(new Complex(2.0, 1.0)));

        // sqrt(b) = sqrt(1-2i)
        // The formula is designed to give the principal root (Re >= 0)
        Complex sqrtB = b.sqrt();
        // Check if (sqrtB)^2 is mathematically equal to b
        assertTrue(sqrtB.multiply(sqrtB).isMathematicallyEqualTo(b));

        // sqrt(0)
        assertTrue(Complex.ZERO.sqrt().isMathematicallyEqualTo(Complex.ZERO));
    }

    // --- Utility/Interface Tests ---

    @Test
    void testMathematicalEquality() {
        Complex a2 = new Complex(3.0, 4.0 + EPSILON / 2.0);
        assertTrue(a.isMathematicallyEqualTo(a2));
        
        Complex a3 = new Complex(3.0, 4.0 + 2 * EPSILON); // Outside tolerance
        assertFalse(a.isMathematicallyEqualTo(a3));
    }

    @Test
    void testStandardEqualsAndHashCode() {
        // 'equals' must check exact bit-for-bit equality
        Complex a2 = new Complex(3.0, 4.0);
        assertEquals(a, a2);
        assertEquals(a.hashCode(), a2.hashCode());
        
        Complex a3 = new Complex(3.0, 4.0 + 1e-15);
        assertNotEquals(a, a3);
        assertNotEquals(a.hashCode(), a3.hashCode());
    }

    @Test
    void testNorm() {
        Real normA = a.norm();
        assertEquals(5.0, normA.getValue(), EPSILON);
        
        Real normI = i.norm();
        assertEquals(1.0, normI.getValue(), EPSILON);
    }
    
    @Test
    void testToString() {
        assertEquals("3.0 + 4.0i", a.toString());
        assertEquals("1.0 - 2.0i", b.toString());
        assertEquals("i", i.toString()); // Special case for 0 + 1i
        assertEquals("-i", new Complex(0.0, -1.0).toString()); // Special case for 0 - 1i
        assertEquals("5.0", realVal.toString()); // Special case for pure real
        assertEquals("-2.0i", new Complex(0.0, -2.0).toString()); // Special case for pure imag
        assertEquals("-3.0 - 2.0i", new Complex(-3.0, -2.0).toString());
    }
}