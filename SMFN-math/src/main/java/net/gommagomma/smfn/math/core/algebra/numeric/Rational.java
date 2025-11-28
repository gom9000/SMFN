/*
 * Rational.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.core.algebra.numeric;


import net.gommagomma.smfn.math.core.algebra.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.core.algebra.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.core.algebra.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.utils.MathUtils;


/**
 * Represents an immutable rational number (fraction)
 * consisting of a numerator and a denominator.
 * Rational numbers are always stored in their simplest
 * form (reduced terms) with a positive denominator.
 * 
 * @author gommagomma.net
 */
public final class Rational
implements FieldElement<Rational>, NormableElement<Real, Rational>, ExponentiableElement<Rational>, ComparableElement<Rational>
{
	public static final Rational ZERO = new Rational(0, 1);
    public static final Rational ONE = new Rational(1, 1);

	private final long numerator;
    private final long denominator;


    /**
     * Constructs a Rational number, normalizing it to the lowest terms and ensuring
     * the denominator is positive.
     *
     * @param numerator   The numerator of the rational number.
     * @param denominator The denominator of the rational number.
     * @throws IllegalArgumentException if the denominator is zero.
     */
    public Rational(long numerator, long denominator)
    {
    	if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }

    	// denominator is always positive, the sign is moved to the numerator
        if (denominator < 0)
        {
            numerator = -numerator;
            denominator = -denominator;
        }

        long gcd = MathUtils.greatestCommonDivisor(numerator, denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }


    /**
     * Constructs a Rational number from a single long integer value.
     * This is equivalent to a fraction with the given value as the numerator
     * and 1 as the denominator (e.g., 5 is represented as 5/1).
     *
     * @param value The integer value, representing the numerator of the rational number.
     */
    public Rational(long value)
    {
        this(value, 1);
    }


    /**
     * Returns the numerator of this rational number in its simplest form.
     *
     * @return The numerator.
     */
    public long getNumerator()
    {
        return numerator;
    }


    /**
     * Returns the denominator of this rational number in its simplest form (always positive).
     *
     * @return The denominator.
     */
    public long getDenominator()
    {
        return denominator;
    }


    // AlgebraicElement impls

    /**
     * Checks if this rational number is mathematically equal to another rational number.
     * This checks the normalized values (e.g., 1/2 is equal to 2/4).
     *
     * @param other The other rational number to compare against.
     * @return {@code true} if the values are equal, {@code false} otherwise.
     */
    @Override
    public boolean isEqual(Rational other)
    {
        return this.numerator == other.numerator && this.denominator == other.denominator;
    }

    /**
     * Creates a copy of this rational number.
     *
     * @return A new identical Rational instance.
     */
    @Override
    public Rational copy()
    {
        return new Rational(this.numerator, this.denominator);
    }


	@Override
	public Rational getZero()
	{
		return Rational.ZERO;
	}


	@Override
	public Rational getOne()
	{
		return Rational.ONE;
	}


    // MonoidElement impls

    /**
     * Adds another rational number to this rational number.
     * The result is a new, normalized Rational instance.
     *
     * @param other The rational number to add.
     * @return The sum of the two rational numbers.
     */
    @Override
    public Rational add(Rational other)
    {
        long newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
        long newDenominator = this.denominator * other.denominator;

        return new Rational(newNumerator, newDenominator);
    }


    // GroupElement impls

    /**
     * Returns the additive inverse (negation) of this rational number.
     *
     * @return The negated rational number.
     */
    @Override
    public Rational negate()
    {
        return new Rational(-this.numerator, this.denominator);
    }


    // MultiplicativeMonoidElement impls

    /**
     * Multiplies this rational number by another rational number.
     * The result is a new, normalized Rational instance.
     *
     * @param other The rational number to multiply by.
     * @return The product of the two rational numbers.
     */
    @Override
    public Rational multiply(Rational other)
    {
        long newNumerator = this.numerator * other.numerator;
        long newDenominator = this.denominator * other.denominator;

        return new Rational(newNumerator, newDenominator);
    }


    // FieldElement impls

    /**
     * Returns the multiplicative inverse (reciprocal) of this rational number.
     *
     * @return The reciprocal rational number.
     * @throws ArithmeticException if the rational number is zero.
     */
    @Override
    public Rational inverse()
    {
        if (this.isZero()) {
            throw new ArithmeticException("Cannot take the inverse of zero in a Field.");
        }

        return new Rational(this.denominator, this.numerator);
    }


    @Override
    public Rational power(int exponent)
    {
        if (this.isZero() && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }

        if (exponent == 0) {
            return Rational.ONE;
        }

        Rational base = this;
        int exp = Math.abs(exponent);
        Rational result = Rational.ONE;

        while (exp > 0)
        {
            if (exp % 2 == 1) {
                result = result.multiply(base);
            }
            base = base.multiply(base);
            exp /= 2;
        }

        return exponent < 0 ? result.inverse() : result;
    }


    public Rational sqrt()
    throws ArithmeticException
    {
        // Funzione helper per verificare se un long è un quadrato perfetto
        long num = this.numerator;
        long den = this.denominator;

        if (num < 0) {
            throw new ArithmeticException("Cannot take the square root of a negative rational number within the domain of rationals.");
        }
        
        long sqrtNum = (long) Math.sqrt(num);
        long sqrtDen = (long) Math.sqrt(den);

        if (sqrtNum * sqrtNum == num && sqrtDen * sqrtDen == den) {
            return new Rational(sqrtNum, sqrtDen);
        } else {
            throw new ArithmeticException("The square root of " + this + " is irrational and cannot be represented as a Rational.");
        }
    }


    @Override
    public int compareTo(Rational other)
    {
        long ad = this.numerator * other.denominator;
        long bc = other.numerator * this.denominator;
        return Long.compare(ad, bc);
    }


    // Java Standard impls

    /**
     * Returns a string representation of the rational number (e.g., "1/2" or "5").
     *
     * @return A string representing the rational number.
     */
    @Override
    public String toString()
    {
        if (denominator == 1) {
        	return String.valueOf(numerator);
        }

        return numerator + "/" + denominator;
    }


    /**
     * Indicates whether some other object is "equal to" this rational number.
     * It relies on {@link #isEqual(Rational)} for mathematical correctness.
     *
     * @param other The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object other) 
    {
        return (other instanceof Rational) && isEqual((Rational)other);
    }


    /**
     * Returns a hash code value for the object,
     * consistent with the {@link #equals(Object)} method.
     *
     * @return A hash code value for this object.
     */
    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(numerator, denominator);
    }


    @Override
    public Real norm()
    {
        return new Real(modulus());
    }


    @Override
    public double modulus()
    {
    	double absValue = (double) numerator / denominator;
        if (absValue < 0) { absValue = -absValue; }

        return absValue;
    }
}
