package net.gommagomma.smfn.math.analysis.differential;

import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.core.linearalgebra.elements.VectorElement;


public interface ODESolver<K extends FieldElement<K>, T extends VectorElement<K, T>>
{
    // Esegue un singolo passo di integrazione temporale.
	T step(DifferentialSystem<K, T> system, T currentState, Real currentTime, Real deltaTime);

    // Esegue l'integrazione completa su un intervallo di tempo.
	T integrate(DifferentialSystem<K, T> system, T initialState, Real startTime, Real endTime, Real deltaTime);
}
