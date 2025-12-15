package net.gommagomma.smfn.math.analysis.core.operators;

import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.algebra.core.Operator;

/**
 * Operatore Simbolico. Trasforma una funzione F in una funzione R 
 * (es. derivata, primitiva).
 * F: Tipo della funzione in input.
 * R: Tipo della funzione in output.
 */
public interface SymbolicOperator<F extends MathFunction<?, ?>, R extends MathFunction<?, ?>> 
extends Operator<F, R>
{}
