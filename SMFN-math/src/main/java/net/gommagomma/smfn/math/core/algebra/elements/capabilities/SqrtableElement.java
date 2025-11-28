package net.gommagomma.smfn.math.core.algebra.elements.capabilities;


import net.gommagomma.smfn.math.core.algebra.AlgebraicElement;


public interface SqrtableElement<E extends SqrtableElement<E>>
extends AlgebraicElement<E>
{
    /**
     * Calculates the principal square root of this element.
     * 
     * @return The square root.
     * @throws ArithmeticException if the operation results in an undefined number 
     *         within the current domain (e.g., sqrt(-1) in Reals).
     */
    E sqrt();
}