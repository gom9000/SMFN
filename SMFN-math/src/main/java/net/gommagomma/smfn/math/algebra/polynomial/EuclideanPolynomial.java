package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.EuclideanDomainElement;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractRingPolynomial;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

/**
 * Polinomio su un Campo. Implementa formalmente il Dominio Euclideo.
 * K deve essere un FieldElement.
 */
public final class EuclideanPolynomial<K extends FieldElement<K>> 
extends AbstractRingPolynomial<K, EuclideanPolynomial<K>>
implements EuclideanDomainElement<EuclideanPolynomial<K>, Natural> 
{
    public EuclideanPolynomial(TreeMap<Integer, K> coefficients, Semiring<K> structure) {
        super(coefficients, structure);
    }

    @Override
    protected EuclideanPolynomial<K> create(TreeMap<Integer, K> newCoeffs) {
        return new EuclideanPolynomial<>(newCoeffs, structure);
    }

    @Override
    public Natural normValue() {
        return new Natural(degree());
    }

    @Override
    public EuclideanPolynomial<K> remainder(EuclideanPolynomial<K> divisor) {
        return euclideanDivide(divisor).remainder();
    }

    @Override
    public EuclideanPolynomial<K> quotient(EuclideanPolynomial<K> divisor) {
        return euclideanDivide(divisor).quotient();
    }

    @Override
    public EuclideanPolynomial<K> copy() {
        TreeMap<Integer, K> clonedCoeffs = new TreeMap<>();
        this.coefficients.forEach((deg, val) -> clonedCoeffs.put(deg, val.copy()));
        return new EuclideanPolynomial<>(clonedCoeffs, structure);
    }

    @Override
    public EuclideanPolynomial<K> getOne() {
        TreeMap<Integer, K> map = new TreeMap<>();
        map.put(0, structure.one());
        return create(map);
    }

    /**
     * Divisione Euclidea garantita dalla presenza di un Campo di coefficienti.
     */
    public PolynomialQuotientRemainder<K, EuclideanPolynomial<K>> euclideanDivide(EuclideanPolynomial<K> divisor) {
        if (divisor.isZero()) throw new ArithmeticException("Division by zero polynomial.");

        EuclideanPolynomial<K> quotient = getZero();
        EuclideanPolynomial<K> remainder = this;
        
        K divisorLeadCoeff = divisor.getCoefficient(divisor.degree());
        K invDivisorLead = divisorLeadCoeff.inverse(); // Sicuro: K è un FieldElement

        while (!remainder.isZero() && remainder.degree() >= divisor.degree()) {
            int degDiff = remainder.degree() - divisor.degree();
            K leadCoeffRem = remainder.getCoefficient(remainder.degree());
            
            K factor = leadCoeffRem.multiply(invDivisorLead);
            
            TreeMap<Integer, K> termMap = new TreeMap<>();
            termMap.put(degDiff, factor);
            EuclideanPolynomial<K> term = create(termMap);
            
            quotient = quotient.add(term);
            remainder = remainder.add(term.multiply(divisor).negate());
        }
        
        return new PolynomialQuotientRemainder<>(quotient, remainder);
    }

    public EuclideanPolynomial<K> normalize() {
        if (isZero()) return this;
        K leading = getCoefficient(degree());
        // Dividi ogni coefficiente per il leading coefficient
        // (o moltiplica per l'inverso)
        TreeMap<Integer, K> normalizedMap = new TreeMap<>();
        K invLeading = leading.inverse();
        coefficients.forEach((deg, val) -> normalizedMap.put(deg, val.multiply(invLeading)));
        return create(normalizedMap);
    }
}