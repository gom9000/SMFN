package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Sqrtable;


public final class Real
implements ApproximateElement<Real>, Normable<Real>, Orderable<Real>, Absolutable<Real>, Exponentiable<Real>, Sqrtable<Real>
{
	private final double value;


	public Real(double value) { this.value = value; }

    public double getValue() { return value; }


    @Override // AlgebraicElement impls
    public Real copy()
    {
        return new Real(this.value);
    }    


    @Override // Normable impls
    public Real norm()
    {
        return abs();
    }
 
 
    @Override // Orderable impls
    public int compareTo(Real other)
    {
        return Double.compare(this.value, other.value);
    }

    @Override
    public boolean isLessThan(Real other) {
        return this.value < other.value;
    }


	@Override // Absolutable impls
	public Real abs() {
		return new Real(Math.abs(this.value));
	}

	@Override
	public int signum() {
		return (int) Math.signum(this.value);
	}


    @Override // Exponentiable impls
    public Real power(int exponent)
    {
        return new Real(Math.pow(this.value, exponent));
    }


    @Override // Sqrtable impls
    public Real sqrt()
    {
        if (this.value < 0.0) {
            throw new ArithmeticException("Cannot take the square root of a negative real number.");
        }

        return new Real(Math.sqrt(this.value));
    }


    @Override // Java Standard impls
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
}
