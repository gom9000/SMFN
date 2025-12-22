package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractPolynomial;

/**
 * Contenitore immutabile per il risultato della Divisione Euclidea:
 * P(x) = Q(x) * D(x) + R(x)
 */
public final class PolynomialQuotientRemainder<K extends SemiringElement<K>, P extends AbstractPolynomial<K, P>>
{    
	private final P quotient;
    private final P remainder;

    public PolynomialQuotientRemainder(P quotient, P remainder) {
        this.quotient = Objects.requireNonNull(quotient);
        this.remainder = Objects.requireNonNull(remainder);
    }

    public P quotient() { return quotient; }
    public P remainder() { return remainder; }

    // Standard Java methods for immutability and value equality

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolynomialQuotientRemainder)) return false;
        
        PolynomialQuotientRemainder<?, ?> that = (PolynomialQuotientRemainder<?, ?>) o;
        
        return Objects.equals(quotient, that.quotient) && 
               Objects.equals(remainder, that.remainder);
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