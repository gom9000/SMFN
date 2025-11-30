package net.gommagomma.smfn.math.analysis.solvers.ode;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.models.DynamicSystem;
import net.gommagomma.smfn.math.analysis.solvers.core.IntervalSolver;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;


public interface ODESolver<K extends FieldElement<K>, T extends VectorElement<K, T>>
extends IntervalSolver<K, T>
{
    // Esegue un singolo passo di integrazione temporale.
	T step(DynamicSystem<K, T> system, T currentState, Real currentTime, Real deltaTime);
}
