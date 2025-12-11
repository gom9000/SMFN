package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;
import net.gommagomma.smfn.math.linearalgebra.core.operators.HermitianOperator;


/**
 * Un Osservabile è, per definizione fisica, un operatore hermitiano.
 * Questa interfaccia funge da marker semantico per il dominio della fisica.
 */
public interface Observable<K extends FieldElement<K, ?>, 
                            V extends VectorElement<K, V>, 
                            O extends Observable<K, V, O>> 
extends HermitianOperator<K, V, O>
{}
