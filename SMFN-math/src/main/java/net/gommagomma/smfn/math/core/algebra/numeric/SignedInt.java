package net.gommagomma.smfn.math.core.algebra.numeric;

import net.gommagomma.smfn.math.core.algebra.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.core.algebra.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.CommutativeRingElement;

public final class SignedInt
implements CommutativeRingElement<SignedInt>, ExponentiableElement<SignedInt>, ComparableElement<SignedInt>
{
	public static final SignedInt ZERO = new SignedInt(0);
    public static final SignedInt ONE = new SignedInt(1);

	private final long value;


    public SignedInt(long value)
    {
        this.value = value;
    }


    public long getValue()
    {
        return value;
    }


    // AlgebraicElement impls

    @Override
    public boolean isEqual(SignedInt other)
    {
        return this.value == other.value;
    }


    @Override
    public SignedInt copy()
    {
        return new SignedInt(this.value);
    }


	@Override
	public SignedInt getZero()
	{
		return SignedInt.ZERO;
	}


	@Override
	public SignedInt getOne()
	{
		return SignedInt.ONE;
	}


    // MonoidElement impls

    @Override
    public SignedInt add(SignedInt other)
    {
        long result = Math.addExact(this.value, other.value);

        return new SignedInt(result);
    }


   // GroupElement impls

	@Override
	public SignedInt negate()
	{
		if (this.value == Long.MIN_VALUE) {
			throw new ArithmeticException("Negation of Long.MIN_VALUE causes overflow.");
		}

		return new SignedInt(-this.value);
	}


    // MultiplicativeMonoidElement impls

    @Override
    public SignedInt multiply(SignedInt other)
    {
        long result = Math.multiplyExact(this.value, other.value);

        return new SignedInt(result);
    }


    @Override
    public SignedInt power(int exponent)
    {
    	if (exponent < 0) {
            throw new ArithmeticException("Cannot raise a SignedInt to a negative power within the ring of integers.");
        }
        if (exponent == 0) {
            return SignedInt.ONE;
        }
        if (this.isZero()) {
            return SignedInt.ZERO;
        }

        long base = this.value;
        long result = 1;
        int exp = exponent;

        while (exp > 0)
        {
            if (exp % 2 == 1) {
                result = Math.multiplyExact(result, base);
            }
            base = Math.multiplyExact(base, base);
            exp /= 2;
        }

        return new SignedInt(result);
    }


    @Override
    public int compareTo(SignedInt other)
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
        return (other instanceof SignedInt) && isEqual((SignedInt)other);
    }


    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }


    @Override
    public double modulus() {
        return Math.abs(this.value);
    }
}
