package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.core.structures.ApproximateStructure;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.utils.MathConstants;

public final class RealField
implements Field<Real>, ApproximateStructure<Real>
{
	private static final Real ZERO = new Real(0.0);
	private static final Real ONE = new Real(1.0);

	public static final RealField INSTANCE = new RealField(MathConstants.EPSILON);
	private final double epsilon;


    private RealField(double epsilon) { this.epsilon = epsilon; }
    public static RealField getInstance() { return INSTANCE; }


    @Override // ApproximateStructure impls
    public double epsilon() { return epsilon; }


    // NumericFactory (via ScalarStructure) impls
    @Override public Real zero() { return ZERO; }
    @Override public Real one() { return ONE; }
    @Override public Real of(double v) { return new Real(v); }
    @Override public Real of(long v) { return new Real(v); }
    @Override public Real of(int v) { return new Real(v); }


    // AdditiveMonoid impls
    @Override
    public Real add(Real a, Real b) { 
        return new Real(a.getValue() + b.getValue()); 
    }


    // MultiplicativeMonoid impls
    @Override
    public Real multiply(Real a, Real b) { 
        return new Real(a.getValue() * b.getValue()); 
    }


     // AdditiveGroup impls
    @Override
    public Real negate(Real e) {
        return new Real(-e.getValue());
    }


    // MultiplicativeGroup impls
    @Override
    public Real inverse(Real e) {
    	if (isZero(e)) throw new ArithmeticException("Division by zero");

        return new Real(1.0 / e.getValue());
    }


    // AlgebraicStructure impls
	@Override
	public String getName()	{
		return "Real Field (R)";
	}

	@Override
	public boolean contains(Real e)	{
		return e != null && !Double.isNaN(e.getValue()) && !Double.isInfinite(e.getValue());
	}

	@Override
	public boolean areEqual(Real a, Real b) {
	    if (a == b) return true;
	    if (a == null || b == null) return false;

	    return Math.abs(a.getValue() - b.getValue()) < epsilon();
	}
}
