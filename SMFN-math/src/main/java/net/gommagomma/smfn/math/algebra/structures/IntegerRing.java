package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class IntegerRing
implements EuclideanDomain<SignedInt, Natural>
{
	public static final IntegerRing INSTANCE = new IntegerRing();


    private IntegerRing() {}
    public static IntegerRing getInstance() { return INSTANCE; }


    @Override // EuclideanDomain impls
    public SignedInt quotient(SignedInt a, SignedInt b) {
        if (b.isZero()) {
            throw new ArithmeticException("Division by zero in Z.");
        }
        return a.quotient(b);
    }

    @Override // EuclideanDomain impls
    public SignedInt remainder(SignedInt a, SignedInt b) {
        if (b.isZero()) {
            throw new ArithmeticException("Modulo by zero in Z.");
        }
        return a.remainder(b);
    }


    @Override // AlgebraicStructure impls
    public String getName()
    {
        return "Integer Ring (Z)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(SignedInt e)
    {
    	return (e != null);
    }


    @Override // AdditiveMonoid impls
    public SignedInt additiveIdentity()
    {
        return SignedInt.ZERO;
    }


    @Override // MultiplicativeMonoid impls
    public SignedInt multiplicativeIdentity()
    {
        return SignedInt.ONE;
    }


    @Override // NumericFactory impls
	public SignedInt of(double value) {
    	if (Double.isNaN(value) || Double.isInfinite(value)) {
	        throw new IllegalArgumentException("Cannot create a SignedInt number from a non-finite value: " + value);
	    }
        if (value > Long.MAX_VALUE) {
	        throw new ArithmeticException("Value " + value + " is outside the range of SignedInt (long).");
	    }

        long roundedValue = Math.round(value);
        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
            throw new IllegalArgumentException("Cannot convert non-integer value " + value + " to integer type.");
        }

		return new SignedInt(roundedValue);
	}

	@Override // NumericFactory impls
	public SignedInt of(long value) {
		return new SignedInt(value);
	}

	@Override // NumericFactory impls
	public SignedInt of(int value) {
		return new SignedInt(value);
	}
}
