package net.gommagomma.smfn.math.analysis.core.operators;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
 * Trasforma una funzione in un'altra funzione.
 */
public interface Operator<F extends Mapping<?, ?>, G extends Mapping<?, ?>>
extends Mapping<F, G>
{
	G transform(F input);

	@Override
    default G apply(F input) {
        return transform(input);
    }
}
