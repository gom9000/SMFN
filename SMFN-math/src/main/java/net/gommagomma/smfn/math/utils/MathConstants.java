/*
 * MathConstants.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.utils;


/**
 * A utility class containing common mathematical constants
 * and library-specific configuration values.
 * This class cannot be instantiated.
 * 
 * @author gommagomma.net
 */
public final class MathConstants
{
	private MathConstants()
	{
		throw new UnsupportedOperationException("Constants class cannot be instantiated");
	}


	/**
     * The default tolerance (epsilon) used throughout
     * the library for floating-point comparisons.
     * Two floating-point numbers {@code a} and {@code b} are considered equal if 
     * {@code Math.abs(a - b) < EPSILON}.
     */
	public static final double EPSILON = 1E-12;

	/**
	 * The ratio of the circumference of a circle to its diameter (Pi).
	 * Equivalent to {@link java.lang.Math#PI}.
	 */
	public static final double PI = Math.PI;

	/**
	 * The base of the natural logarithms (e).
	 * Equivalent to {@link java.lang.Math#E}.
	 */
	public static final double E = Math.E;
}
