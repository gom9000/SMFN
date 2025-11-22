package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NaturalTest
{
    @Test
    void testConstructorValid() {
        Natural n = new Natural(10);
        assertEquals(10L, n.getValue());
    }

    @Test
    void testConstructorThrowsForNegative() {
        // I numeri naturali non possono essere negativi
        assertThrows(IllegalArgumentException.class, () -> {
            new Natural(-1);
        });
    }

    @Test
    void testIsEqual() {
        Natural n1 = new Natural(5);
        Natural n2 = new Natural(5);
        Natural n3 = new Natural(6);

        assertTrue(n1.isEqual(n2), "n1 e n2 dovrebbero essere uguali");
        assertFalse(n1.isEqual(n3), "n1 e n3 non dovrebbero essere uguali");
    }

    @Test
    void testAdd() {
        Natural n1 = new Natural(5);
        Natural n2 = new Natural(10);
        Natural sum = n1.add(n2);
        Natural expected = new Natural(15);

        assertTrue(sum.isEqual(expected));
        assertEquals(15L, sum.getValue());
    }

    @Test
    void testAddOverflowThrowsException() {
        // Test con il massimo valore possibile per long
        Natural n1 = new Natural(Long.MAX_VALUE);
        Natural n2 = new Natural(1);

        assertThrows(ArithmeticException.class, () -> {
            n1.add(n2); // Dovrebbe lanciare ArithmeticException per overflow
        });
    }

    @Test
    void testMultiply() {
        Natural n1 = new Natural(3);
        Natural n2 = new Natural(4);
        Natural product = n1.multiply(n2);
        Natural expected = new Natural(12);

        assertTrue(product.isEqual(expected));
        assertEquals(12L, product.getValue());
    }
    
    @Test
    void testMultiplyOverflowThrowsException() {
        // Test con un valore che causi overflow durante la moltiplicazione
        Natural n1 = new Natural(Long.MAX_VALUE / 2 + 1);
        Natural n2 = new Natural(3);

        assertThrows(ArithmeticException.class, () -> {
            n1.multiply(n2); // Dovrebbe lanciare ArithmeticException per overflow
        });
    }

    @Test
    void testIsZeroAndIsOne() {
        assertTrue(Natural.ZERO.isZero());
        assertFalse(Natural.ONE.isZero());
        assertTrue(Natural.ONE.isOne());
        assertFalse(Natural.ZERO.isOne());
    }

    @Test
    void testPowerPositiveExponent() {
        Natural base = new Natural(3);
        // 3^4 = 81
        Natural result = base.power(4);
        Natural expected = new Natural(81);
        assertTrue(result.isEqual(expected));
    }

    @Test
    void testPowerZeroExponent() {
        Natural base = new Natural(10);
        // 10^0 = 1
        Natural result = base.power(0);
        assertTrue(result.isOne());
    }
    
    @Test
    void testPowerOfZero() {
        // 0^5 = 0
        Natural result = Natural.ZERO.power(5);
        assertTrue(result.isZero());
    }

    @Test
    void testPowerNegativeExponentThrowsException() {
        Natural base = new Natural(2);
        // I naturali non supportano esponenti negativi
        assertThrows(ArithmeticException.class, () -> {
            base.power(-1);
        });
    }

    @Test
    void testPowerOverflow() {
        Natural base = new Natural(Long.MAX_VALUE / 2);
        // Elevare al quadrato causerà overflow
        assertThrows(ArithmeticException.class, () -> {
            base.power(2);
        });
    }

    @Test
    void testToString() {
        Natural n = new Natural(42);
        assertEquals("42", n.toString());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Natural n1 = new Natural(10);
        Natural n2 = new Natural(10);
        
        // Verifica equals(Object other)
        assertTrue(n1.equals(n2)); 
        assertEquals(n1.hashCode(), n2.hashCode());
    }
}