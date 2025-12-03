/*
 * AdditiveMonoidElement.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra.core.elements.additive;


import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;


/**
 * Represents an element in a Monoid under addition. 
 * Requires an associative binary operation (addition) and usually an additive identity (zero).
 * 
 * @param <E> The type extending this interface.
 */
public interface AdditiveMonoidElement<E extends AdditiveMonoidElement<E>>
extends AlgebraicElement<E>
{
	/**
     * Performs the addition operation with another element.
     * @param other The element to add.
     * @return The sum as a new element instance.
     */
    E add(E other);
    E getZero();
    default boolean isZero() { return isMathematicallyEqualTo(getZero()); }
}
