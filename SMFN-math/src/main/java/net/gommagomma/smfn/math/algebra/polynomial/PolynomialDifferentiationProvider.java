package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.SymbolicDifferentiationProvider;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Meccanismo esterno che sa calcolare la derivata simbolica di un Polynomial<K>.
 *
 * Tenuto deliberatamente separato da PolynomialRing: quella classe rappresenta
 * solo gli assiomi d'anello (add/multiply/negate/zero/one), mentre questa
 * rappresenta una capacit di calcolo simbolico che si appoggia a quegli assiomi
 * ma non ne fa parte concettualmente.
 *
 * Il vincolo S extends ... & NumericFactory<K> rende impossibile costruire un
 * provider per una struttura che non sa fabbricare scalari da interi: l'errore
 * si scopre a compile-time, non pi a runtime.
 */
public class PolynomialDifferentiationProvider<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K> & NumericFactory<K>>
implements SymbolicDifferentiationProvider<K, Polynomial<K>>
{
	private final S scalarStructure;
	private final PolynomialRing<K, S> polynomialRing;

	public PolynomialDifferentiationProvider(S scalarStructure) {
		this.scalarStructure = scalarStructure;
		this.polynomialRing = new PolynomialRing<>(scalarStructure);
	}

	@Override
	public Polynomial<K> derivative(Polynomial<K> p) {
		if (p.degree() <= 0) {
			return polynomialRing.zero();
		}

		List<K> derivCoeffs = new ArrayList<>(p.degree());
		for (int i = 1; i <= p.degree(); i++) {
			K nAsScalar = scalarStructure.of(i);
			derivCoeffs.add(scalarStructure.multiply(nAsScalar, p.getCoefficient(i)));
		}

		return polynomialRing.of(derivCoeffs);
	}
}
