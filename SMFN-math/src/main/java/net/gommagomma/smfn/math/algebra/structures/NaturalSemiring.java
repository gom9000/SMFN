package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class NaturalSemiring
implements Semiring<Natural>
{
	public static final NaturalSemiring INSTANCE = new NaturalSemiring();


    private NaturalSemiring() {}
    public static NaturalSemiring getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName()
    {
        return "Natural Semiring (N)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(Natural e)
    {
    	return (e != null);
    }


    @Override // AdditiveMonoid impls
    public Natural additiveIdentity()
    {
        return Natural.ZERO;
    }


    @Override // MultiplicativeMonoid impls
    public Natural multiplicativeIdentity()
    {
        return Natural.ONE;
    }


    @Override // NumericFactory impls
    public Natural of(double value) {
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
        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
            throw new IllegalArgumentException("Cannot convert non-integer value " + value + " to integer type.");
        }
        
        return new Natural(roundedValue);
    }

    @Override // NumericFactory impls
    public Natural of(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }

    @Override // NumericFactory impls
    public Natural of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }
}
