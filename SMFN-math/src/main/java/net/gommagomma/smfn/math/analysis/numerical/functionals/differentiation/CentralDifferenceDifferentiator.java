package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

/**
 * Differenziatore numerico a differenze centrali: f'(x) ~ (f(x+h) - f(x-h)) / (2h).
 *
 * E' un SymbolicOperator<K,K,Mapping<K,K>>: trasforma una funzione in
 * un'altra funzione -- esattamente il concetto gia' pulito in
 * analysis.core.operators, non serviva nessun modello a parte.
 */
public class CentralDifferenceDifferentiator<K extends ScalarElement<K>>
implements Mapping<Mapping<K, K>, Mapping<K, K>>
{
	private final Field<K> field;
	private final K h;

	public CentralDifferenceDifferentiator(Field<K> field, K h) {
		if (h == null || field.isZero(h)) {
			throw new IllegalArgumentException("Il passo h non puo' essere nullo o zero.");
		}
		this.field = field;
		this.h = h;
	}

	@Override
	public Mapping<K, K> apply(Mapping<K, K> f) {
		return x -> {
			K fPlus = f.apply(field.add(x, h));
			K fMinus = f.apply(field.subtract(x, h));
			K twoH = field.add(h, h);
			return field.divide(field.subtract(fPlus, fMinus), twoH);
		};
	}
}
