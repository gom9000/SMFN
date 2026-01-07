package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
 * Specializzazione per la valutazione (es. Horner).
 */
public interface EvaluationFunctional<F extends Mapping<I, K>, I, K> 
extends Functional<F, I, K>
{}
