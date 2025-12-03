package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Complex;


public class ComplexField
implements Field<Complex>
{
	public static final ComplexField INSTANCE = new ComplexField();

    private ComplexField() {}

    public static ComplexField getInstance() {
        return INSTANCE;
    }

	@Override
	public String getName()
	{
		return "Complex Field (C)";
	}


	@Override
	public boolean contains(Complex e)
	{
		if (e == null) {
			return false;
		}

		return !Double.isNaN(e.getRe()) && !Double.isInfinite(e.getRe()) &&
				!Double.isNaN(e.getIm()) && !Double.isInfinite(e.getIm());
	}


	@Override
	public Complex additiveIdentity()
	{
		return Complex.ZERO;
	}


	@Override
	public Complex multiplicativeIdentity()
	{
		return Complex.ONE;
	}


	@Override
    public Complex valueOf(double value)
	{
        return new Complex(value, 0.0);
    }
}
