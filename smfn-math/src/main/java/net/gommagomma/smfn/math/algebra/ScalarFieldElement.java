/*
 * ScalarFieldElement.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * Represent an element of a scalar field.
 * @author gommagomma.net
 * @param <E> - the type of scalar field element
 */
public interface ScalarFieldElement<E>
extends FieldElement<E>
{
	/**
	 * Return the a^power of this scalar.
     * @param power the integer power
	 * @return a new scalar calculated as the a^power of this scalar
	 */
	public E power(int power);

	/**
	 * Return the square root of this scalar.
	 * @return a new scalar calculated as the square root of this scalar
	 */
	public E square();
}
