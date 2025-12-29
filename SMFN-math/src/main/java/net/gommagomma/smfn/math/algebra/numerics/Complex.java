package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Sqrtable;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;

public final class Complex
implements ApproximateElement<Complex>, Normable<Real>, Exponentiable<Complex>, Sqrtable<Complex>
{
    private final double real;
    private final double imaginary;


    public Complex(double real, double imaginary)
    {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complex(double real) { this(real, 0.0); }
    public Complex(Real real) { this(real.getValue(), 0.0); }

    public double getRe() { return this.real; }
    public double getIm() {	return this.imaginary; }


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
    public Complex copy()
    {
        return new Complex(this.real, this.imaginary);
    }


    @Override // Normable impls
    public Real norm()
    {
        return RealField.getInstance().of(this.modulus());
    }


    @Override // Exponentiable impls
    public Complex power(int exponent)
    {
    	ComplexField field = ComplexField.getInstance();
        if (field.isZero(this) && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }
        if (exponent == 0) return field.one();

        Complex base = this;
        Complex result = field.one();
        int exp = Math.abs(exponent);

        while (exp > 0)
        {
            if (exp % 2 == 1) {
                result = field.multiply(result, base);
            }
            base = field.multiply(base, base);
            exp /= 2;
        }

        return exponent < 0 ? field.inverse(result) : result;
    }


    @Override // Squertable impls
    public Complex sqrt() {
        ComplexField field = ComplexField.getInstance();
        if (field.isZero(this)) return field.zero();

        double x = Math.abs(real);
        double y = Math.abs(imaginary);
        double w;

        if (x >= y) {
            double r = y / x;
            w = Math.sqrt(x) * Math.sqrt(0.5 * (1.0 + Math.sqrt(1.0 + r * r)));
        } else {
            double r = x / y;
            w = Math.sqrt(y) * Math.sqrt(0.5 * (r + Math.sqrt(1.0 + r * r)));
        }

        if (real >= 0) {
            return new Complex(w, imaginary / (2.0 * w));
        } else {
            double imPart = (imaginary >= 0) ? w : -w;
            return new Complex(y / (2.0 * w), imPart);
        }
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

    @Override
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

    @Override
    public final int hashCode()
    {
    	return java.util.Objects.hash(this.real, this.imaginary);
    }
}
