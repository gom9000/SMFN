package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * apply(P) rappresenta un residuo da annullare: si cerca P tale che apply(P) = 0.
 */
public interface RootFindingProblem<P extends AlgebraicElement<P>>
extends IterationProblem<P>
{}
