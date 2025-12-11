package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Real;


public final class RealField
implements Field<Real>
{
	public static final RealField INSTANCE = new RealField();


    private RealField() {}
    public static RealField getInstance() { return INSTANCE; }


	@Override // AlgebraicStructure impls
	public String getName()	{
		return "Real Field (R)";
	}

	@Override // AlgebraicStructure impls
	public boolean contains(Real e)	{
		if (e == null) {
            return false;
        }

        return !Double.isNaN(e.getValue()) && !Double.isInfinite(e.getValue());
	}


	@Override // AdditiveMonoid impls
	public Real additiveIdentity() {
		return Real.ZERO;
	}


	@Override // MultiplicativeMonoid impls
	public Real multiplicativeIdentity() {
		return Real.ONE;
	}


	@Override // NumericFactory impls
    public Real of(double value) {
        return new Real(value);
    }

    @Override // NumericFactory impls
    public Real of(long value) {
        return new Real(value); 
    }

    @Override // NumericFactory impls
    public Real of(int value) {
        return new Real(value);
    }
}
