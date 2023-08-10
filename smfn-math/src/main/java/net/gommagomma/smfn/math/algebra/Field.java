/*
 * Field.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * A field is an algebraic structure on which addition, subtraction, multiplication
 * and division are defined.
 * @author gommagomma.net
 * @param <E> - the type of the field elements
 */
public interface Field<E>
extends AlgebraicStructure
{
	public E sum(E a, E b);
	public E mul(E a, E b);
	public E sub(E a, E b);
	public E div(E a, E b);
}
