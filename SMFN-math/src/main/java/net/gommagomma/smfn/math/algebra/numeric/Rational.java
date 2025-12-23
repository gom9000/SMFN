package net.gommagomma.smfn.math.algebra.numeric;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.utils.MathUtils;


public final class Rational
implements FieldElement<Rational>, Exponentiable<Rational>, Absolutable<Rational>, Normable<Real, Rational>
{
	public static final Rational ZERO = new Rational(0, 1);
    public static final Rational ONE = new Rational(1, 1);

	private final long numerator;
    private final long denominator;


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


    public Rational(long value)
    {
        this(value, 1);
    }


    public long getNumerator()
    {
        return numerator;
    }

    public long getDenominator()
    {
        return denominator;
    }


    public Rational sqrt()
    throws ArithmeticException
    {
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


    @Override // AlgebraicElement impls
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

    @Override // AlgebraicElement impls
    public Rational copy()
    {
        return new Rational(this.numerator, this.denominator);
    }


	@Override // MonoidElement impls
	public Rational add(Rational other)
	{
	    // Calcolo del numeratore usando addExact e multiplyExact
	    long num1 = Math.multiplyExact(this.numerator, other.denominator);
	    long num2 = Math.multiplyExact(other.numerator, this.denominator);
	    long newNumerator = Math.addExact(num1, num2);   
	    long newDenominator = Math.multiplyExact(this.denominator, other.denominator);

	    return new Rational(newNumerator, newDenominator);
	}

	@Override // MonoidElement impls
	public Rational getZero()
	{
		return ZERO;
	}


    @Override // MultiplicativeMonoidElement impls
    public Rational multiply(Rational other)
    {
        long newNumerator = Math.multiplyExact(this.numerator, other.numerator);
        long newDenominator = Math.multiplyExact(this.denominator, other.denominator);

        return new Rational(newNumerator, newDenominator);
    }

	@Override // MultiplicativeMonoidElement impls
	public Rational getOne()
	{
		return ONE;
	}


    @Override // GroupElement impls
    public Rational negate()
    {
        return new Rational(-this.numerator, this.denominator);
    }


    @Override // FieldElement impls
    public Rational inverse()
    {
        if (this.isZero()) {
            throw new ArithmeticException("Cannot take the inverse of zero in a Field.");
        }

        return new Rational(this.denominator, this.numerator);
    }


    @Override // ExponentiableElement impls
    public Rational power(int exponent)
    {
        if (this.isZero() && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }

        if (exponent == 0) {
            return ONE;
        }

        Rational base = this;
        int exp = Math.abs(exponent);
        Rational result = ONE;

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


    @Override // ComparableElement impls
    public int compareTo(Rational other)
    {
        long ad = this.numerator * other.denominator;
        long bc = other.numerator * this.denominator;
        return Long.compare(ad, bc);
    }


    @Override // NormableElement
    public Real norm()
    {
    	Rational exactAbs = this.abs();
        return new Real((double) exactAbs.numerator / exactAbs.denominator);
    }


    @Override // Java Standard impls
    public String toString()
    {
        if (denominator == 1) {
        	return String.valueOf(numerator);
        }

        return numerator + "/" + denominator;
    }

    @Override // Java Standard impls
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

    @Override // Java Standard impls
    public final int hashCode()
    {
        return java.util.Objects.hash(numerator, denominator);
    }
}
