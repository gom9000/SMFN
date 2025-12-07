package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ZnElement: Test dell'Aritmetica Modulare (Z/nZ)")
class ZnElementTest {

    // Helper: Crea un SignedInt
    private SignedInt si(long val) {
        return new SignedInt(val);
    }
    
    // Helper: Crea un ZnElement ([value] mod modulus)
    private ZnElement z(long value, long modulus) {
        return new ZnElement(si(value), si(modulus));
    }
    
    // Helper: Confronta due ZnElement
    private void assertZnElementEquals(ZnElement expected, ZnElement actual, String message) {
        // Confronta l'uguaglianza matematica che verifica sia il valore che il modulo
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   message + String.format(" | Atteso: %s, Ottenuto: %s", expected.toString(), actual.toString()));
    }

    // ======================================================================================
    // COSTRUTTORE E NORMALIZZAZIONE
    // ======================================================================================

    @Nested
    @DisplayName("Costruttore e Normalizzazione (mod n)")
    class ConstructorAndNormalizationTests {
        
        private final long MODULUS = 7; // Usiamo Z/7Z
        
        @Test
        @DisplayName("Normalizzazione: Valori Positivi")
        void normalizationPositive() {
            // 10 mod 7 = 3
            ZnElement z1 = z(10, MODULUS);
            assertEquals(si(3), z1.getValue());
            
            // 7 mod 7 = 0
            ZnElement z2 = z(7, MODULUS);
            assertEquals(si(0), z2.getValue());
            
            // 0 mod 7 = 0
            ZnElement z3 = z(0, MODULUS);
            assertEquals(si(0), z3.getValue());
        }

        @Test
        @DisplayName("Normalizzazione: Valori Negativi")
        void normalizationNegative() {
            // -1 mod 7 = 6
            ZnElement z1 = z(-1, MODULUS);
            assertEquals(si(6), z1.getValue());
            
            // -10 mod 7 = 4
            ZnElement z2 = z(-10, MODULUS);
            assertEquals(si(4), z2.getValue());
            
            // -7 mod 7 = 0
            ZnElement z3 = z(-7, MODULUS);
            assertEquals(si(0), z3.getValue());
        }

        @Test
        @DisplayName("Modulus Non Validi (0 o Negativi)")
        void invalidModulusThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> 
                new ZnElement(si(5), si(0)), 
                "Modulus 0 deve lanciare eccezione."
            );
            assertThrows(IllegalArgumentException.class, () -> 
                new ZnElement(si(5), si(-5)), 
                "Modulus negativo deve lanciare eccezione."
            );
        }
        
        @Test
        @DisplayName("Costanti e Modulo")
        void constantsAndModulus() {
            ZnElement ring = z(1, 5);
            
            assertEquals(si(5), ring.getModulus());
            assertZnElementEquals(z(0, 5), ring.getZero(), "ZERO deve avere lo stesso modulo.");
            assertZnElementEquals(z(1, 5), ring.getOne(), "ONE deve avere lo stesso modulo.");
            assertTrue(z(0, 5).isZero());
            assertFalse(z(1, 5).isZero());
        }
    }

    // ======================================================================================
    // OPERAZIONI DI ANELLO (RING OPERATIONS)
    // ======================================================================================

    @Nested
    @DisplayName("Addizione e Negazione (Gruppo Additivo)")
    class AdditiveOperations {
        
        private final long MODULUS = 11; // Usiamo Z/11Z
        private final ZnElement MOD_RING = z(0, MODULUS);

        @Test
        @DisplayName("Addizione Modulare (Overflow)")
        void add() {
            // 5 + 8 = 13. 13 mod 11 = 2
            ZnElement z1 = z(5, MODULUS);
            ZnElement z2 = z(8, MODULUS);
            assertZnElementEquals(z(2, MODULUS), z1.add(z2), "5 + 8 = 2 mod 11");
            
            // 5 + 6 = 11. 11 mod 11 = 0
            ZnElement z3 = z(6, MODULUS);
            assertZnElementEquals(z(0, MODULUS), z1.add(z3), "5 + 6 = 0 mod 11");
            
            // 5 + (-2) = 3. 3 mod 11 = 3
            ZnElement z4 = z(-2, MODULUS); // Rappresentato come 9
            assertZnElementEquals(z(3, MODULUS), z1.add(z4), "5 + (-2) = 3 mod 11");
        }
        
        @Test
        @DisplayName("Addizione con Moduli Diversi (Eccezione)")
        void addDifferentModulusThrowsException() {
            ZnElement z1 = z(5, 7);
            ZnElement z2 = z(5, 11);
            
            assertThrows(IllegalArgumentException.class, () -> 
                z1.add(z2), 
                "L'addizione deve lanciare eccezione per moduli diversi."
            );
        }

        @Test
        @DisplayName("Negazione Modulare")
        void negate() {
            // 5 -> -5. -5 mod 11 = 6
            ZnElement z1 = z(5, MODULUS);
            assertZnElementEquals(z(6, MODULUS), z1.negate(), "Negazione di 5");
            
            // 0 -> 0
            assertZnElementEquals(z(0, MODULUS), z(0, MODULUS).negate(), "Negazione di 0");

            // Verifica che z + (-z) = 0
            assertZnElementEquals(MOD_RING.getZero(), z1.add(z1.negate()), "Proprietà di Gruppo Additivo");
        }
    }

    @Nested
    @DisplayName("Moltiplicazione (Monoide Moltiplicativo)")
    class MultiplicativeOperations {
        
        private final long MODULUS = 13; // Usiamo Z/13Z

        @Test
        @DisplayName("Moltiplicazione Modulare")
        void multiply() {
            // 5 * 8 = 40. 40 mod 13 = 1
            ZnElement z1 = z(5, MODULUS);
            ZnElement z2 = z(8, MODULUS);
            assertZnElementEquals(z(1, MODULUS), z1.multiply(z2), "5 * 8 = 1 mod 13"); // 8 è l'inverso moltiplicativo di 5

            // 5 * 0 = 0
            assertZnElementEquals(z(0, MODULUS), z1.multiply(z(0, MODULUS)), "Moltiplicazione per Zero");

            // 5 * (-2) = -10. -10 mod 13 = 3
            ZnElement z3 = z(-2, MODULUS); // Rappresentato come 11
            assertZnElementEquals(z(3, MODULUS), z1.multiply(z3), "5 * (-2) = 3 mod 13");
        }
        
        @Test
        @DisplayName("Moltiplicazione con Moduli Diversi (Eccezione)")
        void multiplyDifferentModulusThrowsException() {
            ZnElement z1 = z(5, 7);
            ZnElement z2 = z(5, 13);
            
            assertThrows(IllegalArgumentException.class, () -> 
                z1.multiply(z2), 
                "La moltiplicazione deve lanciare eccezione per moduli diversi."
            );
        }
    }

    // ======================================================================================
    // UTILITÀ E CONFRONTO
    // ======================================================================================

    @Nested
    @DisplayName("Uguaglianza, Copia e Conversione")
    class UtilityAndConversionTests {
        
        private final long MODULUS = 10;

        @Test
        @DisplayName("isMathematicallyEqualTo e equals")
        void equality() {
            ZnElement z1 = z(12, MODULUS); // [2] mod 10
            ZnElement z2 = z(2, MODULUS);  // [2] mod 10
            ZnElement z3 = z(2, 5);        // [2] mod 5
            
            // isMathematicallyEqualTo verifica modulo e valore canonico
            assertTrue(z1.isMathematicallyEqualTo(z2), "12 mod 10 = 2 mod 10");
            assertFalse(z1.isMathematicallyEqualTo(z3), "Moduli diversi");
            
            // equals (Java standard) verifica anche l'uguaglianza del modulo e del valore canonico
            assertTrue(z1.equals(z2));
            assertFalse(z1.equals(z3));
        }

        @Test
        @DisplayName("valueOf (CreatableFromDouble)")
        void valueOf() {
            ZnElementFactory ring = new ZnElementFactory(new SignedInt(10));
            
            // Conversione diretta da long
            ZnElement z1 = ring.of(15.0); // 15 mod 10 = 5
            assertZnElementEquals(z(5, 10), z1, "15.0 mod 10");
            
            // Valore negativo
            ZnElement z2 = ring.of(-3.0); // -3 mod 10 = 7
            assertZnElementEquals(z(7, 10), z2, "-3.0 mod 10");
            
            // Valori che causano overflow di long (dovrebbe essere gestito da SignedInt, ma qui è intercettato)
            assertThrows(ArithmeticException.class, () -> 
                ring.of(Double.MAX_VALUE), 
                "Valore fuori dal range long."
            );
        }
        
        @Test
        @DisplayName("ToString e HashCode")
        void stringAndHash() {
            ZnElement z = z(15, 7); // [1] mod 7
            
            assertEquals("[1 mod 7]", z.toString());
            
            // HashCode deve essere coerente con equals
            assertEquals(z.hashCode(), z(1, 7).hashCode());
            assertNotEquals(z.hashCode(), z(1, 8).hashCode());
        }
    }
}