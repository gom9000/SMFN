package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.AbelianGroup;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;

public interface Module<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> 
extends Semimodule<V, K, S>, AbelianGroup<V>
{}
