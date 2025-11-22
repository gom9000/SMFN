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