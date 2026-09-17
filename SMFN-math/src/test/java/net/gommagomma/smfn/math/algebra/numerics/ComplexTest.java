package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.utils.MathConstants;


public class ComplexTest {

    private static final double EPSILON = MathConstants.EPSILON;
    private final ComplexField C = ComplexField.INSTANCE;
    private final Complex i = new Complex(0.0, 1.0);
    private final Complex a = new Complex(3.0, 4.0); // Modulo 5
    private final Complex realVal = new Complex(5.0);

    @Test
    void constructorsAndGetters() {
        assertEquals(3.0, a.getRe());
        assertEquals(4.0, a.getIm());
        assertEquals(5.0, realVal.getRe());
        assertEquals(0.0, realVal.getIm());
    }

    @Test
    void copy() {
        Complex copy = a.copy();
        assertEquals(a, copy);
        assertNotSame(a, copy);
    }

    @Test
    void modulus() {
        assertEquals(5.0, a.modulus(), EPSILON); // 3-4-5
        assertEquals(1.0, i.modulus(), EPSILON);
        assertEquals(25.0, a.modulusSquared(), EPSILON);
    }

    @Test
    void conjugate() {
        Complex conjugateA = a.conjugate();
        assertEquals(3.0, conjugateA.getRe());
        assertEquals(-4.0, conjugateA.getIm());
    }

    @Test
    void argument() {
        assertEquals(Math.atan2(4.0, 3.0), a.argument(), EPSILON);
        assertEquals(Math.PI / 2.0, i.argument(), EPSILON);
        assertEquals(0.0, realVal.argument(), EPSILON);
    }

    @Test
    void exponentiable() {
        // i^3 = -i
        assertTrue(C.areEqual(i.power(3), new Complex(0.0, -1.0)));
        // i^-2 = -1
        assertTrue(C.areEqual(i.power(-2), new Complex(-1.0, 0.0)));
        assertTrue(C.areEqual(a.power(0), C.one()));

        // De Moivre: (-1+i)^4, |z|=sqrt(2), arg=3pi/4 -> 4*e^(i*3pi) = -4
        Complex c = new Complex(-1.0, 1.0);
        assertTrue(C.areEqual(c.power(4), new Complex(-4.0, 0.0)));
    }

    @Test
    void sqrtable() {
        Complex minusOne = new Complex(-1.0, 0.0);
        assertTrue(C.areEqual(minusOne.sqrt(), i));

        // sqrt(3+4i) = 2+i
        assertTrue(C.areEqual(a.sqrt(), new Complex(2.0, 1.0)));

        Complex b = new Complex(1.0, -2.0);
        Complex sqrtB = b.sqrt();
        // La radice principale al quadrato torna b
        assertTrue(C.areEqual(C.multiply(sqrtB, sqrtB), b));

        assertTrue(C.areEqual(C.zero().sqrt(), C.zero()));
    }

    @Test
    void normReturnsReal() {
        assertEquals(5.0, a.norm().getValue(), EPSILON);
        assertEquals(1.0, i.norm().getValue(), EPSILON);
    }

    @Test
    void equalsIsExactBitForBit() {
        Complex a2 = new Complex(3.0, 4.0);
        assertEquals(a, a2);
        assertEquals(a.hashCode(), a2.hashCode());

        Complex a3 = new Complex(3.0, 4.0 + 1e-15);
        assertNotEquals(a, a3);
        assertNotEquals(a.hashCode(), a3.hashCode());
    }

    @Test
    void toStringFormat() {
        assertEquals("3.0 + 4.0i", a.toString());
        assertEquals("1.0 - 2.0i", new Complex(1.0, -2.0).toString());
        assertEquals("i", i.toString());
        assertEquals("-i", new Complex(0.0, -1.0).toString());
        assertEquals("5.0", realVal.toString());
        assertEquals("-2.0i", new Complex(0.0, -2.0).toString());
        assertEquals("-3.0 - 2.0i", new Complex(-3.0, -2.0).toString());
    }
}
