package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

import net.gommagomma.smfn.math.algebra.core.elements.additive.CommutativeMonoidElement;

//Un Semiring ha addizione (monoide commutativo) e moltiplicazione (monoide), 
//e la moltiplicazione distribuisce sull'addizione. Non ha inversi additivi.
public interface SemiringElement<E extends SemiringElement<E>> 
extends CommutativeMonoidElement<E>, MultiplicativeMonoidElement<E>
{}
