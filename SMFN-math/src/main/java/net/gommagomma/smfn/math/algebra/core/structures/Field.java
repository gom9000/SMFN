package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


public interface Field<E extends FieldElement<E, N>, N extends ComparableElement<N>>
extends EuclideanDomain<E, N>
{}
