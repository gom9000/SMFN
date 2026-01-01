package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface LinearStructure<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends ScalarStructure<K>> 
extends CompositeStructure<K, V, S>
{
    V scale(K scalar, V vector);
}
