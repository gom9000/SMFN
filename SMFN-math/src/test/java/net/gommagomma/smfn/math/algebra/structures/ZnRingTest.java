package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.numerics.ZnElement;

@DisplayName("ZnRing: Test della struttura dell'Anello Z/nZ")
class ZnRingTest {

    private final ZnRing Z_7 = new ZnRing(new SignedInt(7));
    private final ZnRing Z_12 = new ZnRing(new SignedInt(12));

    // Helper per creare ZnElement direttamente da Z_7
    private ZnElement zn7(long value) {
        return Z_7.getElement(new SignedInt(value));
    }

    // ======================================================================================
    // 1. COSTRUTTORE E PROPRIETÀ BASE
    // ======================================================================================

    @Test
    @DisplayName("Costruttore: Modulo non valido")
    void constructorInvalidModulus() {
        assertThrows(IllegalArgumentException.class, () -> new ZnRing(SignedInt.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new ZnRing(new SignedInt(-5)));
    }

    @Test
    @DisplayName("Proprietà Statiche e Identità")
    void basicProperties() {
        assertEquals("Z/7Z Ring", Z_7.getName());
        assertEquals(new SignedInt(7), Z_7.getModulus());

        // Identità Additiva (Zero)
        ZnElement zero = Z_7.additiveIdentity();
        assertEquals(0L, zero.getValue().getValue());
        
        // Identità Moltiplicativa (One)
        ZnElement one = Z_7.multiplicativeIdentity();
        assertEquals(1L, one.getValue().getValue());
    }

    @Test
    @DisplayName("contains: Verifica che l'elemento appartenga all'anello")
    void containsElements() {
        ZnElement elementIn7 = zn7(5);
        ZnElement elementIn12 = Z_12.getElement(new SignedInt(5));
        
        assertTrue(Z_7.contains(elementIn7), "Z_7 contiene i suoi elementi.");
        assertFalse(Z_7.contains(elementIn12), "Z_7 non contiene elementi di Z_12.");
    }

    @Test
    @DisplayName("Factory: getElement")
    void getElementFactory() {
        // La factory deve normalizzare il valore
        ZnElement result = Z_7.getElement(new SignedInt(10)); // 10 mod 7 = 3
        assertEquals(3L, result.getValue().getValue());
        assertEquals(new SignedInt(7), result.getModulus());
    }

    @Test
    @DisplayName("Factory: of(double/long/int)")
    void ofMethods() {
        assertEquals(zn7(3), Z_7.of(3.0));
        assertEquals(zn7(15), Z_7.of(15L));
        assertEquals(zn7(4), Z_7.of(4));
        
        // Test overflow (deve delegare a SignedInt e lanciare)
        assertThrows(ArithmeticException.class, () -> Z_7.of(Double.MAX_VALUE));
        
        // Test non intero
        assertThrows(IllegalArgumentException.class, () -> Z_7.of(3.5));
    }

    // ======================================================================================
    // 2. VERIFICA DEGLI ASSIOMI DI ANELLO
    // ======================================================================================
    
    private final ZnElement a = zn7(2); // [2]
    private final ZnElement b = zn7(3); // [3]
    private final ZnElement c = zn7(5); // [5]
    private final ZnElement ZERO = Z_7.additiveIdentity();
    private final ZnElement ONE = Z_7.multiplicativeIdentity();

    @Nested
    @DisplayName("Assiomi Additivi (Gruppo Commutativo)")
    class AdditiveAxioms {
        
        @Test
        @DisplayName("A1: Chiusura (Verificata indirettamente da ZnElement)")
        void closure() {
            ZnElement result = a.add(b); // [2] + [3] = [5]
            assertTrue(Z_7.contains(result));
            assertEquals(5L, result.getValue().getValue());

            ZnElement result2 = zn7(5).add(zn7(4)); // [5] + [4] = [9] = [2] mod 7
            assertTrue(Z_7.contains(result2));
            assertEquals(2L, result2.getValue().getValue());
        }

        @Test
        @DisplayName("A2: Associatività")
        void associativity() {
            // ([2] + [3]) + [5] = [5] + [5] = [10] = [3]
            ZnElement left = a.add(b).add(c);
            // [2] + ([3] + [5]) = [2] + [8] = [2] + [1] = [3]
            ZnElement right = a.add(b.add(c));
            assertEquals(left, right, "L'addizione deve essere associativa.");
        }
        
        @Test
        @DisplayName("A3: Identità Additiva (Zero)")
        void identity() {
            assertEquals(a, a.add(ZERO), "a + 0 == a");
            assertEquals(a, ZERO.add(a), "0 + a == a");
        }
        
        @Test
        @DisplayName("A4: Inverso Additivo (Negate)")
        void inverse() {
            ZnElement inverseA = a.negate(); // -[2] = [5] mod 7
            assertEquals(ZERO, a.add(inverseA), "a + (-a) == 0");
            assertEquals(ZERO, inverseA.add(a), "(-a) + a == 0");
        }
        
        @Test
        @DisplayName("A5: Commutatività")
        void commutativity() {
            assertEquals(a.add(b), b.add(a), "L'addizione deve essere commutativa.");
        }
    }

    @Nested
    @DisplayName("Assiomi Moltiplicativi (Monoide Commutativo)")
    class MultiplicativeAxioms {
        
        @Test
        @DisplayName("M1: Chiusura")
        void closure() {
            ZnElement result = a.multiply(b); // [2] * [3] = [6]
            assertTrue(Z_7.contains(result));
            assertEquals(6L, result.getValue().getValue());

            ZnElement result2 = zn7(4).multiply(zn7(4)); // [4] * [4] = [16] = [2] mod 7
            assertTrue(Z_7.contains(result2));
            assertEquals(2L, result2.getValue().getValue());
        }

        @Test
        @DisplayName("M2: Associatività")
        void associativity() {
            // ([2] * [3]) * [5] = [6] * [5] = [30] = [2] mod 7
            ZnElement left = a.multiply(b).multiply(c);
            // [2] * ([3] * [5]) = [2] * [15] = [2] * [1] = [2] mod 7
            ZnElement right = a.multiply(b.multiply(c));
            assertEquals(left, right, "La moltiplicazione deve essere associativa.");
        }
        
        @Test
        @DisplayName("M3: Identità Moltiplicativa (Uno)")
        void identity() {
            assertEquals(a, a.multiply(ONE), "a * 1 == a");
            assertEquals(a, ONE.multiply(a), "1 * a == a");
        }
        
        @Test
        @DisplayName("M4: Commutatività")
        void commutativity() {
            assertEquals(a.multiply(b), b.multiply(a), "La moltiplicazione deve essere commutativa.");
        }
    }

    @Nested
    @DisplayName("Assiomi di Collegamento")
    class LinkageAxioms {
        
        @Test
        @DisplayName("D: Distributività")
        void distributivity() {
            // [2] * ([3] + [5]) = [2] * [8] = [2] * [1] = [2] mod 7
            ZnElement left = a.multiply(b.add(c));
            // ([2] * [3]) + ([2] * [5]) = [6] + [10] = [6] + [3] = [9] = [2] mod 7
            ZnElement right = a.multiply(b).add(a.multiply(c));
            
            assertEquals(left, right, "La moltiplicazione deve essere distributiva sull'addizione.");
        }
        
        @Test
        @DisplayName("Assorbimento di Zero")
        void zeroAbsorption() {
            assertEquals(ZERO, a.multiply(ZERO), "a * 0 == 0");
            assertEquals(ZERO, ZERO.multiply(a), "0 * a == 0");
        }
    }
}