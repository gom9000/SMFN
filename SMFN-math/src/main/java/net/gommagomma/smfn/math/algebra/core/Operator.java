package net.gommagomma.smfn.math.algebra.core;

/**
 * L'astrazione più alta per qualsiasi trasformazione che accetta un input e produce un output.
 * Base per gli Operatori Funzionali e gli Operatori Lineari.
 *
 * @param <I> Il tipo di input (Dominio).
 * @param <O> Il tipo di output (Codominio).
 */
public interface Operator<I, O>
{
    O apply(I input);
}
