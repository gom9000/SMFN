package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.utils.MathConstants;
import org.junit.jupiter.api.Test;


public class RationalTest {

    private final RationalField Q = RationalField.INSTANCE;

    private final Rational r_3_4 = new Rational(3, 4);
    private final Rational r_neg_1_2 = new Rational(-1, 2);
    private final Rational r_5 = new Rational(5, 1);

    @Test
    void constructorNormalizesAndReducesToLowestTerms() {
        Rational r = new Rational(6, 8);
        assertEquals(3, r.getNumerator());
        assertEquals(4, r.getDenominator());

        // Il segno si sposta sempre al numeratore
        Rational r2 = new Rational(3, -4);
        assertEquals(-3, r2.getNumerator());
        assertEquals(4, r2.getDenominator());
    }

    @Test
    void constructorRejectsZeroDenominator() {
        assertThrows(IllegalArgumentException.class, () -> new Rational(1, 0));
    }

    @Test
    void copy() {
        Rational copy = r_3_4.copy();
        assertEquals(r_3_4, copy);
        assertNotSame(r_3_4, copy);
    }

    @Test
    void orderable() {
        assertTrue(r_3_4.compareTo(r_neg_1_2) > 0);
        assertTrue(r_5.compareTo(r_3_4) > 0);
        assertTrue(r_neg_1_2.compareTo(r_3_4) < 0);
        assertEquals(0, r_3_4.compareTo(new Rational(6, 8)));
        assertTrue(r_neg_1_2.isLessThan(r_3_4));
    }

    @Test
    void absolutable() {
        assertEquals(new Rational(3, 4), r_3_4.abs());
        assertEquals(new Rational(1, 2), r_neg_1_2.abs());
        assertEquals(1, r_3_4.signum());
        assertEquals(-1, r_neg_1_2.signum());
        assertEquals(0, Q.zero().signum());
    }

    @Test
    void exponentiablePositiveAndNegative() {
        assertEquals(new Rational(9, 16), r_3_4.power(2));
        assertEquals(new Rational(16, 9), r_3_4.power(-2)); // (3/4)^-2 = (4/3)^2
        assertEquals(Q.one(), r_3_4.power(0));
        assertThrows(ArithmeticException.class, () -> Q.zero().power(-1));
    }

    @Test
    void normableReturnsRealModulus() {
        assertEquals(0.75, r_3_4.norm().getValue(), MathConstants.EPSILON);
        assertEquals(0.5, r_neg_1_2.norm().getValue(), MathConstants.EPSILON);
        assertEquals(5.0, r_5.norm().getValue(), MathConstants.EPSILON);
    }

    @Test
    void toStringFormat() {
        assertEquals("3/4", r_3_4.toString());
        assertEquals("-1/2", r_neg_1_2.toString());
        assertEquals("5", r_5.toString()); // Denominatore 1: niente frazione
        assertEquals("-1", new Rational(-1, 1).toString());
    }

    // --- Comportamenti di struttura non gia' coperti da RationalFieldTest (i cui assiomi
    // sono verificati genericamente): qui solo la scia di overflow, specifica a questa impl. ---

    @Test
    void structureAdditionOverflow() {
        Rational maxRational = new Rational(Long.MAX_VALUE, 1);
        Rational oneHalf = new Rational(1, 2);
        assertThrows(ArithmeticException.class, () -> Q.add(maxRational, oneHalf));
    }

    @Test
    void structureMultiplicationOverflow() {
        long halfMax = Long.MAX_VALUE / 2 + 1;
        Rational largeRational = new Rational(halfMax, 1);
        assertThrows(ArithmeticException.class, () -> Q.multiply(largeRational, new Rational(2, 1)));
    }
}
