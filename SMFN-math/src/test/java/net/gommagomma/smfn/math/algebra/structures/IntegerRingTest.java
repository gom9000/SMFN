package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;

@DisplayName("IntegerRing: Test della Struttura di Anello Commutativo (Z)")
class IntegerRingTest {

    private final IntegerRing INTEGER_RING = IntegerRing.getInstance();
    
    // Helper per creare un SignedInt
    private SignedInt si(long value) {
        return new SignedInt(value);
    }
    
    // Helper per verificare l'uguaglianza (SignedInt usa isMathematicallyEqualTo)
    private void assertSignedIntEquals(SignedInt expected, SignedInt actual, String message) {
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   message + String.format(" | Atteso: %s, Ottenuto: %s", 
                                          expected.toString(), actual.toString()));
    }

    // ======================================================================================
    // 1. PROPRIETÀ BASE DELLA STRUTTURA
    // ======================================================================================

    @Test
    @DisplayName("Proprietà Statiche e Identità")
    void basicProperties() {
        assertEquals("Integer Ring (Z)", INTEGER_RING.getName(), "Il nome dell'anello non è corretto.");
        assertSame(IntegerRing.INSTANCE, INTEGER_RING, "Deve essere singleton.");
    }

    @Test
    @DisplayName("contains: Inclusione degli elementi")
    void containsElements() {
        // Un IntegerRing contiene ogni oggetto SignedInt non nullo.
        assertTrue(INTEGER_RING.contains(si(5)), "Contiene 5");
        assertTrue(INTEGER_RING.contains(si(-10)), "Contiene -10");
        assertTrue(INTEGER_RING.contains(IntegerRing.getInstance().zero()), "Contiene ZERO");
        
        // Non deve contenere null
        assertFalse(INTEGER_RING.contains(null), "Non contiene null");
    }

    @Test
    @DisplayName("Identità Additiva e Moltiplicativa")
    void identities() {
        assertSignedIntEquals(IntegerRing.getInstance().zero(), INTEGER_RING.additiveIdentity(), "Identità Additiva (Zero)");
        assertSignedIntEquals(IntegerRing.getInstance().one(), INTEGER_RING.multiplicativeIdentity(), "Identità Moltiplicativa (One)");
        assertSame(IntegerRing.getInstance().zero(), INTEGER_RING.additiveIdentity());
        assertSame(IntegerRing.getInstance().one(), INTEGER_RING.multiplicativeIdentity());
    }

    // ======================================================================================
    // 2. VERIFICA DEGLI ASSIOMI DI ANELLO COMMUTATIVO
    // ======================================================================================
    
    // Usiamo tre valori interi generici
    private final SignedInt a = si(5);
    private final SignedInt b = si(-8);
    private final SignedInt c = si(3);

    private final SignedInt ZERO = INTEGER_RING.additiveIdentity();
    private final SignedInt ONE = INTEGER_RING.multiplicativeIdentity();

    // --- GRUPPO ABELIANO ADDITIVO (Assiomi G1-G5) ---
    
    @Nested
    @DisplayName("Assiomi Additivi (Gruppo Abeliano)")
    class AdditiveAxioms {
        
        @Test
        @DisplayName("G1: Chiusura")
        void closure() {
            // (a + b) = -3
            SignedInt result = a.add(b); 
            assertTrue(INTEGER_RING.contains(result), "(a + b) è chiuso");
            assertSignedIntEquals(si(-3), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("G2: Associatività")
        void associativity() {
            // (5 + (-8)) + 3 = 0 == 5 + (-8 + 3) = 0
            SignedInt left = a.add(b).add(c);
            SignedInt right = a.add(b.add(c));
            assertSignedIntEquals(left, right, "L'addizione deve essere associativa.");
        }
        
        @Test
        @DisplayName("G3: Identità Additiva (Zero)")
        void identity() {
            // a + 0 == a
            assertSignedIntEquals(a, a.add(ZERO), "a + 0 == a");
        }

        @Test
        @DisplayName("G4: Inverso Additivo (Negazione)")
        void inverse() {
            // a + (-a) == 0
            SignedInt negated = a.negate(); // -5
            assertSignedIntEquals(ZERO, a.add(negated), "a + (-a) == 0");
            
            // (-8) + (8) = 0
            assertSignedIntEquals(ZERO, b.add(b.negate()), "b + (-b) == 0");
        }
        
        @Test
        @DisplayName("G5: Commutatività")
        void commutativity() {
            // a + b == b + a
            assertSignedIntEquals(a.add(b), b.add(a), "L'addizione deve essere commutativa.");
        }
    }

    // --- MONOIDE COMMUTATIVO MOLTIPLICATIVO (Assiomi M1-M4) ---

    @Nested
    @DisplayName("Assiomi Moltiplicativi (Monoide Commutativo)")
    class MultiplicativeAxioms {
        
        @Test
        @DisplayName("M1: Chiusura")
        void closure() {
            // (a * b) = -40
            SignedInt result = a.multiply(b);
            assertTrue(INTEGER_RING.contains(result), "(a * b) è chiuso");
            assertSignedIntEquals(si(-40), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("M2: Associatività")
        void associativity() {
            // (5 * -8) * 3 = -120 == 5 * (-8 * 3) = -120
            SignedInt left = a.multiply(b).multiply(c);
            SignedInt right = a.multiply(b.multiply(c));
            assertSignedIntEquals(left, right, "La moltiplicazione deve essere associativa.");
        }
        
        @Test
        @DisplayName("M3: Identità Moltiplicativa (Uno)")
        void identity() {
            // a * 1 == a
            assertSignedIntEquals(a, a.multiply(ONE), "a * 1 == a");
            // 1 * a == a
            assertSignedIntEquals(a, ONE.multiply(a), "1 * a == a");
        }
        
        @Test
        @DisplayName("M4: Commutatività")
        void commutativity() {
            // a * b == b * a
            assertSignedIntEquals(a.multiply(b), b.multiply(a), "La moltiplicazione deve essere commutativa.");
        }
    }

    // --- ASSIOMA DI DISTRIBUTIVITÀ ---

    @Nested
    @DisplayName("Assioma Distributivo")
    class DistributiveAxiom {
        
        @Test
        @DisplayName("D: Distributività della moltiplicazione sull'addizione")
        void distributivity() {
            // a * (b + c) == (a * b) + (a * c)
            // 5 * (-8 + 3) = 5 * (-5) = -25
            // (5 * -8) + (5 * 3) = -40 + 15 = -25
            SignedInt left = a.multiply(b.add(c));
            SignedInt right = a.multiply(b).add(a.multiply(c));
            
            assertSignedIntEquals(left, right, "La moltiplicazione deve essere distributiva.");
        }
    }
    
    // --- PROPRIETÀ EXTRA (Non è un Campo) ---
    
    @Nested
    @DisplayName("Proprietà Aggiuntive (Non-Campo)")
    class NonFieldProperties {
        
        @Test
        @DisplayName("Mancanza dell'Inverso Moltiplicativo (non-Field)")
        void noMultiplicativeInverse() {
            // L'anello Z non è un campo, solo 1 e -1 hanno inversi in Z.
            // Se SignedInt implementa inverse(), dovrebbe lanciare un'eccezione o restituire null/zero.
            // Esempio: L'inverso di 5 (1/5) non è in Z.
        }
    }
}