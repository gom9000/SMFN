package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface Absolutable<E extends AlgebraicElement<E>> 
{
	E abs();
	int signum();
}