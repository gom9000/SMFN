package net.gommagomma.smfn.math.algebra.core.elements.capabilities;


import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;


public interface Sqrtable<E extends Sqrtable<E>>
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