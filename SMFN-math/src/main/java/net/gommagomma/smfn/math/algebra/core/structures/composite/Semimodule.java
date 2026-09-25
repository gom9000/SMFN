package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.AdditiveMonoid;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

/**
 * Rappresenta un semimodulo, una generalizzazione di uno spazio vettoriale in cui gli scalari 
 * formano un semianello anziché un campo e l'addizione costituisce un monoide.
 *
 * @param <V> il tipo dell'elemento lineare
 * @param <K> il tipo dello scalare
 * @param <S> il tipo del semirinco scalare
 */
public interface Semimodule<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> 
extends LinearStructure<V, K, S>, AdditiveMonoid<V>
{}
