/*
 * Rational.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra.numeric;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.CreatableFromDouble;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
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
implements FieldElement<Rational>, NormableElement<Real, Rational>, ExponentiableElement<Rational>, ComparableElement<Rational>, CreatableFromDouble<Rational>
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
    public boolean isMathematicallyEqualTo(Rational other)
    {
    	if (this == other) {
    		return true;
    	}
    	if (other == null) {
    		return false;
    	}

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
	    // Calcolo del numeratore usando addExact e multiplyExact
	    long num1 = Math.multiplyExact(this.numerator, other.denominator);
	    long num2 = Math.multiplyExact(other.numerator, this.denominator);
	    long newNumerator = Math.addExact(num1, num2);   
	    long newDenominator = Math.multiplyExact(this.denominator, other.denominator);

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
        long newNumerator = Math.multiplyExact(this.numerator, other.numerator);
        long newDenominator = Math.multiplyExact(this.denominator, other.denominator);

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


    @Override
    public final boolean equals(Object other) 
    {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Rational)) {
            return false;
        }

        Rational rational = (Rational) other;
        return this.numerator == rational.numerator && this.denominator == rational.denominator;
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


    @Override
    public Rational valueOf(double value)
    {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Cannot create a Rational number from NaN or Infinity.");
        }

        if (value == 0.0) {
            return Rational.ZERO; // 0/1
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
        
        // 5. Il costruttore Rational gestisce la semplificazione finale (GCD)
        // e la normalizzazione del segno, grazie alla tua implementazione esistente.
        return new Rational(num, den);
    }
}
