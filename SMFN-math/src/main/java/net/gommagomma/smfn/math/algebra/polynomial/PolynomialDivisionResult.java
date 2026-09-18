package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Rappresenta il risultato di un'operazione di divisione tra polinomi, 
 * incapsulando sia il quoziente che il resto risultanti.
 * 
 * @param <K> il tipo degli elementi scalari (coefficienti) dei polinomi
 */
public final class PolynomialDivisionResult<K extends ScalarElement<K>>
{   
    private final Polynomial<K> quotient;
    private final Polynomial<K> remainder;

    /**
     * Costruisce un nuovo contenitore per il risultato della divisione polinomiale.
     * 
     * @param quotient il polinomio quoziente (non nullo)
     * @param remainder il polinomio resto (non nullo)
     * @throws NullPointerException se il quoziente o il resto sono nulli
     */
    public PolynomialDivisionResult(Polynomial<K> quotient, Polynomial<K> remainder) {
        this.quotient = Objects.requireNonNull(quotient);
        this.remainder = Objects.requireNonNull(remainder);
    }

    /**
     * Restituisce il polinomio quoziente della divisione.
     * 
     * @p_aram il polinomio quoziente
     * @return il quoziente
     */
    public Polynomial<K> quotient() { return quotient; }

    /**
     * Restituisce il polinomio resto della divisione.
     * 
     * @return il resto
     */
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