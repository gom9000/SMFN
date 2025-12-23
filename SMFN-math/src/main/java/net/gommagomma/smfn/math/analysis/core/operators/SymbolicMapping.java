package net.gommagomma.smfn.math.analysis.core.operators;

import net.gommagomma.smfn.math.algebra.core.Mapping;

/**
 * Operatore Simbolico. Trasforma una funzione F in una funzione R 
 * (es. derivata, primitiva).
 * F: Tipo della funzione in input.
 * R: Tipo della funzione in output.
 */
public interface SymbolicMapping<F extends Mapping<?, ?>, R extends Mapping<?, ?>> 
extends Mapping<F, R>
{}
