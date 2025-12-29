package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

public final class Natural
implements ExactElement<Natural>, Orderable<Natural>, Exponentiable<Natural>
{
	private final long value;


    public Natural(long value)
    {
        if (value < 0) {
            throw new IllegalArgumentException("Natural numbers cannot be negative.");
        }
        this.value = value;
    }

    public long getValue() { return value; }


    @Override  // AlgebraicElement impls
    public Natural copy() { return new Natural(value); }


    @Override // Orderable impls
    public int compareTo(Natural other)
    {
        return Long.compare(this.value, other.value);
    }

    @Override
    public boolean isLessThan(Natural other) {
        return this.value < other.value;
    }


    @Override // Exponentiable impls
    public Natural power(int exponent)
    {
    	NaturalSemiring semiring = NaturalSemiring.getInstance();
    	if (semiring.isZero(this) && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }
        if (exponent == 0) { return semiring.one(); }

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
