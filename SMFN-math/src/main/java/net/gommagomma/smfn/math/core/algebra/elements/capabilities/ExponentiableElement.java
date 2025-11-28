package net.gommagomma.smfn.math.core.algebra.elements.capabilities;


import net.gommagomma.smfn.math.core.algebra.AlgebraicElement;


public interface ExponentiableElement<E extends ExponentiableElement<E>>
extends AlgebraicElement<E>
{
    /**
     * Raises this element to the power of the given integer exponent.
     * 
     * @param exponent The integer exponent (can be negative if it's a FieldElement).
     * @return The result of the exponentiation.
     * @throws ArithmeticException if the operation is undefined (e.g., 0^-1).
     */
    E power(int exponent);
}
