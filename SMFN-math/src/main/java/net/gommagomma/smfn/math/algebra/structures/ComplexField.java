package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.numeric.Complex;
import net.gommagomma.smfn.math.core.algebra.structures.Field;


public class ComplexField
implements Field<Complex>
{
	public static final ComplexField INSTANCE = new ComplexField();

    private ComplexField() {
    	// Costruttore privato per il singleton
    }

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
}
