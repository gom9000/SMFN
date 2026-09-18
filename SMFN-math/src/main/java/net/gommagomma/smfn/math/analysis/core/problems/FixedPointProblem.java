package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * apply(T) rappresenta il passo T_k+1 = G(T_k): si itera per osservare il
 * comportamento dell'orbita (convergenza a un punto fisso, divergenza, ciclo
 * -- come nell'iterazione di Mandelbrot/Julia). nextIteration e' solo un
 * alias leggibile di apply, stessa forma sotto: non promette nulla in piu'.
 */
public interface FixedPointProblem<T extends AlgebraicElement<T>>
extends IterationProblem<T>
{
	default T nextIteration(T current) { return apply(current); }
}
