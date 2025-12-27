package net.gommagomma.smfn.math.analysis.core.operators;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMapping;

public interface DifferentialOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends LinearMapping<K, V, M>> 
extends SymbolicOperator<V, V, M>
{}