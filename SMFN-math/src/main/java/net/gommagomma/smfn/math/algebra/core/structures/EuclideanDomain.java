package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Orderable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.EuclideanDomainElement;

public interface EuclideanDomain<E extends EuclideanDomainElement<E, N>, N extends Orderable<N>> 
extends CommutativeRing<E>
{
	E quotient(E a, E b);
	E remainder(E a, E b);
}
