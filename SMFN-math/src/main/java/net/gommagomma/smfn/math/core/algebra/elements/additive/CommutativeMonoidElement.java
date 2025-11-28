package net.gommagomma.smfn.math.core.algebra.elements.additive;


import net.gommagomma.smfn.math.core.algebra.Commutative;


//Monoide Additivo Commutativo (Abelian Monoid Element)
public interface CommutativeMonoidElement<E extends CommutativeMonoidElement<E>> 
extends AdditiveMonoidElement<E>, Commutative
{}
