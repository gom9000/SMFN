/*
 * AlgebraicElement.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.core.algebra;


/**
 * The base interface for all elements within an algebraic structure (e.g., Natural, Rational, Complex numbers).
 * It defines fundamental operations that every element must support, such as comparison and copying.
 *
 * @param <E> The type extending this interface, ensuring type safety in operations (Curiously Recurring Template Pattern).
 * @author gommagomma.net
 */
public interface AlgebraicElement<E extends AlgebraicElement<E>>
{
	/**
	 * Checks if this element is mathematically equal to another element of the same type.
	 * This should provide mathematical equality (e.g., 1/2 == 2/4), distinct from Java's object equality (== or equals()).
	 * 
	 * @param other The other element to compare against.
	 * @return true if the elements are mathematically equal, false otherwise.
	 */
	boolean isEqual(E other);

	/**
	 * Creates an independent, deep copy of this element.
	 * Since elements in mathematical structures are often immutable, this might return a new instance with the same values.
	 * 
	 * @return A new, identical instance of the element.
	 */
	E copy();    
}
