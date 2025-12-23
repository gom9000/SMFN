package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;

public final class Natural
implements SemiringElement<Natural>, Exponentiable<Natural>, Orderable<Natural>
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


    @Override  // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(Natural other)
    {
    	if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        return this.value == other.value;
    }

    @Override // AlgebraicElement impls
    public Natural copy()
    {
        return new Natural(this.value);
    }


    @Override // MonoidElement impls
    public Natural add(Natural other)
    {
        long result = Math.addExact(this.value, other.value); 

        return new Natural(result);
    }

	@Override // MonoidElement impls
	public Natural getZero()
	{
		return ZERO;
	}


    @Override // MultiplicativeMonoidElement impls
    public Natural multiply(Natural other)
    {
        long result = Math.multiplyExact(this.value, other.value);

        return new Natural(result);
    }

	@Override // MultiplicativeMonoidElement impls
	public Natural getOne()
	{
		return ONE;
	}


    @Override // ExponentiableElement impls
    public Natural power(int exponent)
    {
        if (exponent < 0) {
            throw new ArithmeticException("Cannot raise a Natural number to a negative power.");
        }
        if (exponent == 0) {
            return getOne();
        }
        if (this.isZero()) {
            return getZero();
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


    @Override // Orderable impls
    public int compareTo(Natural other)
    {
        return Long.compare(this.value, other.value);
    }


    @Override // Java Standard impls
    public String toString()
    {
    	return String.valueOf(this.value);
    }

    @Override // Java Standard impls
    public final boolean equals(Object other) 
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof Natural)) {
            return false;
        }

        Natural natural = (Natural) other;
        return this.value == natural.value;
    }

    @Override // Java Standard impls
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }
}
