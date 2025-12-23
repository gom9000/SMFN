package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Complex;


public final class ComplexField
implements Field<Complex>
{
	public static final ComplexField INSTANCE = new ComplexField();


    private ComplexField() {}
    public static ComplexField getInstance() { return INSTANCE; }


	@Override // AlgebraicStructure impls
	public String getName()
	{
		return "Complex Field (C)";
	}

	@Override // AlgebraicStructure impls
	public boolean contains(Complex e)
	{
		if (e == null) {
			return false;
		}

		return !Double.isNaN(e.getRe()) && !Double.isInfinite(e.getRe()) &&
				!Double.isNaN(e.getIm()) && !Double.isInfinite(e.getIm());
	}


	@Override // AdditiveMonoid impls
	public Complex additiveIdentity()
	{
		return Complex.ZERO;
	}


	@Override // MultiplicativeMonoid impls
	public Complex multiplicativeIdentity()
	{
		return Complex.ONE;
	}


    @Override // NumericFactory impls
    public Complex of(double value) {
        return new Complex(value);
    }

    @Override // NumericFactory impls
    public Complex of(long value) {
        return new Complex(value);
    }

    @Override // NumericFactory impls
    public Complex of(int value) {
        return new Complex(value);
    }
}
