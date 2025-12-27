package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.additive.CommutativeMonoidElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Scalable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;

/**
 * Rappresenta un morfismo tra spazi che supportano operazioni lineari.
 * È esso stesso un elemento di uno spazio vettoriale/modulo, permettendo
 * somme e scalature di funzioni.
 *
 * @param <K> Il tipo dello scalare (Semianello)
 * @param <V> Il tipo del dominio/codominio (deve essere scalabile e sommabile)
 */
public interface LinearMorphism<K extends SemiringElement<K>, V extends Scalable<K, V> & CommutativeMonoidElement<V>> 
extends Morphism<V, V>, CommutativeMonoidElement<LinearMorphism<K, V>>, Scalable<K, LinearMorphism<K, V>>
{
    @Override
    V evaluate(V x);

    @Override
    default V apply(V input) {
        return evaluate(input);
    }

    // --- Operazioni Algebriche (Puntuali) ---

    @Override
    default LinearMorphism<K, V> add(LinearMorphism<K, V> other) {
        // Ritorna una nuova funzione che è la somma punto a punto
        return x -> this.evaluate(x).add(other.evaluate(x));
    }

    @Override
    default LinearMorphism<K, V> scale(K scalar) {
        // Ritorna una nuova funzione scalata punto a punto
        return x -> this.evaluate(x).scale(scalar);
    }

    // --- Requisiti della Gerarchia Algebrica ---

    @Override
    default LinearMorphism<K, V> getZero() {
        // La funzione nulla: f(x) = 0 per ogni x
        return x -> x.getZero();
    }

    @Override
    default LinearMorphism<K, V> copy() {
        // Trattiamo le definizioni funzionali come immutabili
        return this;
    }

    @Override
    default boolean isMathematicallyEqualTo(LinearMorphism<K, V> other) {
        if (other == null) return false;
        if (this == other) return true;
        // L'uguaglianza tra funzioni non è decidibile algoritmicamente in generale
        throw new UnsupportedOperationException("Functional equality is not decidable.");
    }
}