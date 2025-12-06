package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;


public final class NaturalFactory
implements NumericFactory<Natural>
{
    private static final NaturalFactory INSTANCE = new NaturalFactory();
    private static final Natural ZERO = new Natural(0);
    private static final Natural ONE = new Natural(1);

    private NaturalFactory() {}
    public static NaturalFactory getInstance() { return INSTANCE; }


    @Override // NumericFactory impls
    public Natural zero() { return ZERO; }

    @Override // NumericFactory impls
    public Natural one() { return ONE; }

    @Override // NumericFactory impls
    public Natural fromDouble(double value) {
    	if (Double.isNaN(value) || Double.isInfinite(value)) {
	        throw new IllegalArgumentException("Cannot create a Natural number from a non-finite value: " + value);
	    }
        if (value < 0.0) {
            throw new IllegalArgumentException("Cannot create Natural from negative value: " + value);
        }
        if (value > Long.MAX_VALUE) {
	        throw new ArithmeticException("Value " + value + " is outside the range of Natural (long).");
	    }

        long roundedValue = Math.round(value);
//        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
//            throw new IllegalArgumentException("Cannot create Natural from non-integer value: " + value);
//        }
        
        return new Natural(roundedValue);
    }

    @Override // NumericFactory impls
    public Natural fromLong(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }

    @Override // NumericFactory impls
    public Natural fromInt(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }
}
