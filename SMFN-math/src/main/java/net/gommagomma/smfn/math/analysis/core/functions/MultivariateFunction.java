package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Funzione a piu' variabili: K^n -> K. E' semplicemente Mapping<Vector<K>,K>
 * -- nessun metodo proprio, la forma e' gia' tutta l'informazione.
 */
public interface MultivariateFunction<K extends ScalarElement<K>>
extends Mapping<Vector<K>, K>
{}
