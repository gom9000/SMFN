package net.gommagomma.smfn.math.algebra.core;

public interface Morphism<I, O>
extends Mapping<I, O>
{
	O evaluate(I input);

    @Override
    default O apply(I input) {
        return evaluate(input);
    }
}
