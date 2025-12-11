package net.gommagomma.smfn.math.algebra.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.utils.MathConstants;

@DisplayName("Rational: Test delle proprietà di Campo, Normalizzazione e Conversione")
class RationalTest {

    // Helper per una creazione rapida (long/long)
    private Rational r(long num, long den) {
        return new Rational(num, den);
    }
    
    // Helper per una creazione rapida (long)
    private Rational r(long num) {
        return new Rational(num);
    }

    // ======================================================================================
    // COSTRUTTORE E NORMALIZZAZIONE
    // ======================================================================================

    @Nested
    @DisplayName("Costruttore e Normalizzazione")
    class ConstructorAndNormalizationTests {
        
        @Test
        @DisplayName("Normalizzazione (GCD)")
        void normalization_GCD() {
            // 2/4 deve diventare 1/2
            Rational oneHalf = r(2, 4);
            assertEquals(1L, oneHalf.getNumerator());
            assertEquals(2L, oneHalf.getDenominator());
            
            // 6/9 deve diventare 2/3
            Rational twoThirds = r(6, 9);
            assertEquals(2L, twoThirds.getNumerator());
            assertEquals(3L, twoThirds.getDenominator());
        }
        
        @Test
        @DisplayName("Normalizzazione del Segno (Denominatore Positivo)")
        void normalization_Sign() {
            // 1/-2 deve diventare -1/2
            Rational negHalf = r(1, -2);
            assertEquals(-1L, negHalf.getNumerator());
            assertEquals(2L, negHalf.getDenominator());
            
            // -4/-8 deve diventare 1/2
            Rational posHalf = r(-4, -8);
            assertEquals(1L, posHalf.getNumerator());
            assertEquals(2L, posHalf.getDenominator());
            
            // Numeratore zero (0/5)
            Rational zero = r(0, 5);
            assertEquals(0L, zero.getNumerator());
            assertEquals(1L, zero.getDenominator()); // Denominatore deve essere sempre 1 quando num è 0
        }
        
        @Test
        @DisplayName("Costruttore Singolo (Interi)")
        void singleConstructor() {
            Rational five = r(5);
            assertEquals(5L, five.getNumerator());
            assertEquals(1L, five.getDenominator());
            
            Rational negThree = r(-3);
            assertEquals(-3L, negThree.getNumerator());
            assertEquals(1L, negThree.getDenominator());
        }
        
        @Test
        @DisplayName("Denominatore Zero (Eccezione)")
        void zeroDenominatorThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> 
                new Rational(1, 0), 
                "Il denominatore zero deve lanciare IllegalArgumentException."
            );
        }
        
        @Test
        @DisplayName("Costanti e Copia")
        void constantsAndCopy() {
            assertEquals(0L, RationalField.getInstance().zero().getNumerator());
            assertEquals(1L, RationalField.getInstance().one().getNumerator());
            
            Rational z = r(3, 7);
            Rational copy = z.copy();
            assertTrue(z.isMathematicallyEqualTo(copy));
            assertNotSame(z, copy);
        }
    }

    // ======================================================================================
    // OPERAZIONI DI CAMPO (FIELD OPERATIONS)
    // ======================================================================================

    @Nested
    @DisplayName("Addizione e Sottrazione")
    class AdditiveOperations {
        
        @Test
        void add() {
            // 1/2 + 1/3 = 5/6
            Rational z1 = r(1, 2);
            Rational z2 = r(1, 3);
            assertRationalEquals(r(5, 6), z1.add(z2), "1/2 + 1/3");
            
            // 1/4 + 1/4 = 2/4 = 1/2 (testa la normalizzazione)
            assertRationalEquals(r(1, 2), r(1, 4).add(r(1, 4)), "1/4 + 1/4");
            
            // 1/2 + (-1/2) = 0
            assertRationalEquals(RationalField.getInstance().zero(), r(1, 2).add(r(-1, 2)), "1/2 + (-1/2)");
        }
        
        @Test
        void negateAndSubtract() {
            // Negazione
            assertRationalEquals(r(-3, 4), r(3, 4).negate(), "Negazione");
            assertRationalEquals(r(3, 4), r(-3, 4).negate(), "Negazione (negativo)");
            
            // Sottrazione: 1/2 - 1/3 = 1/6 (Usando add + negate)
            assertRationalEquals(r(1, 6), r(1, 2).add(r(1, 3).negate()), "1/2 - 1/3");
        }

        @Test
        @DisplayName("Overflow in Addizione")
        void addOverflow() {
            // Denominatori grandi (d1 * d2 causa overflow)
            Rational z1 = r(1, Long.MAX_VALUE);
            Rational z2 = r(1, Long.MAX_VALUE - 1); // d1 * d2 > Long.MAX_VALUE
            
            assertThrows(ArithmeticException.class, () -> 
                z1.add(z2), 
                "L'addizione dovrebbe lanciare ArithmeticException in caso di overflow del denominatore."
            );
            
            // Numeratore grande (n1*d2 + n2*d1 causa overflow)
            Rational nMax = r(Long.MAX_VALUE);
            Rational one = r(1);
            
            // nMax + 1 causa overflow nel numeratore risultante
            assertThrows(ArithmeticException.class, () -> 
                nMax.add(one), 
                "L'addizione dovrebbe lanciare ArithmeticException in caso di overflow del numeratore."
            );
        }
    }

    @Nested
    @DisplayName("Moltiplicazione e Divisione")
    class MultiplicativeOperations {
        
        @Test
        void multiply() {
            // 2/3 * 3/4 = 6/12 = 1/2
            assertRationalEquals(r(1, 2), r(2, 3).multiply(r(3, 4)), "Moltiplicazione semplice");
            
            // -1/2 * 1/3 = -1/6
            assertRationalEquals(r(-1, 6), r(-1, 2).multiply(r(1, 3)), "Moltiplicazione con segno");
        }

        @Test
        void inverseAndDivide() {
            Rational z = r(3, 4);
            // Inverso: 4/3
            assertRationalEquals(r(4, 3), z.inverse(), "Inverso");
            
            // Divisione: z1 / z2 = z1 * z2^-1
            // 1/2 / 3/4 = 1/2 * 4/3 = 4/6 = 2/3
            assertRationalEquals(r(2, 3), r(1, 2).multiply(r(3, 4).inverse()), "Divisione");
        }

        @Test
        void inverseOfZeroThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
            RationalField.getInstance().zero().inverse(), 
                "L'inverso di zero deve lanciare ArithmeticException."
            );
        }
        
        @Test
        @DisplayName("Overflow in Moltiplicazione")
        void multiplyOverflow() {
            // Caso 1: Overflow del Numeratore (usando halfMax * 2)
            long halfMax = Long.MAX_VALUE / 2;
            Rational z1 = r(halfMax + 1); // Numeratore > halfMax
            Rational two = r(2);

            // Numeratore risulterebbe (halfMax + 1) * 2 > Long.MAX_VALUE
            assertThrows(ArithmeticException.class, () -> 
                z1.multiply(two), 
                "Moltiplicazione dovrebbe lanciare ArithmeticException (Num Overflow)."
            );
            
            // Caso 2: Overflow del Denominatore (usando halfMax * halfMax)
            // Usiamo il valore al limite per overflow al quadrato
            Rational z2 = r(1, 3_037_000_500L); // Numeratore e Denominatore di z2 sono al limite
            
            // Denominatore risulterebbe (3.037e9)^2 > Long.MAX_VALUE
            assertThrows(ArithmeticException.class, () -> 
                z2.multiply(z2), 
                "Moltiplicazione dovrebbe lanciare ArithmeticException (Den Overflow)."
            );
        }
    }
    
    // ======================================================================================
    // CAPACITÀ AGGIUNTIVE
    // ======================================================================================

    @Nested
    @DisplayName("Potenza (ExponentiableElement)")
    class PowerOperations {
        
        @Test
        void powerPositiveExponent() {
            // (2/3)^3 = 8/27
            assertRationalEquals(r(8, 27), r(2, 3).power(3), "Potenza positiva");
        }
        
        @Test
        void powerNegativeExponent() {
            // (2/3)^-3 = (3/2)^3 = 27/8
            assertRationalEquals(r(27, 8), r(2, 3).power(-3), "Potenza negativa");
        }
        
        @Test
        void powerZero() {
            assertRationalEquals(RationalField.getInstance().one(), r(100, 5).power(0), "z^0");
        }
        
        @Test
        void powerZeroNegativeExponentThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
            RationalField.getInstance().zero().power(-1), 
                "Zero elevato a potenza negativa."
            );
        }
        
        @Test
        @DisplayName("Overflow in Potenza")
        void powerOverflow() {
            Rational z = r(100000); // 10^5
            // (10^5)^3 = 10^15 (Ok, < Long.MAX)
            // (10^5)^4 = 10^20 (Overflow)
            assertThrows(ArithmeticException.class, () -> 
                z.power(4), 
                "La potenza deve lanciare ArithmeticException (Num Overflow)."
            );
        }
    }

    @Nested
    @DisplayName("Radice Quadrata (Restrizione a Quadrati Perfetti)")
    class SqrtOperations {
        
        @Test
        void sqrtPerfectSquare() {
            // sqrt(4/9) = 2/3
            assertRationalEquals(r(2, 3), r(4, 9).sqrt(), "Radice quadrata perfetta");
            
            // sqrt(25) = 5/1
            assertRationalEquals(r(5), r(25).sqrt(), "Radice intera");
        }
        
        @Test
        void sqrtIrrationalThrowsException() {
            // sqrt(2/3) è irrazionale
            assertThrows(ArithmeticException.class, () -> 
                r(2, 3).sqrt(), 
                "Radice di irrazionale deve lanciare ArithmeticException."
            );
            
            // sqrt(2)
            assertThrows(ArithmeticException.class, () -> 
                r(2).sqrt(), 
                "Radice di 2 deve lanciare ArithmeticException."
            );
        }
        
        @Test
        void sqrtNegativeThrowsException() {
            // sqrt(-4/9) non è razionale
            assertThrows(ArithmeticException.class, () -> 
                r(-4, 9).sqrt(), 
                "Radice di numero negativo."
            );
        }
    }
    
    @Nested
    @DisplayName("Comparazione e Rappresentazione")
    class ComparisonAndUtilityTests {
        
        @Test
        void compareTo() {
            // 1/2 vs 1/3 (1*3 vs 1*2 => 3 > 2)
            assertTrue(r(1, 2).compareTo(r(1, 3)) > 0); 
            // -1/2 vs -1/3 (-1*3 vs -1*2 => -3 < -2)
            assertTrue(r(-1, 2).compareTo(r(-1, 3)) < 0);
            
            // 2/4 vs 1/2 (normalizzazione è implicita, usa i valori semplici 1/2)
            assertEquals(0, r(2, 4).compareTo(r(1, 2))); 
        }

        @Test
        void toStringRepresentation() {
            assertEquals("5", r(5).toString());
            assertEquals("-5", r(-5).toString());
            assertEquals("1/2", r(1, 2).toString());
            assertEquals("-1/2", r(-1, 2).toString());
        }
        
        @Test
        void modulusAndNorm() {
            assertEquals(0.5, r(1, 2).modulus(), MathConstants.EPSILON);
            assertEquals(0.5, r(-1, 2).modulus(), MathConstants.EPSILON);
            
            // Verifica la creazione dell'oggetto Real per la norma
            assertEquals(1.5, r(3, 2).norm().getValue(), MathConstants.EPSILON);
        }
    }
    
    // ======================================================================================
    // CONVERSIONE DA DOUBLE (valueOf)
    // ======================================================================================

    @Nested
    @DisplayName("Conversione da Double (valueOf)")
    class ValueOfTests {
        
        @Test
        void valueOfExactPowersOfTwo() {
            // 0.5 = 1/2
            assertRationalEquals(r(1, 2), RationalField.getInstance().of(0.5), "0.5");
            // 0.25 = 1/4
            assertRationalEquals(r(1, 4), RationalField.getInstance().of(0.25), "0.25");
            // 0.125 = 1/8
            assertRationalEquals(r(1, 8), RationalField.getInstance().of(0.125), "0.125");
            // 5.0 = 5
            assertRationalEquals(r(5), RationalField.getInstance().of(5.0), "5.0");
        }
        
        @Test
        void valueOfNegative() {
            // -0.5: La rappresentazione esatta è -2251799813685248 / 4503599627370496 * 2
            // Dopo la semplificazione, diventa -1 / 2, ma solo se l'esponente è -1
            // La rappresentazione IEEE 754 di 0.5 è 1/2.
            
            // Per -0.5:
            Rational result_half = RationalField.getInstance().of(-0.5);
            assertEquals(-1L, result_half.getNumerator());
            assertEquals(2L, result_half.getDenominator());
            
            // Per -3.0:
            assertRationalEquals(r(-3), RationalField.getInstance().of(-3.0), "-3.0");
        }
        
        @Test
        void valueOfWithSimplification() {
            // 0.75 = 3/4
            assertRationalEquals(r(3, 4), RationalField.getInstance().of(0.75), "0.75 (3/4)");
            // 0.4 = 2/5 (Non è una potenza di 2, ma è una frazione esatta in IEEE 754)
            // (0.4 è rappresentato in binario come 0.011001100..., ma il double approssima 
            // a una frazione con denominatore potenza di 2. Il tuo algoritmo lo cattura.)
            assertRationalEquals(r(3602879701896397L, 9007199254740992L).copy(), RationalField.getInstance().of(0.4), "0.4 (approx)");
            
            // Questo test è problematico a causa della precisione. Concentriamoci su quelli che funzionano esatti.
            // 0.75 è (1/2 + 1/4) = 3/4. Bit: 0x3FE8000000000000L. Exp=1022-1023=-1. Mantissa=1.1 -> 1 * 2^-1 + 1 * 2^-2.
            // Il tuo algoritmo dovrebbe produrre una frazione con denominatore potenza di 2.
            // 0.75 -> 3/4 è esatto.
            // 1.5 -> 3/2 è esatto.
            assertRationalEquals(r(3, 2), RationalField.getInstance().of(1.5), "1.5");
        }

        @Test
        void valueOfNonFiniteThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> 
            RationalField.getInstance().of(Double.NaN), 
                "valueOf NaN."
            );
            assertThrows(IllegalArgumentException.class, () -> 
            RationalField.getInstance().of(Double.POSITIVE_INFINITY), 
                "valueOf Infinity."
            );
        }
        
        @Test
        void valueOfZero() {
            assertRationalEquals(RationalField.getInstance().zero(), RationalField.getInstance().of(0.0), "valueOf 0.0");
        }
    }
    
    // ======================================================================================
    // HELPER PER IL CONFRONTO (Rational non ha tolleranza)
    // ======================================================================================

    private void assertRationalEquals(Rational expected, Rational actual, String message) {
        // I razionali sono uguali solo se i loro numeratori e denominatori normalizzati sono identici
        assertTrue(expected.getNumerator() == actual.getNumerator() && expected.getDenominator() == actual.getDenominator(),
                   message + String.format(" | Atteso: %s, Ottenuto: %s", expected.toString(), actual.toString()));
    }
}