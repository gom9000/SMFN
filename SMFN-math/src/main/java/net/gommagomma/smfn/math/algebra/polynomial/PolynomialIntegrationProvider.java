package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.SymbolicIntegrationProvider;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Meccanismo esterno che sa calcolare l'integrale simbolico indefinito di un
 * Polynomial<K>, data una costante di integrazione.
 *
 * Richiede un Field (non solo un Ring) perch la formula a_i / (i+1) implica
 * una divisione: senza inverso moltiplicativo l'integrazione simbolica di un
 * polinomio non  definibile in generale (es. non ha senso su un anello puro
 * come i polinomi a coefficienti interi).
 */
public class PolynomialIntegrationProvider<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K> & NumericFactory<K>>
implements SymbolicIntegrationProvider<K, Polynomial<K>>
{
	private final S scalarStructure;
	private final PolynomialRing<K, S> polynomialRing;

	public PolynomialIntegrationProvider(S scalarStructure) {
		this.scalarStructure = scalarStructure;
		this.polynomialRing = new PolynomialRing<>(scalarStructure);
	}

	@Override
	public Polynomial<K> integrate(Polynomial<K> p, K constant) {
		int oldDegree = p.degree();
		List<K> newCoeffs = new ArrayList<>(oldDegree + 2);
		newCoeffs.add(constant);

		for (int i = 0; i <= oldDegree; i++) {
			K ai = p.getCoefficient(i);
			K divisor = scalarStructure.of(i + 1);
			newCoeffs.add(scalarStructure.divide(ai, divisor));
		}

		return polynomialRing.of(newCoeffs);
	}
}
