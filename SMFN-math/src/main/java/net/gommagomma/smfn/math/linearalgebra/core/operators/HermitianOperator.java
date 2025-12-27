package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.LinearCombinable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

public interface HermitianOperator<K extends FieldElement<K>, V extends LinearCombinable<K, V>, O extends HermitianOperator<K, V, O>> 
extends LinearOperator<K, V, O>, HermitianMapping<K, V, O>
{}
