package net.gommagomma.smfn.math.algebra.core.elements;

/**
 * Rappresenta un elemento lineare (es. un vettore geometrico o un elemento di uno spazio vettoriale/modulo)
 * definito su un insieme di scalari di tipo K.
 *
 * @param <V> il tipo concreto dell'elemento lineare
 * @param <K> il tipo degli scalari sottostanti
 */
public interface LinearElement<V extends LinearElement<V, K>, K extends ScalarElement<K>> 
extends CompositeElement<K, V>
{}
