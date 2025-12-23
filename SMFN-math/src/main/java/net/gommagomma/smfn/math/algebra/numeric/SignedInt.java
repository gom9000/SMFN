package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.EuclideanDomainElement;

public final class SignedInt
implements CommutativeRingElement<SignedInt>, Exponentiable<SignedInt>, Absolutable<SignedInt>, EuclideanDomainElement<SignedInt, Natural>
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


    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(SignedInt other)
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
    public SignedInt copy()
    {
        return new SignedInt(this.value);
    }


    @Override // MonoidElement impls
    public SignedInt add(SignedInt other)
    {
        long result = Math.addExact(this.value, other.value);

        return new SignedInt(result);
    }

	@Override // MonoidElement impls
	public SignedInt getZero()
	{
		return ZERO;
	}


    @Override // MultiplicativeMonoidElement impls
    public SignedInt multiply(SignedInt other)
    {
        long result = Math.multiplyExact(this.value, other.value);

        return new SignedInt(result);
    }

	@Override // MultiplicativeMonoidElement impls
	public SignedInt getOne()
	{
		return ONE;
	}


	@Override // GroupElement impls
	public SignedInt negate()
	{
		if (this.value == Long.MIN_VALUE) {
			throw new ArithmeticException("Negation of Long.MIN_VALUE causes overflow.");
		}

		return new SignedInt(-this.value);
	}


    @Override // ExponentiableElement impls
    public SignedInt power(int exponent)
    {
    	if (exponent < 0) {
            throw new ArithmeticException("Cannot raise a SignedInt to a negative power within the ring of integers.");
        }
        if (exponent == 0) {
            return ONE;
        }
        if (this.isZero()) {
            return ZERO;
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


    @Override // ComparableElement impls
    public int compareTo(SignedInt other)
    {
        return Long.compare(this.value, other.value);
    }

    
    /**
     * Implementa la funzione norma euclidea v(n) = |n|.
     */
    @Override // EuclideanDomainElement impls
    public Natural normValue() {
        return new Natural(Math.abs(this.value));
    }

    /**
     * Calcola il quoziente q della Divisione Euclidea: this = q * divisor + remainder.
     */
    @Override // EuclideanDomainElement impls
    public SignedInt quotient(SignedInt divisor) {
        if (divisor.isZero()) {
            throw new ArithmeticException("Division by zero in Euclidean domain.");
        }

        long a = this.value;
        long b = divisor.value;

        SignedInt remainderElement = this.remainder(divisor);
        long r = remainderElement.value; 
        long numerator = Math.subtractExact(a, r);
        long q = numerator / b; 

        return new SignedInt(q);
    }

    /**
     * Calcola il resto r della Divisione Euclidea: this = q * divisor + remainder.
     * Restituisce r, normalizzato per essere nell'intervallo [0, |divisor| - 1].
     */
    @Override // EuclideanDomainElement impls
    public SignedInt remainder(SignedInt divisor) {
        if (divisor.isZero()) {
            throw new ArithmeticException("Division by zero in Euclidean domain.");
        }
        
        long rem = this.value % divisor.value;
        if (rem < 0) {
            rem += Math.abs(divisor.value);
        }

        return new SignedInt(rem);
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

        if (!(other instanceof SignedInt)) {
            return false;
        }

        SignedInt natural = (SignedInt) other;
        return this.value == natural.value;
    }

    @Override // Java Standard impls
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }
}
