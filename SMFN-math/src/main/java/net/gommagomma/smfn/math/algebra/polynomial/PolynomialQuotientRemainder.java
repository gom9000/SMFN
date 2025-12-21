package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractPolynomial;

/**
 * Contenitore immutabile per il risultato della Divisione Euclidea:
 * P(x) = Q(x) * D(x) + R(x)
 */
public final class PolynomialQuotientRemainder<K extends SemiringElement<K>>
{    
    private final AbstractPolynomial<K, ?> quotient;
    private final AbstractPolynomial<K, ?> remainder;

    public PolynomialQuotientRemainder(AbstractPolynomial<K, ?> quotient, AbstractPolynomial<K, ?> remainder) {
        this.quotient = Objects.requireNonNull(quotient);
        this.remainder = Objects.requireNonNull(remainder);
    }

    public AbstractPolynomial<K, ?> quotient() {
        return quotient;
    }

    public AbstractPolynomial<K, ?> remainder() {
        return remainder;
    }

    // Standard Java methods for immutability and value equality

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolynomialQuotientRemainder<?> that = (PolynomialQuotientRemainder<?>) o;
        return quotient.equals(that.quotient) && remainder.equals(that.remainder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quotient, remainder);
    }

    @Override
    public String toString() {
        return "Quotient: " + quotient + ", Remainder: " + remainder;
    }
}