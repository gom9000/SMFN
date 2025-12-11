package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;


/**
 * Rappresenta un operatore di proiezione. Un tipo speciale di operatore lineare.
 */
public interface ProjectionOperator<K extends FieldElement<K, ?>, V extends VectorElement<K, V>, P extends ProjectionOperator<K, V, P>>
extends LinearOperator<K, V, P> 
{}
