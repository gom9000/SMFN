package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.utils.MathUtils;

public final class Rational
implements ExactElement<Rational>, Normable<Real>, Orderable<Rational>, Absolutable<Rational>, Exponentiable<Rational>
{
	private final long numerator;
    private final long denominator;


    public Rational(long numerator, long denominator)
    {
    	if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }

    	// denominator is always positive, the sign is moved to the numerator
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        long gcd = MathUtils.greatestCommonDivisor(numerator, denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    public Rational(long value)
    {
        this(value, 1);
    }

    public long getNumerator() { return numerator; }
    public long getDenominator() {  return denominator; }


    @Override // ScalarElement impls
    public ScalarStructure<Rational> getStructure() {
        return RationalField.INSTANCE;
    }


    @Override // AlgebraicElement impls
    public Rational copy() { return new Rational(numerator, denominator); }


    @Override // Normable
    public Real norm() {
        return new Real((double) Math.abs(numerator) / denominator);
    }


    @Override // Orderable impls
    public int compareTo(Rational other)
    {
        long ad = this.numerator * other.denominator;
        long bc = other.numerator * this.denominator;
        return Long.compare(ad, bc);
    }

    @Override
    public boolean isLessThan(Rational other) {
        return Math.multiplyExact(this.numerator, other.denominator) < Math.multiplyExact(other.numerator, this.denominator);
    }


    @Override // Absolutable impls
    public Rational abs() {
        return new Rational(Math.abs(numerator), denominator);
    }

    @Override
    public int signum() {
        return Long.signum(numerator);
    }


    @Override // Exponentiable impls
    public Rational power(int exponent)
    {
    	RationalField field = RationalField.INSTANCE;
        if (field.isZero(this) && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }
        if (exponent == 0) { return field.one(); }

        Rational base = this;
        int exp = Math.abs(exponent);
        Rational result = field.one();

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


    @Override // Java Standard impls
    public String toString()
    {
        if (denominator == 1) {
        	return String.valueOf(numerator);
        }

        return numerator + "/" + denominator;
    }

    @Override
    public final boolean equals(Object other) 
    {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Rational)) {
            return false;
        }

        Rational rational = (Rational) other;
        return this.numerator == rational.numerator && this.denominator == rational.denominator;
    }

    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(numerator, denominator);
    }
}
