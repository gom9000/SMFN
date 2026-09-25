package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

/**
 * Rappresenta un numero naturale esatto (non negativo), basato su un valore primitivo a 64 bit (long).
 * Implementa le interfacce per la gestione degli elementi esatti, l'ordinamento e l'elevamento a potenza.
 */
public final class Natural
implements ExactElement<Natural>, Orderable<Natural>, Exponentiable<Natural>
{
    private final long value;

    /**
     * Costruisce un numero naturale a partire da un valore intero a 64 bit.
     * 
     * @pram value il valore numerico long
     * @param value il valore numerico long
     * @throws IllegalArgumentException se il valore è negativo
     */
    public Natural(long value)
    {
        if (value < 0) {
            throw new IllegalArgumentException("Natural numbers cannot be negative.");
        }
        this.value = value;
    }

    /**
     * Restituisce il valore primitivo sottostante del numero naturale.
     * 
     * @return il valore long
     */
    public long getValue() { return value; }

    @Override // ScalarElement impls
    public ScalarStructure<Natural> getStructure() {
        return NaturalSemiring.INSTANCE;
    }

    @Override  // AlgebraicElement impls
    public Natural copy() { return new Natural(value); }

    @Override // Orderable impls
    public int compareTo(Natural other)
    {
        return Long.compare(this.value, other.value);
    }

    @Override
    public boolean isLessThan(Natural other) {
        return this.value < other.value;
    }

    @Override // Exponentiable impls
    public Natural power(int exponent)
    {
        NaturalSemiring semiring = NaturalSemiring.INSTANCE;
        if (semiring.isZero(this) && exponent < 0) {
            throw new ArithmeticException("Cannot raise zero to a negative power.");
        }
        if (exponent == 0 || this.value == 1L) { return semiring.one(); }
        if (exponent < 0) {
            throw new ArithmeticException("Cannot represent a negative power of " + this.value + " as a Natural number.");
        }

        long base = this.value;
        long result = 1;
        int exp = exponent;

        while (exp > 0) {
            if (exp % 2 == 1) {
                result = Math.multiplyExact(result, base);
            }
            base = Math.multiplyExact(base, base);
            exp /= 2;
        }
        return new Natural(result);
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

        if (!(other instanceof Natural)) {
            return false;
        }

        Natural natural = (Natural) other;
        return this.value == natural.value;
    }

    @Override // Java Standard impls
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }
}