package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

public interface IntervalSolver<K extends FieldElement<K>, R extends VectorElement<K, R>>
extends Solver<InitialValueProblem<K, R>, R>
{
    R integrate(InitialValueProblem<K, R> problem, Real endTime, IntegrationParameters params);
}
