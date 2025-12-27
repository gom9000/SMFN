package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

import net.gommagomma.smfn.math.algebra.core.elements.additive.CommutativeMonoidElement;

public interface SemiringElement<E extends SemiringElement<E>> 
extends CommutativeMonoidElement<E>, MultiplicativeMonoidElement<E>
{}
