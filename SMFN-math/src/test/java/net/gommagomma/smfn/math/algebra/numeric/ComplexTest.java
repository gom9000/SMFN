package net.gommagomma.smfn.math.algebra.numeric;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.gommagomma.smfn.math.utils.MathConstants;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Complex: Test delle proprietà di Campo e Funzioni Complesse")
class ComplexTest {

    // Helper: Usiamo una tolleranza specifica per il testing float, ad esempio 1e-12
    private static final double TOLERANCE = 1e-12; 
    
    // Helper per una creazione rapida (a + bi)
    private Complex c(double re, double im) {
        return new Complex(re, im);
    }
    
    // Helper per assertEquals con Complex
    private void assertComplexEquals(Complex expected, Complex actual, String message) {
        // Usa isMathematicallyEqualTo che dovrebbe usare MathConstants.EPSILON
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   message + String.format(" | Atteso: %.12f + %.12fi, Ottenuto: %.12f + %.12fi", 
                                          expected.getRe(), expected.getIm(), actual.getRe(), actual.getIm()));
    }

    // ======================================================================================
    // COSTRUTTORI E PROPRIETÀ BASE
    // ======================================================================================

    @Test
    @DisplayName("Costruttori, Getters e Costanti")
    void constructorAndBasicProps() {
        Complex z1 = c(3.0, -4.0);
        Complex z2 = new Complex(5.0); // Costruttore con solo Real
        Complex z3 = new Complex(new Real(6.0)); // Costruttore con Real object

        assertEquals(3.0, z1.getRe());
        assertEquals(-4.0, z1.getIm());
        assertEquals(5.0, z2.getRe());
        assertEquals(0.0, z2.getIm());
        assertEquals(6.0, z3.getRe());
        assertEquals(0.0, z3.getIm());

        assertComplexEquals(c(0.0, 0.0), ComplexFactory.getInstance().zero(), "Verifica ZERO");
        assertComplexEquals(c(1.0, 0.0), ComplexFactory.getInstance().one(), "Verifica ONE");
    }

    @Test
    @DisplayName("Uguaglianza Matematica (Tolleranza)")
    void mathematicalEquality() {
        // Assumiamo che MathConstants.EPSILON sia la tolleranza usata
        final double epsilon = MathConstants.EPSILON; // Usiamo 1e-15 come valore tipico
        
        Complex z = c(5.0, 3.0);
        Complex z_close = c(5.0 + epsilon / 2.0, 3.0);
        Complex z_far = c(5.0 + epsilon * 2.0, 3.0);

        assertTrue(z.isMathematicallyEqualTo(z_close), "Entro tolleranza");
        assertFalse(z.isMathematicallyEqualTo(z_far), "Fuori tolleranza");
        
        // Verifichiamo la gestione di NaN/Inf se è stata aggiunta
        // Nota: Aggiungere assertThrows o assertTrue(NaN.isMathematicallyEqualTo(NaN)) 
        // dipende dalla correzione in isMathematicallyEqualTo.
    }
    
    @Test
    @DisplayName("Copia, equals e hashCode")
    void copyEqualsHashCode() {
        Complex z = c(2.0, -1.0);
        Complex copy = z.copy();
        
        assertComplexEquals(z, copy, "La copia deve essere matematicamente uguale");
        assertNotSame(z, copy, "La copia non deve essere la stessa istanza");
        
        // equals e hashCode devono usare l'uguaglianza bit a bit per coerenza
        assertTrue(z.equals(c(2.0, -1.0)));
        assertEquals(z.hashCode(), c(2.0, -1.0).hashCode());
        assertFalse(z.equals(c(2.0, -1.0 + 1e-16))); // Differenza bit a bit
    }

    // ======================================================================================
    // OPERAZIONI DI CAMPO (FIELD OPERATIONS)
    // ======================================================================================

    @Nested
    @DisplayName("Addizione e Negazione (Gruppo Additivo)")
    class AdditiveOperations {
        
        @ParameterizedTest(name = "({0} + {1}i) + ({2} + {3}i) = {4} + {5}i")
        @CsvSource({
            "3.0, 4.0, 1.0, 2.0, 4.0, 6.0",       // (3+4i) + (1+2i) = 4+6i
            "5.0, -2.0, -5.0, 2.0, 0.0, 0.0",     // (5-2i) + (-5+2i) = 0
            "10.0, 0.0, 0.0, -5.0, 10.0, -5.0"    // 10 + (-5i) = 10-5i
        })
        void add(double r1, double i1, double r2, double i2, double rExp, double iExp) {
            Complex z1 = c(r1, i1);
            Complex z2 = c(r2, i2);
            assertComplexEquals(c(rExp, iExp), z1.add(z2), "Addizione");
        }

        @Test
        void negate() {
            assertComplexEquals(c(-3.5, 4.5), c(3.5, -4.5).negate(), "Negazione");
            assertComplexEquals(ComplexFactory.getInstance().zero(), c(3.0, 4.0).add(c(3.0, 4.0).negate()), "Addizione con Inverso");
        }
    }

    @Nested
    @DisplayName("Moltiplicazione e Inverso (Gruppo Moltiplicativo)")
    class MultiplicativeOperations {
        
        @ParameterizedTest(name = "({0} + {1}i) * ({2} + {3}i) = {4} + {5}i")
        @CsvSource({
            "2.0, 3.0, 1.0, 5.0, -13.0, 13.0",    // (2+3i)(1+5i) = (2-15) + (10+3)i = -13+13i
            "0.0, 1.0, 0.0, 1.0, -1.0, 0.0",      // i * i = -1
            "5.0, 0.0, 2.0, 4.0, 10.0, 20.0"      // 5 * (2+4i) = 10+20i
        })
        void multiply(double r1, double i1, double r2, double i2, double rExp, double iExp) {
            Complex z1 = c(r1, i1);
            Complex z2 = c(r2, i2);
            assertComplexEquals(c(rExp, iExp), z1.multiply(z2), "Moltiplicazione");
        }

        @Test
        void inverse() {
            Complex z = c(3.0, 4.0); // z * z^-1 = 1
            // 1/z = (3 - 4i) / (3^2 + 4^2) = (3/25) - (4/25)i
            double rExp = 3.0 / 25.0;
            double iExp = -4.0 / 25.0;
            
            Complex inverse = z.inverse();
            
            assertComplexEquals(c(rExp, iExp), inverse, "Inverso");
            
            // Verifica Proprietà di Campo
            assertComplexEquals(ComplexFactory.getInstance().one(), z.multiply(inverse), "z * z^-1 deve essere 1");
        }

        @Test
        void inverseOfZeroThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                ComplexFactory.getInstance().zero().inverse(), 
                "L'inverso di zero deve lanciare ArithmeticException."
            );
        }
    }

    // ======================================================================================
    // CAPACITÀ AGGIUNTIVE
    // ======================================================================================

    @Nested
    @DisplayName("Modulo, Norma e Coniugato")
    class NormAndConjugateOperations {
        
        @Test
        void modulusAndModulusSquared() {
            Complex z = c(3.0, -4.0);
            
            assertEquals(25.0, z.modulusSquared(), TOLERANCE, "|3-4i|^2");
            assertEquals(5.0, z.modulus(), TOLERANCE, "|3-4i|");
            assertEquals(0.0, ComplexFactory.getInstance().zero().modulus(), 0);
        }
        
        @Test
        void norm() {
            Complex z = c(-5.0, 12.0); // Modulo = 13.0
            Real normResult = z.norm();
            
            assertEquals(13.0, normResult.getValue(), TOLERANCE, "Norma come oggetto Real");
        }
        
        @Test
        void conjugate() {
            assertComplexEquals(c(5.0, -3.0), c(5.0, 3.0).conjugate(), "Coniugato");
            assertComplexEquals(c(-2.0, 0.0), c(-2.0, 0.0).conjugate(), "Coniugato di Reale");
            assertComplexEquals(c(0.0, -5.0), c(0.0, 5.0).conjugate(), "Coniugato di Immaginario Puro");
        }
    }

    @Nested
    @DisplayName("Radice Quadrata (SqrtableElement)")
    class SqrtOperations {
        
        @Test
        void sqrtOfZero() {
            assertComplexEquals(ComplexFactory.getInstance().zero(), ComplexFactory.getInstance().zero().sqrt(), "Radice di Zero");
        }

        @Test
        void sqrtOfPositiveReal() {
            assertComplexEquals(c(2.0, 0.0), c(4.0, 0.0).sqrt(), "Radice di 4");
        }
        
        @Test
        void sqrtOfNegativeReal() {
            // sqrt(-4) = 2i (Il tuo algoritmo sceglie la radice con parte immaginaria positiva)
            assertComplexEquals(c(0.0, 2.0), c(-4.0, 0.0).sqrt(), "Radice di -4");
        }

        @Test
        void sqrtOfComplexNumber() {
            // sqrt(8 + 6i) = 3 + 1i
            Complex z = c(8.0, 6.0);
            Complex expected = c(3.0, 1.0);
            Complex result = z.sqrt();
            
            assertComplexEquals(expected, result, "Radice di 8 + 6i");
            // Verifica la proprietà inversa: (3+i)^2 = 9 + 6i + i^2 = 8 + 6i
            assertComplexEquals(z, result.multiply(result), "Verifica inversa");
        }
        
        @Test
        void sqrtOfComplexNumberNegativeImaginary() {
            // sqrt(8 - 6i) = 3 - 1i (L'algoritmo sceglie il segno corretto)
            Complex z = c(8.0, -6.0);
            Complex expected = c(3.0, -1.0);
            Complex result = z.sqrt();
            
            assertComplexEquals(expected, result, "Radice di 8 - 6i");
        }
    }
    
    @Nested
    @DisplayName("Potenza (ExponentiableElement)")
    class PowerOperations {
        
        @Test
        void powerPositiveExponent() {
            // (1 + i)^2 = 1 + 2i - 1 = 2i
            Complex z = c(1.0, 1.0);
            assertComplexEquals(c(0.0, 2.0), z.power(2), "(1+i)^2");
            
            // (2i)^3 = 8 * i^3 = 8 * (-i) = -8i
            Complex z_im = c(0.0, 2.0);
            assertComplexEquals(c(0.0, -8.0), z_im.power(3), "(2i)^3");
        }
        
        @Test
        void powerNegativeExponent() {
            // (2i)^-1 = 1 / 2i = -i / 2
            Complex z_im = c(0.0, 2.0);
            assertComplexEquals(c(0.0, -0.5), z_im.power(-1), "(2i)^-1");
            
            // (1 + i)^-2 = (2i)^-1 = -i / 2
            Complex z = c(1.0, 1.0);
            assertComplexEquals(c(0.0, -0.5), z.power(-2), "(1+i)^-2");
        }
        
        @Test
        void powerZero() {
            assertComplexEquals(ComplexFactory.getInstance().one(), c(10.0, -5.0).power(0), "z^0");
        }

        @Test
        void powerZeroNegativeExponentThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                ComplexFactory.getInstance().zero().power(-1), 
                "Zero elevato a potenza negativa"
            );
        }
    }
    
    @Nested
    @DisplayName("Altre Funzioni e Conversione")
    class UtilityFunctions {
        
        @Test
        void argument() {
            assertEquals(0.0, c(5.0, 0.0).argument(), TOLERANCE, "Argomento 5");
            assertEquals(Math.PI / 2.0, c(0.0, 5.0).argument(), TOLERANCE, "Argomento 5i");
            assertEquals(-Math.PI / 2.0, c(0.0, -5.0).argument(), TOLERANCE, "Argomento -5i");
            assertEquals(Math.PI, c(-5.0, 0.0).argument(), TOLERANCE, "Argomento -5");
            assertEquals(Math.PI / 4.0, c(1.0, 1.0).argument(), TOLERANCE, "Argomento 1+i");
        }

        @ParameterizedTest(name = "valueOf({0})")
        @CsvSource({"10.0", "-5.5", "0.0"})
        void valueOf(double input) {
            Complex result = ComplexFactory.getInstance().fromDouble(input);
            assertComplexEquals(c(input, 0.0), result, "valueOf deve creare un numero reale");
        }
        
        @Test
        @DisplayName("toString Formattazione")
        void stringRepresentation() {
            assertEquals("5.0", c(5.0, 0.0).toString());
            assertEquals("-5.0", c(-5.0, 0.0).toString());
            assertEquals("i", c(0.0, 1.0).toString());
            assertEquals("-i", c(0.0, -1.0).toString());
            assertEquals("5.0i", c(0.0, 5.0).toString());
            assertEquals("5.0 + 3.0i", c(5.0, 3.0).toString());
            assertEquals("5.0 - 3.0i", c(5.0, -3.0).toString());
            assertEquals("5.0 - i", c(5.0, -1.0).toString());
            assertEquals("-5.0 + i", c(-5.0, 1.0).toString());
        }
    }
}