package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

public interface HermitianOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, O extends HermitianOperator<K, V, O>> 
extends LinearOperator<K, V, O>, HermitianMapping<K, V, O>
{}
