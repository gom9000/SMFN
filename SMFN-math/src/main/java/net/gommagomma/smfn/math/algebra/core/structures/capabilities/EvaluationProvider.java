package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

public interface EvaluationProvider<E, I, O>
{
	O evaluate(E element, I input);
}
