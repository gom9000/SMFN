package net.gommagomma.smfn.math.core.linearalgebra.operators;

import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.linearalgebra.elements.VectorElement;


/**
 * Rappresenta un operatore di proiezione. Un tipo speciale di operatore lineare.
 */
public interface ProjectionOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, P extends ProjectionOperator<K, V, P>>
extends LinearOperator<K, V, P> 
{}
