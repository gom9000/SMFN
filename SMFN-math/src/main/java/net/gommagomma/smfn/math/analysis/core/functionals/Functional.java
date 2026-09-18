package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
 * Riduce una funzione e un input a un valore di output (scalare).
 *
 * Non estende Mapping<F,K>: e' genuinamente binaria (funzione, punto) -> K,
 * non un'applicazione a un solo argomento -- estendere Mapping qui prometteva
 * un apply(F):K che nessuna implementazione forniva mai.
 */
public interface Functional<F extends Mapping<I, K>, I, K>
{
	K evaluate(F function, I point);
}
