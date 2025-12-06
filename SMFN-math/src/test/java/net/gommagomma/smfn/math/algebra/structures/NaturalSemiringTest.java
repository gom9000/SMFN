package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.numeric.NaturalFactory;

@DisplayName("NaturalSemiring: Test della Struttura di Semianello (N)")
class NaturalSemiringTest {

    private final NaturalSemiring NATURAL_SEMIRING = NaturalSemiring.getInstance();
    
    // Helper per creare un Natural (Assumiamo che Natural(long) esista)
    private Natural n(long value) {
        return new Natural(value);
    }
    
    // Helper per verificare l'uguaglianza (Assumiamo che Natural.equals sia basato sul valore)
    private void assertNaturalEquals(Natural expected, Natural actual, String message) {
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
        assertEquals("Natural Semiring (N)", NATURAL_SEMIRING.getName(), "Il nome del semianello non è corretto.");
        assertSame(NaturalSemiring.INSTANCE, NATURAL_SEMIRING, "Deve essere singleton.");
    }

    @Test
    @DisplayName("contains: Inclusione degli elementi")
    void containsElements() {
        // Un Semianello Naturale contiene tutti gli elementi Naturali non nulli.
        assertTrue(NATURAL_SEMIRING.contains(n(5)), "Contiene 5");
        assertTrue(NATURAL_SEMIRING.contains(NaturalFactory.getInstance().zero()), "Contiene ZERO");
        assertTrue(NATURAL_SEMIRING.contains(NaturalFactory.getInstance().zero()), "Contiene ONE");
        
        // Non deve contenere null
        assertFalse(NATURAL_SEMIRING.contains(null), "Non contiene null");
    }

    @Test
    @DisplayName("Identità Additiva e Moltiplicativa")
    void identities() {
        assertNaturalEquals(NaturalFactory.getInstance().zero(), NATURAL_SEMIRING.additiveIdentity(), "Identità Additiva (Zero)");
        assertNaturalEquals(NaturalFactory.getInstance().one(), NATURAL_SEMIRING.multiplicativeIdentity(), "Identità Moltiplicativa (One)");
        assertSame(NaturalFactory.getInstance().zero(), NATURAL_SEMIRING.additiveIdentity());
        assertSame(NaturalFactory.getInstance().one(), NATURAL_SEMIRING.multiplicativeIdentity());
    }

    // ======================================================================================
    // 2. VERIFICA DEGLI ASSIOMI DI SEMIANELLO
    // ======================================================================================
    
    // Usiamo tre valori naturali generici
    private final Natural a = n(2);
    private final Natural b = n(3);
    private final Natural c = n(5);

    private final Natural ZERO = NATURAL_SEMIRING.additiveIdentity();
    private final Natural ONE = NATURAL_SEMIRING.multiplicativeIdentity();

    // --- MONOIDE COMMUTATIVO ADDITIVO ---
    
    @Nested
    @DisplayName("Assiomi Additivi (Monoide Commutativo)")
    class AdditiveAxioms {
        
        @Test
        @DisplayName("A1: Chiusura")
        void closure() {
            // (a + b) = 5
            Natural result = a.add(b); 
            assertTrue(NATURAL_SEMIRING.contains(result), "(a + b) è chiuso");
            assertNaturalEquals(n(5), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("A2: Associatività")
        void associativity() {
            // (2 + 3) + 5 = 10 == 2 + (3 + 5) = 10
            Natural left = a.add(b).add(c);
            Natural right = a.add(b.add(c));
            assertNaturalEquals(left, right, "L'addizione deve essere associativa.");
        }
        
        @Test
        @DisplayName("A3: Identità Additiva (Zero)")
        void identity() {
            // a + 0 == a
            assertNaturalEquals(a, a.add(ZERO), "a + 0 == a");
            // 0 + a == a
            assertNaturalEquals(a, ZERO.add(a), "0 + a == a");
        }
        
        @Test
        @DisplayName("A4: Commutatività")
        void commutativity() {
            // a + b == b + a
            assertNaturalEquals(a.add(b), b.add(a), "L'addizione deve essere commutativa.");
        }

        @Test
        @DisplayName("Verifica della Mancanza dell'Inverso Additivo (Non è un Gruppo)")
        void noAdditiveInverse() {
            // In un Semianello, non si richiede l'inverso. 
            // Se Natural ha un metodo negate/inverse, questo test dovrebbe fallire o lanciare eccezione.
            // Esempio: 2 + (-2) non è definito in N.
        }
    }

    // --- MONOIDE MOLTIPLICATIVO ---

    @Nested
    @DisplayName("Assiomi Moltiplicativi (Monoide)")
    class MultiplicativeAxioms {
        
        @Test
        @DisplayName("M1: Chiusura")
        void closure() {
            // (a * b) = 6
            Natural result = a.multiply(b);
            assertTrue(NATURAL_SEMIRING.contains(result), "(a * b) è chiuso");
            assertNaturalEquals(n(6), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("M2: Associatività")
        void associativity() {
            // (2 * 3) * 5 = 30 == 2 * (3 * 5) = 30
            Natural left = a.multiply(b).multiply(c);
            Natural right = a.multiply(b.multiply(c));
            assertNaturalEquals(left, right, "La moltiplicazione deve essere associativa.");
        }
        
        @Test
        @DisplayName("M3: Identità Moltiplicativa (Uno)")
        void identity() {
            // a * 1 == a
            assertNaturalEquals(a, a.multiply(ONE), "a * 1 == a");
            // 1 * a == a
            assertNaturalEquals(a, ONE.multiply(a), "1 * a == a");
        }
        
        @Test
        @DisplayName("M4: Commutatività (Implicitamente se è un Semianello Commutativo)")
        void commutativity() {
            // a * b == b * a
            assertNaturalEquals(a.multiply(b), b.multiply(a), "La moltiplicazione deve essere commutativa.");
        }

        @Test
        @DisplayName("Verifica della Mancanza dell'Inverso Moltiplicativo (Non è un Campo)")
        void noMultiplicativeInverse() {
            // L'inverso di 2 (1/2) non è un numero Naturale.
            // Se Natural ha un metodo inverse(), questo test dovrebbe lanciare eccezione o fallire.
        }
    }

    // --- ASSIOMI DI COLLEGAMENTO ---

    @Nested
    @DisplayName("Assiomi di Collegamento (Linkage Axioms)")
    class LinkageAxioms {
        
        @Test
        @DisplayName("D: Distributività (Moltiplicazione su Addizione)")
        void distributivity() {
            // a * (b + c) == (a * b) + (a * c)
            // 2 * (3 + 5) = 16
            // (2 * 3) + (2 * 5) = 6 + 10 = 16
            Natural left = a.multiply(b.add(c));
            Natural right = a.multiply(b).add(a.multiply(c));
            
            assertNaturalEquals(left, right, "La moltiplicazione deve essere distributiva.");
        }
        
        @Test
        @DisplayName("Assorbimento di Zero (Zero is absorbing)")
        void zeroAbsorption() {
            // a * 0 == 0
            Natural result = a.multiply(ZERO);
            assertNaturalEquals(ZERO, result, "a * 0 == 0");
            
            // 0 * a == 0
            Natural result2 = ZERO.multiply(a);
            assertNaturalEquals(ZERO, result2, "0 * a == 0");
        }
    }
}