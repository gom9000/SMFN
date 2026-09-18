package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Funzione da K^n a K^m: Mapping<Vector<K>,Vector<K>>, non Operator<Vector<K>>
 * -- Vector<K> non porta la dimensione nel tipo, e n puo' differire da m
 * (es. una Matrix rettangolare). Forzare Operator qui promettarebbe una
 * composizione/iterazione che non sempre ha senso dimensionale.
 */
public interface VectorFunction<K extends ScalarElement<K>>
extends Mapping<Vector<K>, Vector<K>>
{}
