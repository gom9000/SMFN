package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.AdditiveMonoid;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

public interface Semimodule<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> 
extends LinearStructure<V, K, S>, AdditiveMonoid<V>
{}
