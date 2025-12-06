package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;

public class SignedIntFactory
implements NumericFactory<SignedInt>
{
	private static final SignedIntFactory INSTANCE = new SignedIntFactory();
	private static final SignedInt ZERO = new SignedInt(0);
    private static final SignedInt ONE = new SignedInt(1);


    private SignedIntFactory() {}
    public static SignedIntFactory getInstance() { return INSTANCE; }


    @Override // NumericFactory impls
    public SignedInt zero() { return ZERO; }

    @Override // NumericFactory impls
    public SignedInt one() { return ONE; }

    @Override // NumericFactory impls
	public SignedInt fromDouble(double value) {
    	if (Double.isNaN(value) || Double.isInfinite(value)) {
	        throw new IllegalArgumentException("Cannot create a SignedInt number from a non-finite value: " + value);
	    }
        if (value > Long.MAX_VALUE) {
	        throw new ArithmeticException("Value " + value + " is outside the range of SignedInt (long).");
	    }

        long roundedValue = Math.round(value);
//        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
//            throw new IllegalArgumentException("Cannot create SignedInt from non-integer value: " + value);
//        }

		return new SignedInt(roundedValue);
	}

	@Override // NumericFactory impls
	public SignedInt fromLong(long value) {
		return new SignedInt(value);
	}

	@Override // NumericFactory impls
	public SignedInt fromInt(int value) {
		return new SignedInt(value);
	}

}
