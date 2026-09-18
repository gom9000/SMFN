package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Base neutra: fornisce una Mapping<P,P>, senza fissare come apply() vada
 * interpretato. RootFindingProblem e FixedPointProblem sono la stessa forma
 * sotto interpretazioni diverse (residuo da annullare, passo da iterare, o
 * -- come nei frattali -- orbita da osservare per divergenza) e vivono qui
 * sotto come specializzazioni di nome, non di struttura.
 */
public interface IterationProblem<P extends AlgebraicElement<P>>
extends AnalysisProblem<P>, Mapping<P, P>
{}
