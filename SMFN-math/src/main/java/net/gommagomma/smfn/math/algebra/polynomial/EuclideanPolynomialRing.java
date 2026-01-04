package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.SymbolicIntegrationProvider;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

public class EuclideanPolynomialRing<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> 
extends CommutativePolynomialRing<K, S> 
implements EuclideanDomain<Polynomial<K>, Natural>, SymbolicIntegrationProvider<Polynomial<K>, K>
{
	public EuclideanPolynomialRing(S scalarStructure) {
		super(scalarStructure);
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
	    if (scalarStructure.isOne(leadingCoeff)) return p;

	    K invLeading = scalarStructure.inverse(leadingCoeff);
	    List<K> normalizedCoeffs = new ArrayList<>();
	    for (K c : p.getCoefficients()) {
	        normalizedCoeffs.add(scalarStructure.multiply(c, invLeading));
	    }
	    return new Polynomial<>(this, scalarStructure, normalizedCoeffs);
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
	    K invDivisorLead = scalarStructure.inverse(divisorLeadCoeff);

	    while (!isZero(remainder) && remainder.degree() >= divisor.degree()) {
	        // 1. Calcola il grado e il coefficiente del termine del quoziente
	        int degDiff = remainder.degree() - divisor.degree();
	        K leadCoeffRem = remainder.getCoefficient(remainder.degree());
	        K factor = scalarStructure.multiply(leadCoeffRem, invDivisorLead);

	        // 2. Crea il monomio factor * x^degDiff
	        List<K> monoCoeffs = new ArrayList<>(Collections.nCopies(degDiff, scalarStructure.zero()));
	        monoCoeffs.add(factor);
	        Polynomial<K> monomial = new Polynomial<>(this, scalarStructure, monoCoeffs);

	        // 3. Aggiorna quoziente e resto
	        quotient = add(quotient, monomial);
	        
	        // Sottrazione: remainder = remainder - (monomial * divisor)
	        Polynomial<K> subtrahend = multiply(monomial, divisor);
	        remainder = subtract(remainder, subtrahend);
	    }

	    return new PolynomialDivisionResult<>(quotient, remainder);
	}


	@Override
	public Polynomial<K> integrate(Polynomial<K> p, K constant) {
        ScalarStructure<K> s = p.getScalarStructure();
        if (!(s instanceof Field) || !(s instanceof NumericFactory)) {
            throw new UnsupportedOperationException("Integration requires a Field that implements NumericFactory.");
        }

        Field<K> field = (Field<K>) s;
        NumericFactory<K> factory = (NumericFactory<K>) s;

        int oldDegree = p.degree();
        List<K> newCoeffs = new ArrayList<>(oldDegree + 2);
        newCoeffs.add(constant);
        
        // iIntegrazione dei termini esistenti: formula [a_i / (i+1)] * x^(i+1)
        for (int i = 0; i <= oldDegree; i++) {
            K ai = p.getCoefficient(i);
            K divisor = factory.of(i + 1);
            newCoeffs.add(field.divide(ai, divisor));
        }
        
        return new Polynomial<>(this, s, newCoeffs);
	}
}
