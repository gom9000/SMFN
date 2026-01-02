package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

public interface LinearSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> 
extends Module<V, K, S>
{}
