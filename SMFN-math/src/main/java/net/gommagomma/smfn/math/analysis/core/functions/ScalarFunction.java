package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Operator;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Funzione a una variabile: K -> K. E' un Operator<K> (endofunzione, con
 * compose()/power() gratis): K -> K e' sempre un endomorfismo, non esiste
 * un caso "rettangolare" per uno scalare -- a differenza di VectorFunction.
 */
public interface ScalarFunction<K extends ScalarElement<K>>
extends Operator<K>
{}
