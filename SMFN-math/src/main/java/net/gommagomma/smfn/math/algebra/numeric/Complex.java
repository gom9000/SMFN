package net.gommagomma.smfn.math.algebra.numeric;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.SqrtableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class Complex
implements FieldElement<Complex>, ExponentiableElement<Complex>, SqrtableElement<Complex>, NormableElement<Real, Complex>
{
    private final double real;
    private final double imaginary;


    public Complex(double real, double imaginary)
    {
        this.real = real;
        this.imaginary = imaginary;
    }


    public Complex(double real)
    {
        this(real, 0.0);
    }


    public Complex(Real real)
    {
        this(real.getValue(), 0.0);
    }


    public double getRe()
    {
    	return this.real;
    }


    public double getIm()
    {
    	return this.imaginary;
    }


    public double modulus()
    {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    public double modulusSquared()
    {
        return real * real + imaginary * imaginary;
    }
 
    public Complex conjugate()
    {
    	return new Complex(real, -imaginary);
    }

    /**
     * Calcola l'argomento (fase) del numero complesso in radianti.
     * Restituisce un valore nell'intervallo (-pi, pi].
     * @return L'angolo in radianti.
     */
    public double argument() {
        return Math.atan2(imaginary, real);
    }


    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(Complex other)
    {
    	if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        return (Math.abs(this.real - other.real) < MathConstants.EPSILON)
            && (Math.abs(this.imaginary - other.imaginary) < MathConstants.EPSILON);
    }

    @Override // AlgebraicElement impls
    public Complex copy()
    {
        return new Complex(this.real, this.imaginary);
    }


    @Override // MonoidElement impls
    public Complex add(Complex other)
    {
        return new Complex(this.real + other.real, this.imaginary + other.imaginary);
    }

	@Override // MonoidElement impls
	public Complex getZero()
	{
		return ComplexFactory.getInstance().zero(); 
	}


    @Override // MultiplicativeMonoidElement impls
    public Complex multiply(Complex other)
    {
        double newReal = this.real * other.real - this.imaginary * other.imaginary;
        double newImaginary = this.real * other.imaginary + this.imaginary * other.real;
        return new Complex(newReal, newImaginary);
    }

	@Override // MultiplicativeMonoidElement impls
	public Complex getOne()
	{
		return ComplexFactory.getInstance().one();
	}


    @Override // GroupElement impls
    public Complex negate()
    {
        return new Complex(-this.real, -this.imaginary);
    }


    @Override // FieldElement impls
    public Complex inverse()
    {
        double modSq = modulusSquared();

        if (this.isZero()) {
            throw new ArithmeticException("Cannot take the inverse of zero in a Field.");
        }

        return new Complex(this.real / modSq, -this.imaginary / modSq);
    }


    @Override // ExponentiableElement impls
    public Complex power(int exponent)
    {
        if (this.isZero() && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }

        if (exponent == 0) {
            return ComplexFactory.getInstance().one();
        }

        Complex base = this;
        int exp = Math.abs(exponent);
        Complex result = ComplexFactory.getInstance().one();

        while (exp > 0)
        {
            if (exp % 2 == 1) {
                result = result.multiply(base);
            }
            base = base.multiply(base);
            exp /= 2;
        }

        return exponent < 0 ? result.inverse() : result;
    }


    @Override // SqrtableElement impls
    public Complex sqrt()
    {
        if (this.isZero()) {
            return ComplexFactory.getInstance().zero();
        }

        double magnitude = this.modulus();
        double realPart = Math.sqrt((magnitude + real) / 2.0);
        double imaginaryPart;
        
        if (this.imaginary >= 0) {
            imaginaryPart = Math.sqrt((magnitude - real) / 2.0);
        } else {
            imaginaryPart = -Math.sqrt((magnitude - real) / 2.0);
        }

        return new Complex(realPart, imaginaryPart);
    }


    @Override // NormableElement impls
    public Real norm()
    {
        return RealFactory.getInstance().fromDouble(this.modulus());
    }


    @Override // Java Standard impls
    public String toString()
    {
        if (imaginary == 0) {
            return String.valueOf(real);
        }
        if (real == 0) {
            if (imaginary == 1.0) return "i";
            if (imaginary == -1.0) return "-i";
            return imaginary + "i";
        }

        String imSign = (imaginary > 0) ? " + " : " - ";
        double absIm = Math.abs(imaginary);
        String imStr = (absIm == 1.0) ? "i" : absIm + "i";

        return real + imSign + imStr;
    }

    @Override // Java Standard impls
    public final boolean equals(Object other)
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof Complex)) {
            return false;
        }

        Complex complex = (Complex) other;
        return Double.doubleToLongBits(this.real) == Double.doubleToLongBits(complex.real) &&
        		Double.doubleToLongBits(this.imaginary) == Double.doubleToLongBits(complex.imaginary);
    }

    @Override // Java Standard impls
    public final int hashCode()
    {
    	return java.util.Objects.hash(this.real, this.imaginary);
    }
}
