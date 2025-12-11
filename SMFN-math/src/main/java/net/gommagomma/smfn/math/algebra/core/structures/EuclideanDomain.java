package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.EuclideanDomainElement;

public interface EuclideanDomain<E extends EuclideanDomainElement<E, N>, N extends ComparableElement<N>> 
extends CommutativeRing<E>
{}
