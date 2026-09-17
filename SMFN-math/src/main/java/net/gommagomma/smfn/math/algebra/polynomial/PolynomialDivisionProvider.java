package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.InvertibleElements;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Divisione polinomiale (quoziente + resto) per scalari K non necessariamente
 * commutativi -- richiede solo Ring<K> & InvertibleElements<K>, non Field<K>.
 *
 * A differenza di EuclideanPolynomialRing, questa classe NON offre gcd()/lcm():
 * per un anello non commutativo (es. SquareMatrix) un resto intermedio potrebbe
 * avere un coefficiente di testa non invertibile anche se il divisore di
 * partenza lo era, e l'algoritmo di Euclide per il GCD non potrebbe procedere.
 * Offre solo un singolo passo di divisione, la cui validita' e' verificata
 * (isInvertible), non assunta a priori come farebbe un Field.
 *
 * Convenzione fissata: il quoziente moltiplica sempre il divisore a SINISTRA
 * (dividendo = quoziente * divisore + resto). Verificarlo sempre in questo
 * verso, mai nel verso opposto -- per K non commutativo i due differiscono.
 */
public class PolynomialDivisionProvider<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K> & InvertibleElements<K>>
{
	private final S scalarStructure;
	private final PolynomialRing<K, S> polynomialRing;

	public PolynomialDivisionProvider(S scalarStructure) {
		this.scalarStructure = scalarStructure;
		this.polynomialRing = new PolynomialRing<>(scalarStructure);
	}

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
