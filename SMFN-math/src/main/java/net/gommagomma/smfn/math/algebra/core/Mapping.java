package net.gommagomma.smfn.math.algebra.core;

import java.util.Objects;

/**
 * Definisce l'applicazione matematica f: I -> O.
 * È l'astrazione per ogni legge di corrispondenza tra un dominio e un codominio.
 */
@FunctionalInterface
public interface Mapping<I, O>
{
	/**
     * Applica la legge di trasformazione.
     * @param input l'elemento del dominio
     * @return l'elemento corrispondente nel codominio
     */
    O apply(I input);


    /**
     * Composizione di applicazioni: (f * g)(x) = f(g(x))
     */
    default <V> Mapping<V, O> compose(Mapping<? super V, ? extends I> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }
}
