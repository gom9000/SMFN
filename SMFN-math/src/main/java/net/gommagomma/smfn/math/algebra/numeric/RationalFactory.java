package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.utils.MathUtils;

public class RationalFactory
implements NumericFactory<Rational>
{
    private static final RationalFactory INSTANCE = new RationalFactory();
    private static final Rational ZERO = new Rational(0, 1);
    private static final Rational ONE = new Rational(1, 1);

    private RationalFactory() {}
    public static RationalFactory getInstance() { return INSTANCE; }


    @Override // NumericFactory impls
    public Rational zero() { return ZERO; }

    @Override // NumericFactory impls
    public Rational one() { return ONE; }

    @Override // NumericFactory impls
    public Rational of(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Cannot create a Rational number from NaN or Infinity.");
        }

        if (value == 0.0) {
            return zero();
        }
        
        // --- Standard IEEE 754 (64-bit double) ---
        long bits = Double.doubleToLongBits(value);
        
        // Estrazione di esponente (11 bit) e mantissa (52 bit)
        int exponent = (int) ((bits >> 52) & 0x7FFL);
        long mantissa = bits & 0x000FFFFFFFFFFFFL;
        
        // Bit di segno: 0 se positivo, diverso da 0 se negativo
        boolean negative = (bits & 0x8000000000000000L) != 0;

        // Se l'esponente è 0, è un valore denormalizzato. Altrimenti è normalizzato.
        if (exponent == 0) { 
            // Denormalizzato: l'esponente è -1022, il bit implicito non è aggiunto
            exponent = -1022; 
        } else {
            // Normalizzato: aggiunge il bit implicito (il 53° bit della mantissa)
            mantissa |= 0x0010000000000000L; 
            // Il bias dell'esponente è 1023
            exponent -= 1023;
        }

        // Il valore è: mantissa * 2^exponent
        
        long num;
        long den;

        if (exponent >= 0) {
            // Numeri interi o grandi. Qui potresti ancora rischiare overflow.
            // L'unica soluzione sicura qui è usare BigInteger, ma assumiamo che non lo faremo.
            // Per ora, continuiamo a usare power() e accettiamo il rischio per i numeri grandi.
            num = mantissa * MathUtils.power(2L, exponent);
            den = 1L;
        } else {
            // Potenza di due nel denominatore
            long powerOfTwo = 52 - exponent; // Es: 0.5 -> 52 - (-1) = 53
            
            // Controlliamo se la potenza eccede il limite di 62 per un long
            if (powerOfTwo > 62) {
                 throw new ArithmeticException("Exponent magnitude is too large to represent the Rational denominator (overflow).");
            }

            // Calcoliamo il denominatore usando lo shift bit, evitando MathUtils.power
            // (1L << N è 2^N)
            den = 1L << powerOfTwo; 
            num = mantissa;
        }

        // 4. Applica il segno
        if (negative) {
            num = -num;
        }

        return new Rational(num, den);
    }

    @Override // NumericFactory impls
    public Rational of(long value) {
        return new Rational(value);
    }

    @Override // NumericFactory impls
    public Rational of(int value) {
        return new Rational(value);
    }
}