package net.gommagomma.smfn.math.analysis.core.operators;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
 * Un operatore simbolico trasforma un Morfismo in un altro Morfismo.
 * Esempio: Derivata (prende un Morfismo, restituisce un Morfismo).
 */
public interface SymbolicOperator<I, O, F extends Mapping<I, O>> 
extends Operator<F, F>
{
    @Override
    F transform(F input); 
}
