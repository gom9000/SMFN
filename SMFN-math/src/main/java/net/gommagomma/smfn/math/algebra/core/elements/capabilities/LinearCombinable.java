package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;

public interface LinearCombinable<K, E extends LinearCombinable<K, E>> 
extends Scalable<K, E>, AbelianGroupElement<E>
{
	default E linearCombine(K a, E other, K b) {
        return this.scale(a).add(other.scale(b));
    }
}
