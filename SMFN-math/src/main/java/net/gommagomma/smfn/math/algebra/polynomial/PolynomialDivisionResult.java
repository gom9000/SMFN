package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public final class PolynomialDivisionResult<K extends ScalarElement<K>>
{   
    private final Polynomial<K> quotient;
    private final Polynomial<K> remainder;

    public PolynomialDivisionResult(Polynomial<K> quotient, Polynomial<K> remainder) {
        this.quotient = Objects.requireNonNull(quotient);
        this.remainder = Objects.requireNonNull(remainder);
    }

    public Polynomial<K> quotient() { return quotient; }
    public Polynomial<K> remainder() { return remainder; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolynomialDivisionResult)) return false;
        PolynomialDivisionResult<?> that = (PolynomialDivisionResult<?>) o;
        return Objects.equals(quotient, that.quotient) && Objects.equals(remainder, that.remainder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quotient, remainder);
    }

    @Override
    public String toString() {
        return String.format("PolynomialDivisionResult[Quotient=%s, Remainder=%s]", quotient, remainder);
    }
}
