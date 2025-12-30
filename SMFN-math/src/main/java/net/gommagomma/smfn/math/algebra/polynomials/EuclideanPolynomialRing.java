package net.gommagomma.smfn.math.algebra.polynomials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

public class EuclideanPolynomialRing<K extends ScalarElement<K>> 
extends CommutativePolynomialRing<K> 
implements EuclideanDomain<Polynomial<K>, Natural>
{
	protected final Field<K> kField;

	public EuclideanPolynomialRing(Field<K> kField) {
		super(kField);
		this.kField = kField;
	}


	// EuclideanDomain impls
	@Override
	public Polynomial<K> quotient(Polynomial<K> a, Polynomial<K> b) {
	    return divide(a, b).quotient();
	}

	@Override
	public Polynomial<K> remainder(Polynomial<K> a, Polynomial<K> b) {
	    return divide(a, b).remainder();
	}

	@Override
	public Natural degree(Polynomial<K> e) {
		return new Natural(Math.max(0, e.degree()));
	}

	@Override
	public Polynomial<K> normalize(Polynomial<K> p) {
	    if (isZero(p)) return p;
	    // Rende il polinomio monico dividendo tutti i coefficienti per il leading coefficient
	    K leadingCoeff = p.getCoefficient(p.degree());
	    if (kField.isOne(leadingCoeff)) return p;

	    K invLeading = kField.inverse(leadingCoeff);
	    List<K> normalizedCoeffs = new ArrayList<>();
	    for (K c : p.getCoefficients()) {
	        normalizedCoeffs.add(kField.multiply(c, invLeading));
	    }
	    return new Polynomial<>(kField, normalizedCoeffs);
	}


	// Helper per restituire entrambi (algoritmo di divisione lunga)
	public PolynomialDivisionResult<K> divide(Polynomial<K> dividend, Polynomial<K> divisor) {
	    if (isZero(divisor)) {
	        throw new ArithmeticException("Division by zero polynomial.");
	    }

	    Polynomial<K> quotient = zero();
	    Polynomial<K> remainder = dividend;

	    // Prendiamo il leading coefficient del divisore e ne calcoliamo l'inverso una volta sola
	    K divisorLeadCoeff = divisor.getCoefficient(divisor.degree());
	    K invDivisorLead = kField.inverse(divisorLeadCoeff);

	    while (!isZero(remainder) && remainder.degree() >= divisor.degree()) {
	        // 1. Calcola il grado e il coefficiente del termine del quoziente
	        int degDiff = remainder.degree() - divisor.degree();
	        K leadCoeffRem = remainder.getCoefficient(remainder.degree());
	        K factor = kField.multiply(leadCoeffRem, invDivisorLead);

	        // 2. Crea il monomio factor * x^degDiff
	        List<K> monoCoeffs = new ArrayList<>(Collections.nCopies(degDiff, kField.zero()));
	        monoCoeffs.add(factor);
	        Polynomial<K> monomial = new Polynomial<>(kField, monoCoeffs);

	        // 3. Aggiorna quoziente e resto
	        quotient = add(quotient, monomial);
	        
	        // Sottrazione: remainder = remainder - (monomial * divisor)
	        Polynomial<K> subtrahend = multiply(monomial, divisor);
	        remainder = subtract(remainder, subtrahend);
	    }

	    return new PolynomialDivisionResult<>(quotient, remainder);
	}
}
