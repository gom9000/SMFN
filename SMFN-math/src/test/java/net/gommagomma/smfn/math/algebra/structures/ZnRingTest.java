package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.numeric.ZnElement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ZnRing: Test della Struttura dell'Anello Z/nZ")
class ZnRingTest {

    // Helper: Crea un SignedInt
    private SignedInt si(long val) {
        return new SignedInt(val);
    }
    
    // Helper: Confronta due ZnElement
    private void assertZnElementEquals(ZnElement expected, ZnElement actual, String message) {
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   message + String.format(" | Atteso: %s, Ottenuto: %s", 
                                          expected.toString(), actual.toString()));
    }

    // ======================================================================================
    // 1. COSTRUTTORE E PROPRIETÀ BASE
    // ======================================================================================

    @Nested
    @DisplayName("Costruzione e Identità")
    class ConstructorAndIdentityTests {
        
        @Test
        @DisplayName("Costruzione e Modulus Validi")
        void validConstruction() {
            SignedInt modulus = si(12);
            ZnRing ring = new ZnRing(modulus);
            
            assertEquals(modulus, ring.getModulus(), "Il modulo deve corrispondere a quello fornito.");
            assertEquals("Z/12Z Ring", ring.getName(), "Il nome deve riflettere il modulo.");
        }

        @Test
        @DisplayName("Modulus Non Validi (0 o Negativi)")
        void invalidModulusThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> 
                new ZnRing(si(0)), 
                "Modulus 0 deve lanciare eccezione."
            );
            assertThrows(IllegalArgumentException.class, () -> 
                new ZnRing(si(-5)), 
                "Modulus negativo deve lanciare eccezione."
            );
        }
        
        @Test
        @DisplayName("Identità Additiva (Zero)")
        void additiveIdentityTest() {
            ZnRing ring = new ZnRing(si(5));
            ZnElement zero = ring.additiveIdentity();
            
            // L'identità deve essere [0] mod 5
            assertEquals(si(0), zero.getValue());
            assertEquals(si(5), zero.getModulus());
            
            // Verifica che l'identità sia la stessa istanza in cache
            assertSame(ring.additiveIdentity(), zero, "L'identità deve essere l'istanza cached.");
        }

        @Test
        @DisplayName("Identità Moltiplicativa (One)")
        void multiplicativeIdentityTest() {
            ZnRing ring = new ZnRing(si(5));
            ZnElement one = ring.multiplicativeIdentity();
            
            // L'identità deve essere [1] mod 5
            assertEquals(si(1), one.getValue());
            assertEquals(si(5), one.getModulus());
            
            // Verifica che l'identità sia la stessa istanza in cache
            assertSame(ring.multiplicativeIdentity(), one, "L'identità deve essere l'istanza cached.");
        }
    }

    // ======================================================================================
    // 2. CONFRONTO E FACTORY
    // ======================================================================================

    @Nested
    @DisplayName("Inclusione, Uguaglianza e Factory")
    class UtilityAndFactoryTests {
        
        private final ZnRing RING_7 = new ZnRing(si(7));
        private final ZnRing RING_7_COPY = new ZnRing(si(7));
        private final ZnRing RING_5 = new ZnRing(si(5));

        @Test
        @DisplayName("equals e hashCode")
        void equalityAndHash() {
            // Uguaglianza per stesso modulo
            assertTrue(RING_7.equals(RING_7_COPY), "Anelli con lo stesso modulo sono uguali.");
            assertEquals(RING_7.hashCode(), RING_7_COPY.hashCode(), "HashCodes devono essere uguali.");
            
            // Disuguaglianza per moduli diversi
            assertFalse(RING_7.equals(RING_5), "Anelli con moduli diversi non sono uguali.");
            assertNotEquals(RING_7.hashCode(), RING_5.hashCode(), "HashCodes devono essere diversi.");
        }
        
        @Test
        @DisplayName("contains: Elementi validi per l'Anello")
        void containsTest() {
            ZnElement e_valid = new ZnElement(si(5), si(7)); // [5] mod 7
            ZnElement e_invalid = new ZnElement(si(5), si(8)); // [5] mod 8
            
            assertTrue(RING_7.contains(e_valid), "L'anello deve contenere elementi con lo stesso modulo.");
            assertFalse(RING_7.contains(e_invalid), "L'anello non deve contenere elementi con modulo diverso.");
        }
        
        @Test
        @DisplayName("getElement (Factory): Riduzione e Normalizzazione")
        void getElementTest() {
            ZnRing ring = new ZnRing(si(12));
            
            // Valore maggiore del modulo (15 mod 12 = 3)
            ZnElement z1 = ring.getElement(si(15));
            assertZnElementEquals(new ZnElement(si(3), si(12)), z1, "La factory deve normalizzare i valori > n.");
            
            // Valore negativo (-5 mod 12 = 7)
            ZnElement z2 = ring.getElement(si(-5));
            assertZnElementEquals(new ZnElement(si(7), si(12)), z2, "La factory deve normalizzare i valori negativi.");
            
            // Valore già canonico
            ZnElement z3 = ring.getElement(si(11));
            assertZnElementEquals(new ZnElement(si(11), si(12)), z3, "La factory deve accettare valori canonici.");
            
            // Verifica che l'elemento creato abbia il modulo corretto
            assertEquals(si(12), z1.getModulus());
        }
    }

    // ======================================================================================
    // 3. VERIFICA DEGLI ASSIOMI DI ANELLO COMMUTATIVO
    // ======================================================================================

    @Nested
    @DisplayName("Assiomi Aritmetici")
    class RingAxiomsTests {
        
        // Test implicito degli assiomi (add, multiply, negate) usando la factory
        private final ZnRing RING = new ZnRing(si(6)); // Z/6Z
        private final ZnElement a = RING.getElement(si(5)); // [5] mod 6
        private final ZnElement b = RING.getElement(si(3)); // [3] mod 6
        private final ZnElement c = RING.getElement(si(4)); // [4] mod 6

        private final ZnElement ZERO = RING.additiveIdentity(); // [0] mod 6
        private final ZnElement ONE = RING.multiplicativeIdentity(); // [1] mod 6

        @Test
        @DisplayName("Assiomi Additivi (Gruppo Abeliano)")
        void additiveAxioms() {
            // Chiusura: a + b = 8 mod 6 = 2
            assertZnElementEquals(RING.getElement(si(2)), a.add(b), "Chiusura");
            
            // Associatività: (a + b) + c == a + (b + c)
            assertZnElementEquals(a.add(b).add(c), a.add(b.add(c)), "Associatività");
            
            // Identità: a + 0 == a
            assertZnElementEquals(a, a.add(ZERO), "Identità");
            
            // Inverso: a + (-a) == 0
            assertZnElementEquals(ZERO, a.add(a.negate()), "Inverso"); // [5] + [-5] = 5 + 1 = 6 mod 6 = 0
            
            // Commutatività: a + b == b + a
            assertZnElementEquals(a.add(b), b.add(a), "Commutatività");
        }

        @Test
        @DisplayName("Assiomi Moltiplicativi (Monoide Commutativo)")
        void multiplicativeAxioms() {
            // Chiusura: a * c = 5 * 4 = 20 mod 6 = 2
            assertZnElementEquals(RING.getElement(si(2)), a.multiply(c), "Chiusura");
            
            // Associatività: (a * b) * c == a * (b * c)
            assertZnElementEquals(a.multiply(b).multiply(c), a.multiply(b.multiply(c)), "Associatività");
            
            // Identità: a * 1 == a
            assertZnElementEquals(a, a.multiply(ONE), "Identità");
            
            // Commutatività: a * b == b * a
            assertZnElementEquals(a.multiply(b), b.multiply(a), "Commutatività");
        }
        
        @Test
        @DisplayName("Assioma Distributivo")
        void distributiveAxiom() {
            // a * (b + c) == (a * b) + (a * c)
            // 5 * (3 + 4) = 5 * 7 = 35 mod 6 = 5
            // (5 * 3) + (5 * 4) = 15 + 20 = 35 mod 6 = 5
            ZnElement left = a.multiply(b.add(c));
            ZnElement right = a.multiply(b).add(a.multiply(c));
            
            assertZnElementEquals(left, right, "Distributività");
        }

        @Test
        @DisplayName("Zero Assorbe (Anello)")
        void zeroAbsorption() {
            // a * 0 == 0
            assertZnElementEquals(ZERO, a.multiply(ZERO), "Zero Assorbe");
        }
    }
}