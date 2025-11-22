package net.gommagomma.smfn.math.algebra.numeric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.utils.MathConstants;

class ComplexTest {

    // Costante di tolleranza per i confronti floating point nei test
    private static final double EPSILON_TEST = MathConstants.EPSILON * 10; 
    // Usiamo una tolleranza leggermente maggiore per i test, a causa degli errori di arrotondamento accumulati.

    @Test
    void testConstructorAndGetters() {
        Complex z = new Complex(3.0, 4.0);
        Assertions.assertEquals(3.0, z.getRe(), EPSILON_TEST);
        Assertions.assertEquals(4.0, z.getIm(), EPSILON_TEST);
    }

    @Test
    void testIsEqual() {
        Complex z1 = new Complex(1.0, 2.0);
        Complex z2 = new Complex(1.0 + MathConstants.EPSILON / 2, 2.0 - MathConstants.EPSILON / 2);
        Complex z3 = new Complex(1.1, 2.0);

        Assertions.assertTrue(z1.isEqual(z2), "z1 e z2 dovrebbero essere uguali entro la tolleranza");
        Assertions.assertFalse(z1.isEqual(z3), "z1 e z3 non dovrebbero essere uguali");
    }

    @Test
    void testAdd() {
        Complex z1 = new Complex(1.0, 2.0);
        Complex z2 = new Complex(3.0, -4.0);
        Complex sum = z1.add(z2);
        Complex expected = new Complex(4.0, -2.0);

        Assertions.assertTrue(sum.isEqual(expected));
    }

    @Test
    void testNegate() {
        Complex z = new Complex(1.0, -2.0);
        Complex negated = z.negate();
        Complex expected = new Complex(-1.0, 2.0);
        
        Assertions.assertTrue(negated.isEqual(expected));
    }
    
    @Test
    void testSubtract() {
        Complex z1 = new Complex(1.0, 2.0);
        Complex z2 = new Complex(3.0, -4.0);
        // Il metodo subtract() è di default nell'interfaccia GroupElement
        Complex diff = z1.subtract(z2); 
        Complex expected = new Complex(-2.0, 6.0);

        Assertions.assertTrue(diff.isEqual(expected));
    }

    @Test
    void testMultiply() {
        Complex z1 = new Complex(1.0, 2.0);
        Complex z2 = new Complex(3.0, -4.0);
        // (1 + 2i) * (3 - 4i) = 3 - 4i + 6i - 8i^2 = 3 + 2i + 8 = 11 + 2i
        Complex product = z1.multiply(z2);
        Complex expected = new Complex(11.0, 2.0);

        Assertions.assertTrue(product.isEqual(expected));
    }
    
    @Test
    void testModulusAndSquared() {
        Complex z = new Complex(3.0, 4.0);
        Assertions.assertEquals(5.0, z.modulus(), EPSILON_TEST);
        Assertions.assertEquals(25.0, z.modulusSquared(), EPSILON_TEST);
    }

    @Test
    void testInverse() {
        Complex z = new Complex(1.0, 2.0);
        // Inverso di (1+2i) = (1-2i) / (1^2 + 2^2) = (1-2i)/5 = 0.2 - 0.4i
        Complex inverse = z.inverse();
        Complex expected = new Complex(0.2, -0.4);

        Assertions.assertTrue(inverse.isEqual(expected));
        // Verifica che z * inverse sia circa 1 (l'identità moltiplicativa)
        Assertions.assertTrue(z.multiply(inverse).isOne());
    }

    @Test
    void testInverseOfZeroThrowsException() {
        Complex z = Complex.ZERO;
        Assertions.assertThrows(ArithmeticException.class, z::inverse);
    }
    
    @Test
    void testConjugate() {
        Complex z = new Complex(1.0, -2.0);
        Complex conjugated = z.conjugate();
        Complex expected = new Complex(1.0, 2.0);
        Assertions.assertTrue(conjugated.isEqual(expected));
    }

    @Test
    void testPowerPositive() {
        Complex z = new Complex(1.0, 1.0);
        // (1 + i)^3 = (1 + i)^2 * (1 + i) = (1 + 2i - 1) * (1 + i) = 2i * (1 + i) = 2i - 2 = -2 + 2i
        Complex result = z.power(3);
        Complex expected = new Complex(-2.0, 2.0);
        Assertions.assertTrue(result.isEqual(expected));
    }
    
    @Test
    void testPowerNegative() {
        Complex z = new Complex(1.0, 0.0); // 1.0
        // 1^-5 = 1
        Complex result = z.power(-5);
        Complex expected = new Complex(1.0, 0.0);
        Assertions.assertTrue(result.isEqual(expected));
        
        Complex z2 = new Complex(2.0, 0.0); // 2.0
        // 2^-2 = 0.25
        Complex result2 = z2.power(-2);
        Complex expected2 = new Complex(0.25, 0.0);
        Assertions.assertTrue(result2.isEqual(expected2));
    }

    @Test
    void testPowerOfZeroThrowsExceptionForNegativeExponent() {
        Assertions.assertThrows(ArithmeticException.class, () -> {
            Complex.ZERO.power(-1);
        });
    }

    @Test
    void testSqrt() {
        // Test sqrt(i) = (1+i)/sqrt(2) approx 0.707 + 0.707i
        Complex z = new Complex(0.0, 1.0);
        Complex result = z.sqrt();
        double expectedRe = 1.0 / Math.sqrt(2.0);
        Complex expected = new Complex(expectedRe, expectedRe);
        Assertions.assertTrue(result.isEqual(expected));
        
        // Verifica che il quadrato del risultato sia circa z originale
        Assertions.assertTrue(result.multiply(result).isEqual(z));
        
        // Test sqrt(-4) = 2i
        Complex z2 = new Complex(-4.0, 0.0);
        Complex result2 = z2.sqrt();
        Complex expected2 = new Complex(0.0, 2.0);
        Assertions.assertTrue(result2.isEqual(expected2));
    }
    
    @Test
    void testToString() {
        Complex z1 = new Complex(1.0, 2.0);
        Assertions.assertEquals("1.0 + 2.0i", z1.toString());
        
        Complex z2 = new Complex(-1.0, -2.0);
        Assertions.assertEquals("-1.0 - 2.0i", z2.toString());
        
        Complex z3 = new Complex(0.0, 1.0);
        Assertions.assertEquals("i", z3.toString());

        Complex z4 = new Complex(0.0, -1.0);
        Assertions.assertEquals("-i", z4.toString());

        Complex z5 = new Complex(3.0, 0.0);
        Assertions.assertEquals("3.0", z5.toString());
    }
}
