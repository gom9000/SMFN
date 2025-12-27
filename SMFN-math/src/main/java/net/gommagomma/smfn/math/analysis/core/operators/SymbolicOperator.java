package net.gommagomma.smfn.math.analysis.core.operators;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.Morphism;

/**
 * Un operatore simbolico trasforma un Morfismo in un altro Morfismo.
 * Esempio: Derivata (prende un Morfismo, restituisce un Morfismo).
 */
public interface SymbolicOperator<I, O, M extends Morphism<I, O>> 
extends Mapping<M, M>
{
    @Override
    M apply(M input); 
}
