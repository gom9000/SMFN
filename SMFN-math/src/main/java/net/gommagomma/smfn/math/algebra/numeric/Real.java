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
    public boolean isEqual(Real other)
    {
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


    /**
     * Indicates whether some other object is "equal to" this real number
     * using an epsilon-based comparison for mathematical correctness.
     *
     * <p><b>WARNING:</b> Due to the use of a tolerance (EPSILON), this
     * method violates the strict transitivity contract required by
     * {@link Object#equals(Object)} and standard Java collections
     * (like {@link java.util.HashSet} or {@link java.util.HashMap}).
     * Use this class in standard collections with caution.</p>
     *
     * @param other The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object other) 
    {
        return (other instanceof Real) && isEqual((Real)other);
    }


    /**
     * Returns a hash code value for the object. The hash code is based
     * on the exact internal double value, which might be inconsistent
     * with the epsilon-based {@link #equals(Object)} method for values
     * that are "close enough" but have different exact representations.
     *
     * @return A hash code value for this object.
     */
    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }


    @Override
    public Real norm()
    {
        return new Real(Math.abs(this.value));
    }


}
