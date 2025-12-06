package net.gommagomma.smfn.math.algebra.core.elements.factory;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


public interface ScalarFactory<E extends SemiringElement<E>>
{
    /** Restituisce l'identità additiva (Zero) dello scalare E. */
    E getZeroScalar();

    /** Restituisce l'identità moltiplicativa (Uno) dello scalare E. */
    E getOneScalar();

    /** Crea un nuovo elemento E partendo da un primitivo double. */
    E createScalar(double value);

    /** Crea un nuovo elemento E partendo da un primitivo int. */
    E createScalar(int value);
}
