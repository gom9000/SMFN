package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
* Riduce una funzione e un input a un valore di output (scalare).
*/
public interface Functional<F extends Mapping<I, K>, I, K>
{
	K evaluate(F function, I point);
}
