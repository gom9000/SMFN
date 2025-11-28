package net.gommagomma.smfn.math.core.algebra.elements.multiplicative;

import net.gommagomma.smfn.math.core.algebra.elements.additive.AbelianGroupElement;

//Anello: Addizione (Gruppo abeliano) + Moltiplicazione (Monoide)
public interface RingElement<E extends RingElement<E>> 
extends SemiringElement<E>, AbelianGroupElement<E>
{}
