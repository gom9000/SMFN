package net.gommagomma.smfn.math.algebra.numeric;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.SqrtableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class Real
implements FieldElement<Real>, ExponentiableElement<Real>, SqrtableElement<Real>, ComparableElement<Real>, NormableElement<Real, Real>
{
	private final double value;


	public Real(double value)
	{
        this.value = value;
    }


    public double getValue()
    {
        return value;
    }


    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(Real other)
    {
    	if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        if (Double.isNaN(this.value) && Double.isNaN(other.value)) {
            return true;
        }

        if (Double.isInfinite(this.value) && this.value == other.value) {
            return true;
        }

        return Math.abs(this.value - other.value) < MathConstants.EPSILON;
    }


    @Override // AlgebraicElement impls
    public Real copy()
    {
        return new Real(this.value);
    }


    @Override // MonoidElement impls
    public Real add(Real other)
    {
        return new Real(this.value + other.value);
    }


    @Override // MultiplicativeMonoidElement impls
    public Real multiply(Real other)
    {
        return new Real(this.value * other.value);
    }

	@Override // MonoidElement impls
	public Real getZero()
	{
		return RealFactory.getInstance().zero(); 
	}

	@Override // MultiplicativeMonoidElement impls
	public Real getOne() {
		return RealFactory.getInstance().one(); 
	}


    @Override // GroupElement impls
    public Real negate()
    {
        return new Real(-this.value);
    }


    @Override // FieldElement impls
    public Real inverse()
    {
        if (this.isZero())
        {
            throw new ArithmeticException("Cannot take the inverse of zero in a Field.");
        }
        return new Real(1.0 / this.value);
    }    


    @Override // ExponentiableElement impls
    public Real power(int exponent)
    {
        if (this.isZero() && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }

        return new Real(Math.pow(this.value, exponent));
    }


    @Override // SqrtableElement impls
    public Real sqrt()
    {
        if (this.value < 0) {
            throw new ArithmeticException("Cannot take the square root of a negative real number.");
        }

        return new Real(Math.sqrt(this.value));
    }


    @Override // ComparableElement impls
    public int compareTo(Real other)
    {
        return Double.compare(this.value, other.value);
    }

    @Override // ComparableElement impls
    public double modulus() {
        return Math.abs(this.value);
    }
    

    @Override // NormableElement impls
    public Real norm()
    {
        return new Real(modulus());
    }
 
 
    @Override // Java Standard impls
    public String toString()
    {
    	return String.valueOf(this.value);
    }

    @Override // Java Standard impls
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

    @Override // Java Standard impls
    public final int hashCode()
    {
    	return java.util.Objects.hash(this.value);
    }
}
