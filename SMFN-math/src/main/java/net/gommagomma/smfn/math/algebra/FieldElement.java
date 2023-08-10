/*
 * FieldElement.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * Represent an element of a field.
 * @author gommagomma.net
 * @param <E> - the type of field element
 */
public interface FieldElement<E>
{
	/**
	 * Return the modulus of the element.
	 * @return the modulus of the element
	 */
	public double modulus();
}
