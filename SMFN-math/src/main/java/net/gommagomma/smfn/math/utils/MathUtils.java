/*
 * MathUtils.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.utils;


/**
 * Utility class for common mathematical operations.
 * 
 * @author gommagomma.net
 */
public final class MathUtils
{
    private MathUtils()
    {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }


    /**
     * Calcola base^exponent per i long.
     * Utilizza l'algoritmo di potenza binaria veloce.
     * @param base Il numero base.
     * @param exponent L'esponente (deve essere non negativo in questo contesto).
     * @return base^exponent.
     * @throws ArithmeticException se si verifica un overflow o l'esponente è negativo.
     */
    public static long power(long base, int exponent)
    {
        if (exponent < 0) {
            throw new ArithmeticException("Negative exponent not supported for integer power.");
        }
        if (exponent == 0) {
            return 1L;
        }
        if (base == 0) {
            return 0L;
        }

        long result = 1L;
        long b = base;
        int exp = exponent;

        while (exp > 0) {
            if ((exp & 1) == 1) { // exp è dispari
                result = Math.multiplyExact(result, b);
            }
            b = Math.multiplyExact(b, b);
            exp >>= 1; // exp = exp / 2
        }
        return result;
    }


    /**
     * Calculates the Greatest Common Divisor (GCD) of two long integers using the Euclidean algorithm.
     * The result is always positive.
     *
     * @param a The first number.
     * @param b The second number.
     * @return The greatest common divisor of a and b.
     */
    public static long greatestCommonDivisor(long a, long b)
    {
        a = Math.abs(a);
        b = Math.abs(b);

        while (b != 0L)
        {
            long temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }
    
    // leastCommonMultiple(LCM)...
}