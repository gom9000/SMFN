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
    public Rational fromDouble(double value) {
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
            // Il denominatore implicito è 1 (mantissa è già un intero)
            // num = mantissa * 2^exponent
            num = mantissa * MathUtils.power(2L, exponent);
            den = 1L;
        } else {
            // L'esponente negativo indica un denominatore potenza di 2
            // num = mantissa
            // den = 2^(-exponent)
            num = mantissa;
            den = MathUtils.power(2L, -exponent);
        }

        // 4. Applica il segno
        if (negative) {
            num = -num;
        }

        return new Rational(num, den);
    }

    @Override // NumericFactory impls
    public Rational fromLong(long value) {
        return new Rational(value);
    }

    @Override // NumericFactory impls
    public Rational fromInt(int value) {
        return new Rational(value);
    }
}