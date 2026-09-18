package net.gommagomma.smfn.math.algebra.core;

import java.util.Objects;

/**
 * Rappresenta un'applicazione matematica o mappa f: I -> O.
 * È l'astrazione fondamentale per qualsiasi legge di corrispondenza tra un dominio e un codominio.
 *
 * @param <I> il tipo del dominio (input)
 * @param <O> il tipo del codominio (output)
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
     * Esegue la composizione di morfismi: (f * g)(x) = f(g(x)).
     * 
     * @param <V> il tipo del dominio della funzione precedente
     * @param before la funzione da applicare prima di questa
     * @return un nuovo mapping che rappresenta la composizione
     */
    default <V> Mapping<V, O> compose(Mapping<? super V, ? extends I> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Restituisce il mapping identità f(x) = x.
     * 
     * @param <T> il tipo degli elementi
     * @return un mapping identità
     */
    static <T> Mapping<T, T> identity() {
        return (T t) -> t;
    }
}
