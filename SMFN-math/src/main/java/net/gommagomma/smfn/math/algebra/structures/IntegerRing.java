package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.core.structures.ExactStructure;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.utils.MathConstants;

public final class IntegerRing
implements EuclideanDomain<SignedInt, Natural>, ExactStructure<SignedInt>
{
	private static final SignedInt ZERO = new SignedInt(0);
	private static final SignedInt ONE = new SignedInt(1);

	public static final IntegerRing INSTANCE = new IntegerRing();

    private IntegerRing() {}
    public static IntegerRing getInstance() { return INSTANCE; }


    // NumericFactory (via ScalarStructure) impls
    @Override public SignedInt zero() { return ZERO; }
    @Override public SignedInt one() { return ONE; }
    @Override public SignedInt of(long value) { return new SignedInt(value); }
    @Override public SignedInt of(int value) { return new SignedInt(value); }
    @Override
    public SignedInt of(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Non-finite value: " + value);
        }
        long rounded = Math.round(value);
        if (Math.abs(value - rounded) > MathConstants.EPSILON) {
            throw new IllegalArgumentException("Value " + value + " is not an integer.");
        }
        return new SignedInt(rounded);
    }


    // AdditiveMonoid impls
    @Override
    public SignedInt add(SignedInt a, SignedInt b) {
        return new SignedInt(Math.addExact(a.getValue(), b.getValue()));
    }


    // MultiplicativeMonoid impls
    @Override
    public SignedInt multiply(SignedInt a, SignedInt b) {
        return new SignedInt(Math.multiplyExact(a.getValue(), b.getValue()));
    }


    // AdditiveGroup impls
    @Override
    public SignedInt negate(SignedInt e) {
    	if (e.getValue() == Long.MIN_VALUE) {
            throw new ArithmeticException("Integer overflow: negation of Long.MIN_VALUE");
        }
        return new SignedInt(-e.getValue());
    }


    // EuclideanDomain impls
    @Override
    public SignedInt quotient(SignedInt a, SignedInt b) {
        if (isZero(b)) throw new ArithmeticException("Division by zero");
        
        long aVal = a.getValue();
        long bVal = b.getValue();
        
        // Per gli interi, a = qb + r. Se r deve essere >= 0:
        long r = aVal % bVal;
        if (r < 0) r += Math.abs(bVal);
        
        return new SignedInt((aVal - r) / bVal);
    }

    @Override
    public SignedInt remainder(SignedInt a, SignedInt b) {
        if (isZero(b)) throw new ArithmeticException("Modulo by zero");
        long rem = a.getValue() % b.getValue();
        if (rem < 0) rem += Math.abs(b.getValue());
        return new SignedInt(rem);
    }

	@Override
	public Natural degree(SignedInt e) {
		return new Natural(Math.abs(e.getValue()));
	}


    @Override // AlgebraicStructure impls
    public String getName()
    {
        return "Integer Ring (Z)";
    }

    @Override
    public boolean contains(SignedInt e)
    {
    	return (e != null);
    }
}
