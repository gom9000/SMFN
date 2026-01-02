package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;

public final class SignedInt
implements ExactElement<SignedInt>, Orderable<SignedInt>, Absolutable<SignedInt>, Exponentiable<SignedInt>
{
	private final long value;


    public SignedInt(long value)
    {
        this.value = value;
    }

    public long getValue() { return value; }


    @Override // ScalarElement impls
    public ScalarStructure<SignedInt> getStructure() {
        return IntegerRing.INSTANCE;
    }


    @Override // AlgebraicElement impls
    public SignedInt copy() {  return new SignedInt(this.value); }


    @Override // Orderable impls
    public int compareTo(SignedInt other)
    {
        return Long.compare(this.value, other.value);
    }

    @Override
    public boolean isLessThan(SignedInt other) {
        return this.value < other.value;
    }


    @Override // Absolutable impls
    public SignedInt abs() {
        if (this.value == Long.MIN_VALUE) throw new ArithmeticException("Overflow in abs()");
        return new SignedInt(Math.abs(this.value));
    }

    @Override
    public int signum() {
        return Long.signum(this.value);
    }


    @Override // Exponentiable impls
    public SignedInt power(int exponent)
    {
    	IntegerRing ring = IntegerRing.INSTANCE;
    	if (ring.isZero(this) && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }
        if (exponent == 0) { return ring.one(); }

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


    @Override // Java Standard impls
    public String toString()
    {
    	return String.valueOf(this.value);
    }

    @Override
    public final boolean equals(Object other) 
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof SignedInt)) {
            return false;
        }

        SignedInt natural = (SignedInt) other;
        return this.value == natural.value;
    }

    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }
}
