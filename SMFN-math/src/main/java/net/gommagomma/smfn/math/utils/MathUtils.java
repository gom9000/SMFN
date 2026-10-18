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

import java.math.BigInteger;

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
     * Calculates the Greatest Common Divisor (GCD) of two long integers using the Euclidean algorithm.
     * The result is always positive.
     *
     * @param a The first number.
     * @param b The second number.
     * @return The greatest common divisor of a and b.
     */
    public static long greatestCommonDivisor(long a, long b)
    {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValueExact();
    }

    // leastCommonMultiple(LCM)...

    /**
     * Confronto approssimato fra due double con tolleranza ibrida assoluta+relativa:
     * {@code |a - b| < epsilon * max(1.0, |a|, |b|)}.
     *
     * @param a il primo valore
     * @param b il secondo valore
     * @param epsilon la tolleranza di base
     * @return true se a e b sono approssimativamente uguali secondo il criterio ibrido
     */
    public static boolean nearlyEqual(double a, double b, double epsilon)
    {
        if (a == b) return true;
        double scale = Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
        return Math.abs(a - b) < epsilon * scale;
    }
}