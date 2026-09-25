package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.AbelianGroup;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;

/**
 * Rappresenta un modulo (modulo su un anello), una generalizzazione di uno
 * spazio vettoriale in cui gli scalari formano un anello anziché un campo 
 * e l'addizione costituisce un gruppo abeliano.
 *
 * @param <V> il tipo dell'elemento lineare (vettore o elemento del modulo)
 * @param <K> il tipo dello scalare appartenente all'anello
 * @param <S> il tipo della struttura scalare basata su anello
 */
public interface Module<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> 
extends Semimodule<V, K, S>, AbelianGroup<V>
{}
