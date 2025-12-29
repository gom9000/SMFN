package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.numerics.ZnElement;

@DisplayName("ZnElement: Test delle operazioni in Z/nZ (aritmetica modulare)")
class ZnElementTest {

    // Modulo n = 7
    private static final SignedInt MOD_7 = new SignedInt(7);
    // Modulo n = 12
    private static final SignedInt MOD_12 = new SignedInt(12);

    // Helper per creare ZnElement in Z/7Z
    private ZnElement zn7(long value) {
        return new ZnElement(new SignedInt(value), MOD_7);
    }
    
    // Helper per creare ZnElement in Z/12Z
    private ZnElement zn12(long value) {
        return new ZnElement(new SignedInt(value), MOD_12);
    }

    // ======================================================================================
    // 1. COSTRUTTORE E PROPRIETÀ BASE
    // ======================================================================================

    @Test
    @DisplayName("Costruttore: Modulo deve essere positivo")
    void constructorInvalidModulus() {
        // Modulo zero
        assertThrows(IllegalArgumentException.class, () -> 
            new ZnElement(new SignedInt(5), new SignedInt(0)));
        // Modulo negativo
        assertThrows(IllegalArgumentException.class, () -> 
            new ZnElement(new SignedInt(5), new SignedInt(-5)));
    }

    @Test
    @DisplayName("Costruttore: Normalizzazione del valore")
    void constructorValueNormalization() {
        // 10 mod 7 = 3
        ZnElement tenMod7 = zn7(10);
        assertEquals(3L, tenMod7.getValue().getValue());
        assertEquals(MOD_7, tenMod7.getModulus());

        // -1 mod 7 = 6 (resto normalizzato)
        ZnElement minusOneMod7 = zn7(-1);
        assertEquals(6L, minusOneMod7.getValue().getValue());

        // 7 mod 7 = 0
        ZnElement sevenMod7 = zn7(7);
        assertEquals(0L, sevenMod7.getValue().getValue());
    }

    @Test
    @DisplayName("isMathematicallyEqualTo e equals/hashCode")
    void equalityAndHashCode() {
        ZnElement a = zn7(10); // [3] mod 7
        ZnElement b = zn7(3);  // [3] mod 7
        ZnElement c = zn7(4);  // [4] mod 7
        ZnElement d = zn12(3); // [3] mod 12 (modulo diverso)

        // Uguaglianza matematica (stesso anello, stesso valore ridotto)
        assertTrue(a.isMathematicallyEqualTo(b));
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());

        // Non uguali
        assertFalse(a.isMathematicallyEqualTo(c));
        assertFalse(a.equals(c));
        assertNotEquals(a.hashCode(), c.hashCode());
        
        // Modulo diverso
        assertFalse(a.isMathematicallyEqualTo(d), "Elementi di anelli diversi non sono matematicamente uguali.");
        assertFalse(a.equals(d), "Elementi di anelli diversi non sono uguali.");
    }
    
    @Test
    @DisplayName("Zero e IsZero")
    void zeroAndIsZero() {
        ZnElement zero = zn7(0);
        
        assertTrue(zero.isZero());
        assertEquals(zn7(0), zero.getZero());
        assertTrue(zn7(7).isZero()); // Corretto: [7] mod 7 = [0]
        
        assertFalse(zn7(1).isZero());
    }

    @Test
    @DisplayName("Copia")
    void copy() {
        ZnElement original = zn7(5);
        ZnElement copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);
    }
    
    @Test
    @DisplayName("toString")
    void testToString() {
        assertEquals("[3 mod 7]", zn7(3).toString());
        assertEquals("[1 mod 12]", zn12(13).toString());
    }

    // ======================================================================================
    // 2. OPERAZIONI DI ANELLO (RING OPERATIONS)
    // ======================================================================================

    @Nested
    @DisplayName("Addizione Modulare")
    class AdditiveOperations {
        
        @ParameterizedTest(name = "[{0}] + [{1}] = [{2}] mod 7")
        @CsvSource({
            "3, 2, 5",      // 3 + 2 = 5
            "5, 4, 2",      // 5 + 4 = 9, 9 mod 7 = 2
            "6, 1, 0",      // 6 + 1 = 7, 7 mod 7 = 0
            "6, 6, 5"       // 6 + 6 = 12, 12 mod 7 = 5
        })
        void add(long a, long b, long expected) {
            ZnElement op1 = zn7(a);
            ZnElement op2 = zn7(b);
            ZnElement result = op1.add(op2);
            
            assertEquals(zn7(expected), result);
            assertEquals(expected, result.getValue().getValue());
        }

        @Test
        @DisplayName("Addizione con modulo diverso")
        void addDifferentModulus() {
            ZnElement op1 = zn7(5);
            ZnElement op2 = zn12(5);
            assertThrows(IllegalArgumentException.class, () -> op1.add(op2));
        }
        
        @Test
        @DisplayName("Negazione Modulare")
        void negate() {
            // -[3] mod 7 = [-3] mod 7 = [4]
            ZnElement three = zn7(3);
            assertEquals(zn7(4), three.negate());
            
            // -[0] mod 7 = [0]
            assertEquals(zn7(0), zn7(0).negate());

            // -[4] mod 12 = [8]
            ZnElement fourMod12 = zn12(4);
            assertEquals(zn12(8), fourMod12.negate());
        }
    }

    @Nested
    @DisplayName("Moltiplicazione Modulare")
    class MultiplicativeOperations {
        
        @ParameterizedTest(name = "[{0}] * [{1}] = [{2}] mod 7")
        @CsvSource({
            "3, 2, 6",      // 3 * 2 = 6
            "4, 2, 1",      // 4 * 2 = 8, 8 mod 7 = 1
            "3, 3, 2"       // 3 * 3 = 9, 9 mod 7 = 2
        })
        void multiply(long a, long b, long expected) {
            ZnElement op1 = zn7(a);
            ZnElement op2 = zn7(b);
            ZnElement result = op1.multiply(op2);
            
            assertEquals(zn7(expected), result);
            assertEquals(expected, result.getValue().getValue());
        }

        @Test
        @DisplayName("Identità Moltiplicativa (One)")
        void getOne() {
            ZnElement oneMod7 = zn7(1);
            assertEquals(oneMod7, zn7(5).getOne());
            assertEquals(1L, zn7(5).getOne().getValue().getValue());
            assertEquals(MOD_7, zn7(5).getOne().getModulus());
        }

        @Test
        @DisplayName("Moltiplicazione per modulo diverso")
        void multiplyDifferentModulus() {
            ZnElement op1 = zn7(5);
            ZnElement op2 = zn12(5);
            assertThrows(IllegalArgumentException.class, () -> op1.multiply(op2));
        }
    }
}