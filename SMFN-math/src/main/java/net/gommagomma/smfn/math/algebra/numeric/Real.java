package net.gommagomma.smfn.math.algebra.numeric;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.SqrtableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class Real
implements FieldElement<Real>, NormableElement<Real, Real>, ExponentiableElement<Real>, SqrtableElement<Real>, ComparableElement<Real>
{
	public static final Real ZERO = new Real(0.0);
    public static final Real ONE = new Real(1.0);

	private final double value;


	public Real(double value)
	{
        this.value = value;
    }


    public double getValue()
    {
        return value;
    }


    // AlgebraicElement impls

    @Override
    public boolean isMathematicallyEqualTo(Real other)
    {
    	if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        return Math.abs(this.value - other.value) < MathConstants.EPSILON;
    }


    @Override
    public Real copy()
    {
        return new Real(this.value);
    }


	@Override
	public Real getZero()
	{
		return Real.ZERO; 
	}


	@Override
	public Real getOne() {
		return Real.ONE; 
	}


    // MonoidElement impls

    @Override
    public Real add(Real other)
    {
        return new Real(this.value + other.value);
    }


    // GroupElement impls

    @Override
    public Real negate()
    {
        return new Real(-this.value);
    }


    // MultiplicativeMonoidElement impls

    @Override
    public Real multiply(Real other)
    {
        return new Real(this.value * other.value);
    }


    // FieldElement impls

    @Override
    public Real inverse()
    {
        if (this.isZero())
        {
            throw new ArithmeticException("Cannot take the inverse of zero in a Field.");
        }
        return new Real(1.0 / this.value);
    }    


    @Override
    public Real power(int exponent)
    {
        if (this.isZero() && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }

        return new Real(Math.pow(this.value, exponent));
    }


    @Override
    public Real sqrt()
    {
        if (this.value < 0) {
            throw new ArithmeticException("Cannot take the square root of a negative real number.");
        }

        return new Real(Math.sqrt(this.value));
    }


    @Override
    public int compareTo(Real other)
    {
        return Double.compare(this.value, other.value);
    }


    // Java Standard impls

    @Override
    public String toString()
    {
    	return String.valueOf(this.value);
    }


    @Override
    public final boolean equals(Object other) 
    {
        if (this == other) {
        	return true;
        }

        if (!(other instanceof Real)) {
        	return false;
        }

        Real real = (Real) other;
        return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(real.value);
    }


    @Override
    public final int hashCode()
    {
    	return java.util.Objects.hash(this.value);
    }


    @Override
    public Real norm()
    {
        return new Real(modulus());
    }


    @Override
    public double modulus() {
        return Math.abs(this.value);
    }
}
