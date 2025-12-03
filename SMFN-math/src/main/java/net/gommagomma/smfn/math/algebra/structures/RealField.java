package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Real;


public final class RealField
implements Field<Real>
{
	public static final RealField INSTANCE = new RealField();

    private RealField() {}

    public static RealField getInstance() {
        return INSTANCE;
    }

	@Override
	public String getName()
	{
		return "Real Field (R)";
	}


	@Override
	public boolean contains(Real e)
	{
		if (e == null) {
            return false;
        }

        return !Double.isNaN(e.getValue()) && !Double.isInfinite(e.getValue());
	}


	@Override
	public Real additiveIdentity()
	{
		return Real.ZERO;
	}


	@Override
	public Real multiplicativeIdentity()
	{
		return Real.ONE;
	}


	@Override
    public Real valueOf(double value)
	{
        return new Real(value);
    }
}
