package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.InvertibleElements;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Provider per l'esecuzione della divisione lunga tra polinomi su anelli i cui coefficienti 
 * di testa del divisore sono invertibili. Restituisce sia il quoziente che il resto della divisione.
 * 
 * @param <K> il tipo degli elementi scalari (coefficienti)
 * @param <S> il tipo della struttura algebrica di supporto (anello, struttura scalare e gestione elementi invertibili)
 */
public class PolynomialDivisionProvider<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K> & InvertibleElements<K>>
{
    private final S scalarStructure;
    private final PolynomialRing<K, S> polynomialRing;

    /**
     * Costruisce un provider di divisione polinomiale basato sulla struttura scalare specificata.
     * 
     * @param scalarStructure la struttura algebrica dei coefficienti
     */
    public PolynomialDivisionProvider(S scalarStructure) {
        this.scalarStructure = scalarStructure;
        this.polynomialRing = new PolynomialRing<>(scalarStructure);
    }

    /**
     * Esegue l'algoritmo di divisione euclidea o generale tra un dividendo e un divisore polinomiale.
     * 
     * @param dividend il polinomio dividendo
     * @param divisor il polinomio divisore
     * @return un oggetto {@link PolynomialDivisionResult} contenente il quoziente e il resto
     * @throws ArithmeticException se il divisore è il polinomio zero o se il coefficiente di testa non è invertibile
     */
    public PolynomialDivisionResult<K> divide(Polynomial<K> dividend, Polynomial<K> divisor) {
        if (polynomialRing.isZero(divisor)) {
            throw new ArithmeticException("Divisione per il polinomio zero.");
        }

        K divisorLead = divisor.getCoefficient(divisor.degree());
        if (!scalarStructure.isInvertible(divisorLead)) {
            throw new ArithmeticException("Il coefficiente di testa del divisore non e' invertibile: " + divisorLead);
        }
        K invDivisorLead = scalarStructure.inverse(divisorLead);

        Polynomial<K> remainder = dividend;
        Polynomial<K> quotient = polynomialRing.zero();

        while (!polynomialRing.isZero(remainder) && remainder.degree() >= divisor.degree()) {
            K remainderLead = remainder.getCoefficient(remainder.degree());
            K factor = scalarStructure.multiply(remainderLead, invDivisorLead);
            int degDiff = remainder.degree() - divisor.degree();

            List<K> monomialCoeffs = new ArrayList<>(degDiff + 1);
            for (int i = 0; i < degDiff; i++) monomialCoeffs.add(scalarStructure.zero());
            monomialCoeffs.add(factor);
            Polynomial<K> monomial = polynomialRing.of(monomialCoeffs);

            quotient = polynomialRing.add(quotient, monomial);
            // Convenzione: quoziente a sinistra del divisore, sempre.
            Polynomial<K> subtrahend = polynomialRing.multiply(monomial, divisor);
            remainder = polynomialRing.subtract(remainder, subtrahend);
        }

        return new PolynomialDivisionResult<>(quotient, remainder);
    }
}