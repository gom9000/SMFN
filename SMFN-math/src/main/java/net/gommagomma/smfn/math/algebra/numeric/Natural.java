package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;

public final class Natural
implements SemiringElement<Natural>, ExponentiableElement<Natural>, ComparableElement<Natural>
{
	public static final Natural ZERO = new Natural(0);
    public static final Natural ONE = new Natural(1);

	private final long value;


    public Natural(long value)
    {
        if (value < 0) {
            throw new IllegalArgumentException("Natural numbers cannot be negative.");
        }
        this.value = value;
    }


    public long getValue()
    {
        return value;
    }


    // AlgebraicElement impls

    @Override
    public boolean isEqual(Natural other)
    {
        return this.value == other.value;
    }


    @Override
    public Natural copy()
    {
        return new Natural(this.value);
    }


	@Override
	public Natural getZero()
	{
		return Natural.ZERO;
	}


	@Override
	public Natural getOne()
	{
		return Natural.ONE;
	}


    // MonoidElement impls

    @Override
    public Natural add(Natural other)
    {
        long result = Math.addExact(this.value, other.value); 

        return new Natural(result);
    }


    // MultiplicativeMonoidElement impls

    @Override
    public Natural multiply(Natural other)
    {
        long result = Math.multiplyExact(this.value, other.value);

        return new Natural(result);
    }


    @Override
    public Natural power(int exponent)
    {
        if (exponent < 0) {
            throw new ArithmeticException("Cannot raise a Natural number to a negative power.");
        }
        if (exponent == 0) {
            return Natural.ONE;
        }
        if (this.isZero()) {
            return Natural.ZERO;
        }

        long base = this.value;
        long result = 1;
        int exp = exponent;

        while (exp > 0) {
            if (exp % 2 == 1) {
                result = Math.multiplyExact(result, base);
            }
            base = Math.multiplyExact(base, base);
            exp /= 2;
        }
        return new Natural(result);
    }


    @Override
    public int compareTo(Natural other)
    {
        return Long.compare(this.value, other.value);
    }


    // Java Standard impls

    @Override
    public String toString()
    {
    	return String.valueOf(this.value);
    }


    @Override
    public final boolean equals(Object other) 
    {
        return (other instanceof Natural) && isEqual((Natural)other);
    }


    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }
}
