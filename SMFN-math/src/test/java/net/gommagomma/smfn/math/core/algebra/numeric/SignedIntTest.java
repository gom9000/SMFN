package net.gommagomma.smfn.math.core.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;

class SignedIntTest
{
    @Test
    void testConstructorAndGetter() {
        SignedInt n = new SignedInt(-10);
        assertEquals(-10L, n.getValue());
    }

    @Test
    void testIsEqual() {
        SignedInt n1 = new SignedInt(5);
        SignedInt n2 = new SignedInt(5);
        SignedInt n3 = new SignedInt(6);

        assertTrue(n1.isMathematicallyEqualTo(n2), "n1 e n2 dovrebbero essere uguali");
        assertFalse(n1.isMathematicallyEqualTo(n3), "n1 e n3 non dovrebbero essere uguali");
    }

    @Test
    void testAdd() {
        SignedInt n1 = new SignedInt(5);
        SignedInt n2 = new SignedInt(-10);
        SignedInt sum = n1.add(n2);
        SignedInt expected = new SignedInt(-5);

        assertTrue(sum.isMathematicallyEqualTo(expected));
        assertEquals(-5L, sum.getValue());
    }

    @Test
    void testAddOverflowThrowsException() {
        // Test con il massimo valore possibile per long
        SignedInt n1 = new SignedInt(Long.MAX_VALUE);
        SignedInt n2 = new SignedInt(1);

        assertThrows(ArithmeticException.class, () -> {
            n1.add(n2); // Dovrebbe lanciare ArithmeticException per overflow
        });
    }
    
    @Test
    void testNegate() {
        SignedInt n = new SignedInt(5);
        SignedInt negated = n.negate();
        SignedInt expected = new SignedInt(-5);
        assertTrue(negated.isMathematicallyEqualTo(expected));
        
        SignedInt n2 = new SignedInt(-5);
        SignedInt negated2 = n2.negate();
        SignedInt expected2 = new SignedInt(5);
        assertTrue(negated2.isMathematicallyEqualTo(expected2));
    }
    
    @Test
    void testNegateMinLongThrowsException() {
    	// Negare il minimo valore di long causa overflow perché il massimo valore è inferiore in magnitudine
        SignedInt min = new SignedInt(Long.MIN_VALUE);
        assertThrows(ArithmeticException.class, min::negate);
    }
    
    @Test
    void testSubtract() {
        SignedInt n1 = new SignedInt(10);
        SignedInt n2 = new SignedInt(3);
        // Il metodo subtract() è di default nell'interfaccia GroupElement
        SignedInt diff = n1.subtract(n2); 
        SignedInt expected = new SignedInt(7);

        assertTrue(diff.isMathematicallyEqualTo(expected));
    }

    @Test
    void testMultiply() {
        SignedInt n1 = new SignedInt(-3);
        SignedInt n2 = new SignedInt(4);
        SignedInt product = n1.multiply(n2);
        SignedInt expected = new SignedInt(-12);

        assertTrue(product.isMathematicallyEqualTo(expected));
        assertEquals(-12L, product.getValue());
    }
    
    @Test
    void testMultiplyOverflowThrowsException() {
        // Test con un valore che causi overflow durante la moltiplicazione
        SignedInt n1 = new SignedInt(Long.MAX_VALUE / 2 + 1);
        SignedInt n2 = new SignedInt(3);

        assertThrows(ArithmeticException.class, () -> {
            n1.multiply(n2); // Dovrebbe lanciare ArithmeticException per overflow
        });
    }

    @Test
    void testIsZeroAndIsOne() {
        assertTrue(SignedInt.ZERO.isZero());
        assertFalse(SignedInt.ONE.isZero());
        assertTrue(SignedInt.ONE.isOne());
        assertFalse(SignedInt.ZERO.isOne());
    }

    @Test
    void testPowerPositiveExponent() {
        SignedInt base = new SignedInt(-3);
        // (-3)^3 = -27
        SignedInt result = base.power(3);
        SignedInt expected = new SignedInt(-27);
        assertTrue(result.isMathematicallyEqualTo(expected));
    }

    @Test
    void testPowerZeroExponent() {
        SignedInt base = new SignedInt(-10);
        // (-10)^0 = 1
        SignedInt result = base.power(0);
        assertTrue(result.isOne());
    }
    
    @Test
    void testPowerOfZero() {
        // 0^5 = 0
        SignedInt result = SignedInt.ZERO.power(5);
        assertTrue(result.isZero());
    }

    @Test
    void testPowerNegativeExponentThrowsException() {
        SignedInt base = new SignedInt(2);
        // Gli interi non supportano esponenti negativi nell'anello Z
        assertThrows(ArithmeticException.class, () -> {
            base.power(-1);
        });
    }

    @Test
    void testPowerOverflow() {
        SignedInt base = new SignedInt(Long.MAX_VALUE / 2);
        // Elevare al quadrato causerà overflow
        assertThrows(ArithmeticException.class, () -> {
            base.power(2);
        });
    }

    @Test
    void testToString() {
        SignedInt n = new SignedInt(-42);
        assertEquals("-42", n.toString());
    }
}