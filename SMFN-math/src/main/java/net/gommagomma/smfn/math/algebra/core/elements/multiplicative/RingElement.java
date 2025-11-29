package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;

//Anello: Addizione (Gruppo abeliano) + Moltiplicazione (Monoide)
public interface RingElement<E extends RingElement<E>> 
extends SemiringElement<E>, AbelianGroupElement<E>
{}
