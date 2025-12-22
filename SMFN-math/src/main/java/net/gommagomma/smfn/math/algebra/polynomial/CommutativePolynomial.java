package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.TreeMap;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.polynomial.core.AbstractRingPolynomial;

public final class CommutativePolynomial<K extends CommutativeRingElement<K>> 
extends AbstractRingPolynomial<K, CommutativePolynomial<K>> 
implements CommutativeRingElement<CommutativePolynomial<K>>
{
    public CommutativePolynomial(TreeMap<Integer, K> coefficients, Semiring<K> structure) {
        super(coefficients, structure);
    }

    @Override
    protected CommutativePolynomial<K> create(TreeMap<Integer, K> newCoeffs) {
        return new CommutativePolynomial<>(newCoeffs, structure);
    }

    @Override
    public CommutativePolynomial<K> copy() {
        TreeMap<Integer, K> clonedCoeffs = new TreeMap<>();
        this.coefficients.forEach((deg, val) -> clonedCoeffs.put(deg, val.copy()));
        return new CommutativePolynomial<>(clonedCoeffs, structure);
    }

    @Override
    public CommutativePolynomial<K> getOne() {
        TreeMap<Integer, K> map = new TreeMap<>();
        map.put(0, structure.one());
        return create(map);
    }

    /**
     * Esegue la divisione euclidea. Richiede che i coefficienti siano FieldElements.
     */
    @SuppressWarnings("unchecked")
    public PolynomialQuotientRemainder<K, CommutativePolynomial<K>> euclideanDivide(CommutativePolynomial<K> divisor) {
        if (divisor.isZero()) throw new ArithmeticException("Division by zero polynomial.");
        
        K zeroK = structure.zero();
        if (!(zeroK instanceof FieldElement)) {
             throw new UnsupportedOperationException("Euclidean division requires Field coefficients (to invert the leading coefficient).");
        }

        CommutativePolynomial<K> quotient = getZero();
        CommutativePolynomial<K> remainder = this;
        
        K divisorLeadCoeff = divisor.getCoefficient(divisor.degree());
        K invDivisorLead = (K) ((FieldElement<?, ?>) divisorLeadCoeff).inverse(); 

        while (!remainder.isZero() && remainder.degree() >= divisor.degree()) {
            int degDiff = remainder.degree() - divisor.degree();
            K leadCoeffRem = remainder.getCoefficient(remainder.degree());
            
            K factor = leadCoeffRem.multiply(invDivisorLead);
            
            TreeMap<Integer, K> termMap = new TreeMap<>();
            termMap.put(degDiff, factor);
            CommutativePolynomial<K> term = create(termMap);
            
            quotient = quotient.add(term);
            remainder = remainder.add(term.multiply(divisor).negate());
        }
        
        return new PolynomialQuotientRemainder<>(quotient, remainder);
    }
}