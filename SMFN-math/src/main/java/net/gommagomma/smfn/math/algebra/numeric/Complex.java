package net.gommagomma.smfn.math.algebra.numeric;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.SqrtableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.utils.MathConstants;


public final class Complex
implements FieldElement<Complex>, NormableElement<Real, Complex>, ExponentiableElement<Complex>, SqrtableElement<Complex>
{
	public static final Complex ZERO = new Complex(0.0, 0.0);
    public static final Complex ONE = new Complex(1.0, 0.0);

    private final double real;
    private final double imaginary;


    /**
     * Constructs a new complex number with the given real and imaginary parts.
     * @param real the real part of the complex number
     * @param imaginary the imaginary part of the complex number
     */
    public Complex(double real, double imaginary)
    {
        this.real = real;
        this.imaginary = imaginary;
    }


    /**
     * Constructs a new complex number with the given real part.
     * @param real the real part of the complex number
     */
    public Complex(double real)
    {
        this(real, 0.0);
    }


    /**
     * Returns the real part of this complex number.
     * @return the real part of this complex number
     */
    public double getRe()
    {
    	return this.real;
    }


    /**
     * Returns the imaginary part of this complex number.
     * @return the imaginary part of this complex number
     */
    public double getIm()
    {
    	return this.imaginary;
    }


    // AlgebraicElement impls

    @Override
    public boolean isEqual(Complex other)
    {
        return (Math.abs(this.real - other.real) < MathConstants.EPSILON)
            && (Math.abs(this.imaginary - other.imaginary) < MathConstants.EPSILON);
    }


    @Override
    public Complex copy()
    {
        return new Complex(this.real, this.imaginary);
    }


	@Override
	public Complex getZero()
	{
		return Complex.ZERO; 
	}


	@Override
	public Complex getOne()
	{
		return Complex.ONE;
	}


    // MonoidElement impls

    @Override
    public Complex add(Complex other)
    {
        return new Complex(this.real + other.real, this.imaginary + other.imaginary);
    }


    // GroupElement impls

    @Override
    public Complex negate()
    {
        return new Complex(-this.real, -this.imaginary);
    }


    // MultiplicativeMonoidElement impls

    @Override
    public Complex multiply(Complex other)
    {
        double newReal = this.real * other.real - this.imaginary * other.imaginary;
        double newImaginary = this.real * other.imaginary + this.imaginary * other.real;
        return new Complex(newReal, newImaginary);
    }


    // FieldElement impls

    @Override
    public Complex inverse()
    {
        double modSq = modulusSquared();

        if (this.isZero()) {
            throw new ArithmeticException("Cannot take the inverse of zero in a Field.");
        }

        return new Complex(this.real / modSq, -this.imaginary / modSq);
    }


    /**
     * Calcola il modulo (valore assoluto) del numero complesso.
     * |z| = sqrt(a^2 + b^2)
     */
    public double modulus()
    {
        return Math.sqrt(real * real + imaginary * imaginary);
    }


    /**
     * Calcola il quadrato del modulo.
     */
    public double modulusSquared()
    {
        return real * real + imaginary * imaginary;
    }


    @Override
    public Complex power(int exponent)
    {
        if (this.isZero() && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }

        if (exponent == 0) {
            return Complex.ONE;
        }

        Complex base = this;
        int exp = Math.abs(exponent);
        Complex result = Complex.ONE;

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


    @Override
    public Complex sqrt()
    {
        if (this.isZero()) {
            return Complex.ZERO;
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


    public Complex conjugate()
    {
    	return new Complex(real, -imaginary);
    }


    // Java Standard impls

    @Override
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


    /**
     * Indicates whether some other object is "equal to" this complex number
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
        return (other instanceof Complex) && isEqual((Complex)other);
    }


    /**
     * Returns a hash code value for the object, based on the exact internal
     * double values of the real and imaginary parts.
     *
     * @return A hash code value for this object.
     */
    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(real, imaginary);
    }


    @Override
    public Real norm()
    {
        return new Real(this.modulus());
    }


    /**
     * Calcola l'argomento (fase) del numero complesso in radianti.
     * Restituisce un valore nell'intervallo (-pi, pi].
     * @return L'angolo in radianti.
     */
    public double argument() {
        return Math.atan2(imaginary, real);
    }
}
