package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;
import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Sqrtable;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Rappresenta un numero reale approssimato, basato su un valore primitivo
 * in virgola mobile a doppia precisione (double). Implementa le capacità numeriche,
 * di ordinamento e di calcolo algebrico all'interno del campo reale.
 * Implementa anche LinearElement: R e' uno spazio vettoriale di dimensione 1 su se stesso.
 */
public final class Real
implements ApproximateElement<Real>, Normable<Real>, Orderable<Real>, Absolutable<Real>, Exponentiable<Real>, Sqrtable<Real>,
           LinearElement<Real, Real>
{
    private final double value;

    /**
     * Costruisce un numero reale a partire da un valore double.
     * 
     * @param value il valore numerico in virgola mobile
     */
    public Real(double value) { this.value = value; }

    /**
     * Restituisce il valore primitivo sottostante del numero reale.
     * 
     * @return il valore double
     */
    public double getValue() { return value; }

    @Override // ScalarElement impls
    public ScalarStructure<Real> getStructure() {
        return RealField.INSTANCE;
    }

    @Override // CompositeElement impls (via LinearElement)
    public ScalarStructure<Real> getScalarStructure() {
        return getStructure();
    }

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
        return compareTo(other) < 0;
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
        if (RealField.INSTANCE.isZero(this) && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }

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