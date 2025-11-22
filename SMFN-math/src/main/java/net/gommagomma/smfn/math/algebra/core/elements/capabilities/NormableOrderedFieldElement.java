package net.gommagomma.smfn.math.algebra.core.elements.capabilities;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


public interface NormableOrderedFieldElement<E extends NormableOrderedFieldElement<E>> 
extends FieldElement<E>, ComparableElement<E>, Normable<E, E>, Sqrtable<E>
{}