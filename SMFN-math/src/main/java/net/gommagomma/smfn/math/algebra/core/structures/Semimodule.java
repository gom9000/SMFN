package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface Semimodule<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> 
extends LinearStructure<V, K, S>, AdditiveMonoid<V>
{}
