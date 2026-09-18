package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Specializzazione vettoriale (dimensione n): Vector<K> -> Vector<K>.
 */
public interface VectorRootFindingProblem<K extends ScalarElement<K>>
extends RootFindingProblem<Vector<K>>
{}
