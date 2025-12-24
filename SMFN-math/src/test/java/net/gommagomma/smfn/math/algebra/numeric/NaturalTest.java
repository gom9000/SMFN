package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class NaturalTest {

    private final Natural n5 = new Natural(5);
    private final Natural n10 = new Natural(10);
    private final Natural nMax = new Natural(Long.MAX_VALUE);
    
    // --- Constructor & Identity Tests ---

    @Test
    void testConstructorValid() {
        assertEquals(10, n10.getValue());
        assertEquals(0, Natural.ZERO.getValue());
        assertEquals(1, Natural.ONE.getValue());
    }

    @Test
    void testConstructorInvalid() {
        // Natural numbers cannot be negative
        assertThrows(IllegalArgumentException.class, () -> new Natural(-1));
    }

    @Test
    void testIdentities() {
        assertTrue(Natural.ZERO.isMathematicallyEqualTo(n5.getZero()));
        assertTrue(Natural.ONE.isMathematicallyEqualTo(n5.getOne()));
    }

    // --- Arithmetic Operations ---

    @Test
    void testAdd() {
        Natural sum = n5.add(n10);
        assertEquals(15, sum.getValue());
    }
    
    @Test
    void testAddOverflow() {
        // Test overflow using Math.addExact
        assertThrows(ArithmeticException.class, () -> nMax.add(Natural.ONE));
    }

    @Test
    void testMultiply() {
        Natural product = n5.multiply(n10);
        assertEquals(50, product.getValue());
        
        Natural productZero = n5.multiply(Natural.ZERO);
        assertEquals(0, productZero.getValue());
    }

    @Test
    void testMultiplyOverflow() {
        // Test overflow using Math.multiplyExact (MAX_VALUE * 2 will overflow)
        assertThrows(ArithmeticException.class, () -> nMax.multiply(new Natural(2)));
    }
    
    // --- Exponentiation ---
    
    @Test
    void testPowerValid() {
        // 5^3 = 125
        Natural p3 = n5.power(3);
        assertEquals(125, p3.getValue());
        
        // 5^0 = 1
        Natural p0 = n5.power(0);
        assertEquals(1, p0.getValue());
        
        // 0^5 = 0
        Natural zeroP = Natural.ZERO.power(5);
        assertEquals(0, zeroP.getValue());
        
        // 1^100 = 1
        Natural oneP = Natural.ONE.power(100);
        assertEquals(1, oneP.getValue());
    }

    @Test
    void testPowerNegativeExponent() {
        // Cannot raise a Natural number to a negative power
        assertThrows(ArithmeticException.class, () -> n5.power(-1));
    }
    
    @Test
    void testPowerOverflow() {
        // 10^19 (close to MAX_VALUE) * 10 will surely overflow
        Natural largeBase = new Natural(1000000000000000000L); // 10^18
        assertThrows(ArithmeticException.class, () -> largeBase.power(3)); // 10^54 (overflow)
    }

    // --- Comparison and Modulus ---
    
    @Test
    void testCompareTo() {
        assertTrue(n10.compareTo(n5) > 0);
        assertTrue(n5.compareTo(n10) < 0);
        assertTrue(n5.compareTo(new Natural(5)) == 0);
    }
    
    // --- Utility Tests ---
    
    @Test
    void testMathematicalEquality() {
        Natural n5copy = new Natural(5);
        assertTrue(n5.isMathematicallyEqualTo(n5copy));
        assertFalse(n5.isMathematicallyEqualTo(n10));
    }

    @Test
    void testToString() {
        assertEquals("5", n5.toString());
        assertEquals(String.valueOf(Long.MAX_VALUE), nMax.toString());
    }
}