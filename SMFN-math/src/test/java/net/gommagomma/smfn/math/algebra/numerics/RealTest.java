package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.utils.MathConstants;
import org.junit.jupiter.api.Test;


public class RealTest {

    private static final double EPSILON = MathConstants.EPSILON;
    private final RealField R = RealField.INSTANCE;
    private final Real r5 = new Real(5.0);
    private final Real r_neg3 = new Real(-3.0);

    @Test
    void constructorAndGetter() {
        assertEquals(5.0, r5.getValue());
        assertEquals(-3.0, r_neg3.getValue());
    }

    @Test
    void copy() {
        Real copy = r5.copy();
        assertEquals(r5, copy);
        assertNotSame(r5, copy);
    }

    @Test
    void orderable() {
        assertTrue(r5.compareTo(r_neg3) > 0);
        assertTrue(r_neg3.compareTo(r5) < 0);
        assertTrue(r5.compareTo(new Real(5.0)) == 0);
        assertTrue(r_neg3.isLessThan(r5));
    }

    @Test
    void absolutable() {
        assertEquals(5.0, r5.abs().getValue(), EPSILON);
        assertEquals(3.0, r_neg3.abs().getValue(), EPSILON);
        assertEquals(1, r5.signum());
        assertEquals(-1, r_neg3.signum());
    }

    @Test
    void normableIsAbs() {
        assertEquals(3.0, r_neg3.norm().getValue(), EPSILON);
    }

    @Test
    void exponentiable() {
        assertEquals(125.0, r5.power(3).getValue(), EPSILON);
        assertEquals(0.04, r5.power(-2).getValue(), EPSILON);
        assertEquals(1.0, r5.power(0).getValue(), EPSILON);
    }

    @Test
    void sqrtable() {
        assertEquals(3.0, new Real(9.0).sqrt().getValue(), EPSILON);
        assertEquals(0.0, R.zero().sqrt().getValue(), EPSILON);
        assertThrows(ArithmeticException.class, () -> r_neg3.sqrt());
    }

    @Test
    void toStringFormat() {
        assertEquals("5.0", r5.toString());
        assertEquals("-3.0", r_neg3.toString());
    }

    @Test
    void equalsIsExactBitForBit() {
        // A differenza di areEqual() (con tolleranza epsilon a livello di struttura),
        // equals() deve essere un confronto esatto: e' il contratto standard di Object.
        Real r5_exact = new Real(5.0);
        assertEquals(r5, r5_exact);
        assertEquals(r5.hashCode(), r5_exact.hashCode());

        Real r5_diff = new Real(5.0 + 1e-15);
        assertNotEquals(r5, r5_diff);
    }

    @Test
    void equalsHandlesNaNAndInfinityBitwise() {
        // Real.equals usa Double.doubleToLongBits: NaN risulta uguale a se stesso,
        // a differenza di Double.equals(NaN) puro (che pure lo sarebbe) ma diversamente
        // dall'operatore == su double (dove NaN != NaN).
        assertEquals(new Real(Double.NaN), new Real(Double.NaN));
        assertEquals(new Real(Double.POSITIVE_INFINITY), new Real(Double.POSITIVE_INFINITY));
        assertNotEquals(new Real(Double.POSITIVE_INFINITY), new Real(Double.NEGATIVE_INFINITY));
    }
}
