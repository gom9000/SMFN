package net.gommagomma.smfn.math.analysis.solvers.core;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.models.DynamicSystem;
import net.gommagomma.smfn.math.linearalgebra.core.elements.VectorElement;


// K = Tipo scalare (FieldElement), V = Tipo vettore (VectorElement)
public interface IntervalSolver<K extends FieldElement<K>, V extends VectorElement<K, V>> 
extends Solver<V, V>
{
    V integrate(DynamicSystem<K, V> system, 
                V initialState, 
                Real startTime, 
                Real endTime, 
                IntegrationParameters params);
}
