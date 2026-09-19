package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.utils.MathUtils;

/**
 * Rappresenta un numero razionale esatto sotto forma di frazione irriducibile (n/d), 
 * basato su valori numerici a 64 bit (long) per numeratore e denominatore. 
 * Il denominatore è sempre mantenuto positivo normalizzando il segno sul numeratore.
 */
public final class Rational
implements ExactElement<Rational>, Normable<Real>, Orderable<Rational>, Absolutable<Rational>, Exponentiable<Rational>
{
    private final long numerator;
    private final long denominator;

    /**
     * Costruisce un numero razionale semplificato a partire da numeratore e denominatore.
     * Il segno viene normalizzato affinché il denominatore sia sempre positivo e la frazione 
     * viene ridotta ai minimi termini tramite il massimo comun divisore (MCD).
     * 
     * @param numerator il numeratore
     * @param denominator il denominatore
     * @throws IllegalArgumentException se il denominatore è zero
     */
    public Rational(long numerator, long denominator)
    {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }

        // denominator is always positive, the sign is moved to the numerator
        if (denominator < 0) {
        	if (numerator == Long.MIN_VALUE || denominator == Long.MIN_VALUE) {
                throw new ArithmeticException("Impossibile rappresentare questo razionale: negare Long.MIN_VALUE causa overflow.");
            }
            numerator = -numerator;
            denominator = -denominator;
        }

        long gcd = MathUtils.greatestCommonDivisor(numerator, denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    /**
     * Costruisce un numero razionale rappresentante un numero intero (con denominatore pari a 1).
     * 
     * @param value il valore intero long
     */
    public Rational(long value)
    {
        this(value, 1);
    }

    /**
     * Restituisce il numeratore della frazione ridotta.
     * 
     * @return il valore del numeratore
     */
    public long getNumerator() { return numerator; }

    /**
     * Restituisce il denominatore della frazione ridotta (sempre positivo).
     * 
     * @return il valore del denominatore
     */
    public long getDenominator() {  return denominator; }

    @Override // ScalarElement impls
    public ScalarStructure<Rational> getStructure() {
        return RationalField.INSTANCE;
    }

    @Override // AlgebraicElement impls
    public Rational copy() { return new Rational(numerator, denominator); }

    @Override // Normable
    public Real norm() {
    	if (numerator == Long.MIN_VALUE) {
            throw new ArithmeticException("Cannot compute the norm: numerator is Long.MIN_VALUE, whose absolute value overflows a long.");
        }
        return new Real((double) Math.abs(numerator) / denominator);
    }

    @Override // Orderable impls
    public int compareTo(Rational other)
    {
    	long ad = Math.multiplyExact(this.numerator, other.denominator);
        long bc = Math.multiplyExact(other.numerator, this.denominator);
        return Long.compare(ad, bc);
    }

    @Override
    public boolean isLessThan(Rational other) {
        return Math.multiplyExact(this.numerator, other.denominator) < Math.multiplyExact(other.numerator, this.denominator);
    }

    @Override // Absolutable impls
    public Rational abs() {
    	if (numerator == Long.MIN_VALUE) {
            throw new ArithmeticException("Impossibile calcolare il valore assoluto di Long.MIN_VALUE come long.");
        }
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
        long exp = Math.abs((long)exponent); // evita l'overflow di Math.abs(Integer.MIN_VALUE)
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