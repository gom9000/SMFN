package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Conjugable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Sqrtable;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Rappresenta un numero complesso in forma algebrica (z = a + bi), 
 * basato su valori in virgola doppia precisione (double) per la parte reale e immaginaria.
 * Implementa le capacità numeriche e algebriche per operare all'interno del campo complesso.
 */
public final class Complex
implements ApproximateElement<Complex>, Normable<Real>, Exponentiable<Complex>, Sqrtable<Complex>, Conjugable<Complex>
{
    private final double real;
    private final double imaginary;

    /**
     * Costruisce un numero complesso a partire dalla sua parte reale e immaginaria.
     * 
     * @param real la parte reale
     * @param imaginary la parte immaginaria
     */
    public Complex(double real, double imaginary)
    {
        this.real = real;
        this.imaginary = imaginary;
    }

    /**
     * Costruisce un numero complesso puramente reale con parte immaginaria nulla.
     * 
     * @param real la parte reale
     */
    public Complex(double real) { this(real, 0.0); }

    /**
     * Costruisce un numero complesso a partire da un'istanza di {@link Real}.
     * 
     * @param real l'elemento reale
     */
    public Complex(Real real) { this(real.getValue(), 0.0); }

    /**
     * Restituisce la parte reale del numero complesso.
     * 
     * @return il valore della parte reale
     */
    public double getRe() { return this.real; }

    /**
     * Restituisce la parte immaginaria del numero complesso.
     * 
     * @return il valore della parte immaginaria
     */
    public double getIm() {    return this.imaginary; }

    /**
     * Calcola il modulo (o valore assoluto) del numero complesso.
     * 
     * @return la magnitudine complessa $\sqrt{a^2 + b^2}$
     */
    public double modulus()
    {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    /**
     * Calcola il quadrato del modulo del numero complesso, utile per evitare 
     * l'operazione di radice quadrata quando serve solo il confronto o la norma al quadrato.
     * 
     * @return il quadrato della magnitudine $a^2 + b^2$
     */
    public double modulusSquared()
    {
        return real * real + imaginary * imaginary;
    }
 
    @Override // Conjugable impls
    /**
     * Calcola il complesso coniugato ($a - bi$).
     * 
     * @return un nuovo numero complesso coniugato
     */
    public Complex conjugate()
    {
        return new Complex(real, -imaginary);
    }

    /**
     * Calcola l'argomento (fase) del numero complesso in radianti.
     * Restituisce un valore nell'intervallo $(-\pi, \pi]$.
     * 
     * @return l'angolo di fase in radianti
     */
    public double argument() {
        return Math.atan2(imaginary, real);
    }

    @Override // ScalarElement impls
    public ScalarStructure<Complex> getStructure() {
        return ComplexField.INSTANCE;
    }

    @Override // AlgebraicElement impls
    public Complex copy()
    {
        return new Complex(this.real, this.imaginary);
    }

    @Override // Normable impls
    public Real norm()
    {
        return RealField.INSTANCE.of(this.modulus());
    }

    @Override // Exponentiable impls
    public Complex power(int exponent)
    {
        ComplexField field = ComplexField.INSTANCE;
        if (field.isZero(this) && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }
        if (exponent == 0) return field.one();

        Complex base = this;
        Complex result = field.one();
        long exp = Math.abs((long)exponent); // evita l'overflow di Math.abs(Integer.MIN_VALUE)

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

    @Override // Sqrtable impls
    public Complex sqrt() {
        ComplexField field = ComplexField.INSTANCE;
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