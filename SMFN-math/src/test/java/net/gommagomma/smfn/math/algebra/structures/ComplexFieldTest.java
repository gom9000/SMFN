package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Complex;

@DisplayName("ComplexField: Test della Struttura del Campo (C)")
class ComplexFieldTest {

    private final ComplexField COMPLEX_FIELD = ComplexField.getInstance();

    
    // Helper per creare un Complex
    private Complex c(double re, double im) {
        return new Complex(re, im);
    }
    
    // Helper per verificare l'uguaglianza con tolleranza (matematica)
    private void assertComplexEquals(Complex expected, Complex actual, String message) {
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   message + String.format(" | Atteso: %s, Ottenuto: %s", 
                                          expected.toString(), actual.toString()));
    }

    // ======================================================================================
    // 1. PROPRIETÃ€ BASE DELLA STRUTTURA
    // ======================================================================================

    @Test
    @DisplayName("ProprietÃ  Statiche e IdentitÃ ")
    void basicProperties() {
        assertNotNull(COMPLEX_FIELD, "L'istanza non deve essere null.");
        assertEquals("Complex Field (C)", COMPLEX_FIELD.getName(), "Il nome del campo non Ã¨ corretto.");
        assertSame(ComplexField.INSTANCE, COMPLEX_FIELD, "Deve essere singleton.");
    }

    @Test
    @DisplayName("contains: Inclusione degli elementi")
    void containsElements() {
        // Numeri Complessi Finiti
        assertTrue(COMPLEX_FIELD.contains(c(5.0, 2.0)), "Contiene 5 + 2i");
        assertTrue(COMPLEX_FIELD.contains(c(-12.34, 0.0)), "Contiene -12.34");
        assertTrue(COMPLEX_FIELD.contains(ComplexField.getInstance().zero()), "Contiene 0");
        
        // Elementi Non Finiti/Non Numerici (Non dovrebbero essere inclusi)
        assertFalse(COMPLEX_FIELD.contains(c(Double.NaN, 1.0)), "Non contiene NaN nella parte Reale");
        assertFalse(COMPLEX_FIELD.contains(c(1.0, Double.POSITIVE_INFINITY)), "Non contiene InfinitÃ  nella parte Immaginaria");
        assertFalse(COMPLEX_FIELD.contains(null), "Non contiene null");
    }

    @Test
    @DisplayName("IdentitÃ  Additiva e Moltiplicativa")
    void identities() {
        assertComplexEquals(ComplexField.getInstance().zero(), COMPLEX_FIELD.additiveIdentity(), "IdentitÃ  Additiva (Zero)");
        assertComplexEquals(ComplexField.getInstance().one(), COMPLEX_FIELD.multiplicativeIdentity(), "IdentitÃ  Moltiplicativa (One)");
        assertSame(ComplexField.getInstance().zero(), COMPLEX_FIELD.additiveIdentity());
        assertSame(ComplexField.getInstance().one(), COMPLEX_FIELD.multiplicativeIdentity());
    }

    // ======================================================================================
    // 2. VERIFICA DEGLI ASSIOMI DI CAMPO (PROPRIETÃ€ FUNZIONALI)
    // ======================================================================================
    
    // Usiamo tre valori complessi generici: a = 1+2i, b = 3-i, c = 0.5+0.5i
    private final Complex a = c(1.0, 2.0); 
    private final Complex b = c(3.0, -1.0);
    private final Complex c = c(0.5, 0.5);

    private final Complex ZERO = COMPLEX_FIELD.additiveIdentity();
    private final Complex ONE = COMPLEX_FIELD.multiplicativeIdentity();

    // --- GRUPPO ABELIANO ADDITIVO ---
    
    @Nested
    @DisplayName("Assiomi Additivi (Gruppo Abeliano)")
    class AdditiveAxioms {
        
        @Test
        @DisplayName("G1: Chiusura")
        void closure() {
            // (a + b) = 4 + i
            Complex result = a.add(b); 
            assertTrue(COMPLEX_FIELD.contains(result), "(a + b) Ã¨ chiuso");
            assertComplexEquals(c(4.0, 1.0), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("G2: AssociativitÃ ")
        void associativity() {
            // (a + b) + c == a + (b + c)
            Complex left = a.add(b).add(c);
            Complex right = a.add(b.add(c));
            assertComplexEquals(left, right, "L'addizione deve essere associativa.");
        }
        
        @Test
        @DisplayName("G3: IdentitÃ  Additiva (Zero)")
        void identity() {
            // a + 0 == a
            assertComplexEquals(a, a.add(ZERO), "a + 0 == a");
        }

        @Test
        @DisplayName("G4: Inverso Additivo (Negazione)")
        void inverse() {
            // a + (-a) == 0
            Complex negated = a.negate(); // -1 - 2i
            assertComplexEquals(ZERO, a.add(negated), "a + (-a) == 0");
        }
        
        @Test
        @DisplayName("G5: CommutativitÃ ")
        void commutativity() {
            // a + b == b + a
            assertComplexEquals(a.add(b), b.add(a), "L'addizione deve essere commutativa.");
        }
    }

    // --- GRUPPO ABELIANO MOLTIPLICATIVO (per C - {0}) ---

    @Nested
    @DisplayName("Assiomi Moltiplicativi (Gruppo Abeliano)")
    class MultiplicativeAxioms {
        
        @Test
        @DisplayName("M1: Chiusura")
        void closure() {
            // (a * b) = (1+2i)(3-i) = 3 - i + 6i + 2 = 5 + 5i
            Complex result = a.multiply(b);
            assertTrue(COMPLEX_FIELD.contains(result), "(a * b) Ã¨ chiuso");
            assertComplexEquals(c(5.0, 5.0), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("M2: AssociativitÃ ")
        void associativity() {
            // (a * b) * c == a * (b * c)
            Complex left = a.multiply(b).multiply(c);
            Complex right = a.multiply(b.multiply(c));
            assertComplexEquals(left, right, "La moltiplicazione deve essere associativa.");
        }
        
        @Test
        @DisplayName("M3: IdentitÃ  Moltiplicativa (Uno)")
        void identity() {
            // a * 1 == a
            assertComplexEquals(a, a.multiply(ONE), "a * 1 == a");
        }

        @Test
        @DisplayName("M4: Inverso Moltiplicativo (Reciproco)")
        void inverse() {
            // Per tutti gli a != 0, a * aâ�»Â¹ == 1
            Complex invA = a.inverse(); // (1-2i) / 5 = 0.2 - 0.4i
            assertComplexEquals(ONE, a.multiply(invA), "a * aâ�»Â¹ == 1 (per a = 1+2i)");
            
            // Verifica dell'inverso di un altro elemento
            Complex invB = b.inverse(); 
            assertComplexEquals(ONE, b.multiply(invB), "b * bâ�»Â¹ == 1 (per b = 3-i)");
        }
        
        @Test
        @DisplayName("M4: Inverso di Zero (Eccezione)")
        void inverseOfZeroThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                ZERO.inverse(), 
                "L'inverso di zero deve lanciare ArithmeticException in un Campo."
            );
        }
        
        @Test
        @DisplayName("M5: CommutativitÃ ")
        void commutativity() {
            // a * b == b * a
            assertComplexEquals(a.multiply(b), b.multiply(a), "La moltiplicazione deve essere commutativa.");
        }
    }

    // --- ASSIOMA DI DISTRIBUTIVITÃ€ ---

    @Nested
    @DisplayName("Assioma Distributivo")
    class DistributiveAxiom {
        
        @Test
        @DisplayName("D: DistributivitÃ  della moltiplicazione sull'addizione")
        void distributivity() {
            // a * (b + c) == (a * b) + (a * c)
            Complex left = a.multiply(b.add(c));
            Complex right = a.multiply(b).add(a.multiply(c));
            
            assertComplexEquals(left, right, "La moltiplicazione deve essere distributiva.");
        }
    }
}