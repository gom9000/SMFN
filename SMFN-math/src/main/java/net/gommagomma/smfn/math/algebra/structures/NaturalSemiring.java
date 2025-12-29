package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.ExactStructure;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class NaturalSemiring
implements Semiring<Natural>, ExactStructure<Natural>
{
	public static final Natural ZERO = new Natural(0);
    public static final Natural ONE = new Natural(1);

	public static final NaturalSemiring INSTANCE = new NaturalSemiring();

    private NaturalSemiring() {}
    public static NaturalSemiring getInstance() { return INSTANCE; }


    // NumericFactory (via ScalarStructure) impls
    @Override public Natural zero() { return ZERO; }
    @Override public Natural one() { return ONE; }
    @Override
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

    @Override
    public Natural of(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }

    @Override
    public Natural of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot create Natural from negative integer: " + value);
        }
        return new Natural(value);
    }


     // AdditiveMonoid impls
    @Override
    public Natural add(Natural a, Natural b) {
        return new Natural(Math.addExact(a.getValue(), b.getValue()));
    }


    // MultiplicativeMonoid impls
    @Override
    public Natural multiply(Natural a, Natural b) {
        return new Natural(Math.multiplyExact(a.getValue(), b.getValue()));
    }


    @Override // AlgebraicStructure impls
    public String getName()
    {
        return "Natural Semiring (N)";
    }

    @Override
    public boolean contains(Natural e)
    {
    	return (e != null);
    }
}
