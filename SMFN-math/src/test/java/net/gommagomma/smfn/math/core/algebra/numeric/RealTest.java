package net.gommagomma.smfn.math.core.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.utils.MathConstants;

class RealTest
{
    private static final double EPSILON_TEST = MathConstants.EPSILON * 10.0; 

    @Test
    void testConstructorAndGetter() {
        Real r = new Real(3.14);
        assertEquals(3.14, r.getValue(), EPSILON_TEST);
    }

    @Test
    void testIsEqual() {
        Real r1 = new Real(1.0);
        Real r2 = new Real(1.0 + MathConstants.EPSILON / 2); // Entro la tolleranza
        Real r3 = new Real(1.1); // Fuori tolleranza

        assertTrue(r1.isEqual(r2), "r1 e r2 dovrebbero essere uguali entro la tolleranza");
        assertFalse(r1.isEqual(r3), "r1 e r3 non dovrebbero essere uguali");
    }

    @Test
    void testAdd() {
        Real r1 = new Real(1.5);
        Real r2 = new Real(2.3);
        Real sum = r1.add(r2);
        Real expected = new Real(3.8);

        assertTrue(sum.isEqual(expected));
    }

    @Test
    void testNegate() {
        Real r = new Real(-5.5);
        Real negated = r.negate();
        Real expected = new Real(5.5);
        
        assertTrue(negated.isEqual(expected));
    }
    
    @Test
    void testSubtract() {
        Real r1 = new Real(10.0);
        Real r2 = new Real(3.0);
        // Il metodo subtract() è di default nell'interfaccia GroupElement
        Real diff = r1.subtract(r2); 
        Real expected = new Real(7.0);

        assertTrue(diff.isEqual(expected));
    }

    @Test
    void testMultiply() {
        Real r1 = new Real(2.5);
        Real r2 = new Real(2.0);
        Real product = r1.multiply(r2);
        Real expected = new Real(5.0);

        assertTrue(product.isEqual(expected));
    }
    
    @Test
    void testInverse() {
        Real r = new Real(4.0);
        // Inverso di 4.0 = 0.25
        Real inverse = r.inverse();
        Real expected = new Real(0.25);

        assertTrue(inverse.isEqual(expected));
        // Verifica che r * inverse sia circa 1 (l'identità moltiplicativa)
        assertTrue(r.multiply(inverse).isOne());
    }

    @Test
    void testInverseOfZeroThrowsException() {
        Real r = Real.ZERO;
        assertThrows(ArithmeticException.class, r::inverse);
    }
    
    @Test
    void testPowerPositiveExponent() {
        Real base = new Real(2.0);
        // 2.0^3 = 8.0
        Real result = base.power(3);
        Real expected = new Real(8.0);
        assertTrue(result.isEqual(expected));
    }
    
    @Test
    void testPowerNegativeExponent() {
        Real base = new Real(2.0);
        // 2.0^-2 = 0.25
        Real result = base.power(-2);
        Real expected = new Real(0.25);
        assertTrue(result.isEqual(expected));
    }

    @Test
    void testPowerOfZeroThrowsExceptionForNegativeExponent() {
        assertThrows(ArithmeticException.class, () -> {
            Real.ZERO.power(-1);
        });
    }

    @Test
    void testSqrtPositive() {
        Real r = new Real(25.0);
        Real result = r.sqrt();
        Real expected = new Real(5.0);
        assertTrue(result.isEqual(expected));
    }
    
    @Test
    void testSqrtZero() {
        Real result = Real.ZERO.sqrt();
        assertTrue(result.isZero());
    }
    
    @Test
    void testSqrtNegativeThrowsException() {
        Real r = new Real(-1.0);
        // La radice di -1 non esiste nei numeri Reali
        assertThrows(ArithmeticException.class, r::sqrt);
    }

    @Test
    void testToString() {
        Real r = new Real(123.45);
        assertEquals("123.45", r.toString());
    }
}