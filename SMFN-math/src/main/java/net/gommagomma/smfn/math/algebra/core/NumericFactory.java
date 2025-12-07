package net.gommagomma.smfn.math.algebra.core;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


public interface NumericFactory<E extends SemiringElement<E>>
{
    /** Restituisce l'identità additiva (Zero) del number E. */
    E zero();

    /** Restituisce l'identità moltiplicativa (Uno) del number E. */
    E one();

    /** Crea un nuovo elemento E partendo da un primitivo double. */
    E of(double value);

    /** Crea un nuovo elemento E partendo da un primitivo long. */
    E of(long value);

    /** Crea un nuovo elemento E partendo da un primitivo int. */
    E of(int value);
}
