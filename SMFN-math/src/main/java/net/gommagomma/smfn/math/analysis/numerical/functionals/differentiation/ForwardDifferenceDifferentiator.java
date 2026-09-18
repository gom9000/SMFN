package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

/**
 * Differenziatore numerico a differenze in avanti: f'(x) ~ (f(x+h) - f(x)) / h.
 */
public class ForwardDifferenceDifferentiator<K extends ScalarElement<K>>
implements Mapping<Mapping<K, K>, Mapping<K, K>>
{
	private final Field<K> field;
	private final K h;

	public ForwardDifferenceDifferentiator(Field<K> field, K h) {
		if (h == null || field.isZero(h)) {
			throw new IllegalArgumentException("Il passo h non puo' essere nullo o zero.");
		}
		this.field = field;
		this.h = h;
	}

	@Override
	public Mapping<K, K> apply(Mapping<K, K> f) {
		return x -> {
			K fXPlusH = f.apply(field.add(x, h));
			K fX = f.apply(x);
			return field.divide(field.subtract(fXPlusH, fX), h);
		};
	}
}
