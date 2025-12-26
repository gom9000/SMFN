package net.gommagomma.smfn.math.algebra.core;

public interface Morphism<I, O>
extends Mapping<I, O>
{
	default O evaluate(I input) {
        return apply(input);
    }
}