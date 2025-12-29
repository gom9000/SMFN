/*
 * SpaceElement.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.linearalgebra.core.elements.vectors;


import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;


/**
 * Represent an element of a field. 
 * @param <V> - the type of field element
 *
 * @author gommagomma.net
 */
public interface SpaceElement<V extends SpaceElement<V>>
extends AlgebraicElement<V>
{}
