package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.*;

import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.utils.MathConstants;
import org.junit.jupiter.api.Test;


public class RationalTest {

    private final RationalField Q = RationalField.INSTANCE;

    private final Rational r_3_4 = new Rational(3, 4);
    private final Rational r_neg_1_2 = new Rational(-1, 2);
    private final Rational r_5 = new Rational(5, 1);

    @Test
    void constructorNormalizesAndReducesToLowestTerms() {
        Rational r = new Rational(6, 8);
        assertEquals(3, r.getNumerator());
        assertEquals(4, r.getDenominator());

        // Il segno si sposta sempre al numeratore
        Rational r2 = new Rational(3, -4);
        assertEquals(-3, r2.getNumerator());
        assertEquals(4, r2.getDenominator());
    }

    @Test
    void constructorRejectsZeroDenominator() {
        assertThrows(IllegalArgumentException.class, () -> new Rational(1, 0));
    }

    @Test
    void copy() {
        Rational copy = r_3_4.copy();
        assertEquals(r_3_4, copy);
        assertNotSame(r_3_4, copy);
    }

    @Test
    void orderable() {
        assertTrue(r_3_4.compareTo(r_neg_1_2) > 0);
        assertTrue(r_5.compareTo(r_3_4) > 0);
        assertTrue(r_neg_1_2.compareTo(r_3_4) < 0);
        assertEquals(0, r_3_4.compareTo(new Rational(6, 8)));
        assertTrue(r_neg_1_2.isLessThan(r_3_4));
    }

    @Test
    void absolutable() {
        assertEquals(new Rational(3, 4), r_3_4.abs());
        assertEquals(new Rational(1, 2), r_neg_1_2.abs());
        assertEquals(1, r_3_4.signum());
        assertEquals(-1, r_neg_1_2.signum());
        assertEquals(0, Q.zero().signum());
    }

    @Test
    void exponentiablePositiveAndNegative() {
        assertEquals(new Rational(9, 16), r_3_4.power(2));
        assertEquals(new Rational(16, 9), r_3_4.power(-2)); // (3/4)^-2 = (4/3)^2
        assertEquals(Q.one(), r_3_4.power(0));
        assertThrows(ArithmeticException.class, () -> Q.zero().power(-1));
    }

    @Test
    void normableReturnsRealModulus() {
        assertEquals(0.75, r_3_4.norm().getValue(), MathConstants.EPSILON);
        assertEquals(0.5, r_neg_1_2.norm().getValue(), MathConstants.EPSILON);
        assertEquals(5.0, r_5.norm().getValue(), MathConstants.EPSILON);
    }

    @Test
    void toStringFormat() {
        assertEquals("3/4", r_3_4.toString());
        assertEquals("-1/2", r_neg_1_2.toString());
        assertEquals("5", r_5.toString()); // Denominatore 1: niente frazione
        assertEquals("-1", new Rational(-1, 1).toString());
    }

    // --- Comportamenti di struttura non gia' coperti da RationalFieldTest (i cui assiomi
    // sono verificati genericamente): qui solo la scia di overflow, specifica a questa impl. ---

    @Test
    void structureAdditionOverflow() {
        Rational maxRational = new Rational(Long.MAX_VALUE, 1);
        Rational oneHalf = new Rational(1, 2);
        assertThrows(ArithmeticException.class, () -> Q.add(maxRational, oneHalf));
    }

    @Test
    void structureMultiplicationOverflow() {
        long halfMax = Long.MAX_VALUE / 2 + 1;
        Rational largeRational = new Rational(halfMax, 1);
        assertThrows(ArithmeticException.class, () -> Q.multiply(largeRational, new Rational(2, 1)));
    }

    // --- Casi limite legati a Long.MIN_VALUE / Integer.MIN_VALUE ---

    @Test
    void constructorRejectsLongMinValueDenominator() {
        // new Rational(1, Long.MIN_VALUE): normalizzare il segno del denominatore
        // richiederebbe -Long.MIN_VALUE, che va in overflow. Deve lanciare, non
        // restituire un razionale con denominatore ancora negativo.
        assertThrows(ArithmeticException.class, () -> new Rational(1, Long.MIN_VALUE));
    }

    @Test
    void constructorRejectsLongMinValueNumeratorWithNegativeDenominator() {
        // new Rational(Long.MIN_VALUE, -1): normalizzare richiederebbe -Long.MIN_VALUE
        // sul numeratore, overflow silenzioso che lascerebbe il segno sbagliato.
        assertThrows(ArithmeticException.class, () -> new Rational(Long.MIN_VALUE, -1));
    }

    @Test
    void constructorAcceptsLongMinValueNumeratorWithPositiveDenominator() {
        // Long.MIN_VALUE / 1 non richiede alcuna negazione (il denominatore e' gia'
        // positivo) ed e' perfettamente rappresentabile: numerator = Long.MIN_VALUE,
        // denominator = 1, senza bisogno di ridurre nulla oltre al gcd banale con 1.
        Rational r = new Rational(Long.MIN_VALUE, 1);
        assertEquals(Long.MIN_VALUE, r.getNumerator());
        assertEquals(1, r.getDenominator());
    }

    @Test
    void absStillRejectsLongMinValueNumerator() {
        // abs() non puo' negare Long.MIN_VALUE (overflow genuino, indipendente
        // dal denominatore): a differenza del costruttore, qui il rifiuto resta
        // necessario, non un limite evitabile.
        Rational r = new Rational(Long.MIN_VALUE, 1);
        assertThrows(ArithmeticException.class, r::abs);
    }

    @Test
    void powerHandlesIntegerMinValueExponent() {
        // Base vicina a 1: il vero risultato resta finito, non sfonda il range di un double.
        // Verifica che l'esponente venga trattato come |Integer.MIN_VALUE| senza
        // overflow (che lo lascerebbe negativo e farebbe rientrare erroneamente nel
        // ramo "esponente zero", restituendo sempre 1).
        Rational base = new Rational(1); // 1^n = 1 per ogni n, incluso Integer.MIN_VALUE
        assertEquals(Q.one(), base.power(Integer.MIN_VALUE));

        // Con una base diversa da 1, l'esponente e' cosi' grande che il numeratore/denominatore
        // esatto risultante non sta in un long: deve lanciare, non restituire un
        // valore silenziosamente sbagliato (es. 1, come accadrebbe col vecchio bug).
        Rational two = new Rational(2, 1);
        assertThrows(ArithmeticException.class, () -> two.power(Integer.MIN_VALUE));
    }

    @Test
    void compareToHandlesLargeCrossProductsWithoutOverflow() {
        // Prodotti incrociati (4 miliardi * 4 miliardi) superano abbondantemente
        // Long.MAX_VALUE, ma i due razionali restano perfettamente confrontabili:
        // il confronto deve restituire l'ordinamento corretto, non lanciare.
        Rational big = new Rational(4_000_000_000L, 1);
        Rational tiny = new Rational(1, 4_000_000_000L);

        assertTrue(big.compareTo(tiny) > 0);
        assertTrue(tiny.compareTo(big) < 0);
        assertFalse(big.isLessThan(tiny));
        assertTrue(tiny.isLessThan(big));
    }

    @Test
    void compareToHandlesCrossProductOverflowNearLongRange() {
        // Stesso principio del bug segnalato: N/(N-1) e (N-1)/(N-2), con
        // N = Long.MAX_VALUE, hanno prodotti incrociati che superano Long.MAX_VALUE
        // (verificato indipendentemente con BigInteger: N*(N-2) < (N-1)^2), quindi
        // x < y -- il confronto deve restituirlo, non lanciare ArithmeticException.
        long n = Long.MAX_VALUE;
        Rational x = new Rational(n, n - 1);
        Rational y = new Rational(n - 1, n - 2);

        assertTrue(x.compareTo(y) < 0);
        assertTrue(x.isLessThan(y));
    }
}
