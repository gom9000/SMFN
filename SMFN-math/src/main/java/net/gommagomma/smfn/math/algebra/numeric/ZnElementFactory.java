package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;

public class ZnElementFactory
implements NumericFactory<ZnElement>
{
//	private static final ZnElementFactory INSTANCE = new ZnElementFactory();
	private final SignedInt modulus;
	private final ZnElement ZERO;
    private final ZnElement ONE;


    public ZnElementFactory(SignedInt modulus) {
    	this.modulus = modulus;
        this.ZERO = new ZnElement(SignedIntFactory.getInstance().zero(), modulus);
        this.ONE = new ZnElement(SignedIntFactory.getInstance().one(), modulus);
    }
 //   public static ZnElementFactory getInstance() { return INSTANCE; }


    @Override // NumericFactory impls
    public ZnElement zero() { return ZERO; }

    @Override // NumericFactory impls
    public ZnElement one() { return ONE; }

    @Override // NumericFactory impls
	public ZnElement fromDouble(double value) {
    	if (Double.isNaN(value) || Double.isInfinite(value)) {
	        throw new IllegalArgumentException("Cannot create a SignedInt number from a non-finite value: " + value);
	    }
        if (value > Long.MAX_VALUE) {
	        throw new ArithmeticException("Value " + value + " is outside the range of SignedInt (long).");
	    }

//        long roundedValue = Math.round(value);
//        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
//            throw new IllegalArgumentException("Cannot create SignedInt from non-integer value: " + value);
//        }

        SignedInt signedInt = SignedIntFactory.getInstance().fromDouble(value);
        return new ZnElement(signedInt, this.modulus);
	}

	@Override // NumericFactory impls
	public ZnElement fromLong(long value) {
		SignedInt signedInt = SignedIntFactory.getInstance().fromLong(value);
		return new ZnElement(signedInt, this.modulus);
	}

	@Override // NumericFactory impls
	public ZnElement fromInt(int value) {
		SignedInt signedInt = SignedIntFactory.getInstance().fromInt(value);
		return new ZnElement(signedInt, this.modulus);
	}
}
