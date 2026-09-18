package net.gommagomma.smfn.math.algebra.core.elements.tensors;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Interfaccia fondamentale per tutti gli elementi strutturati N-dimensionali (Tensori),
 * di rango 1 o superiore. Incapsula le proprietà strutturali e l'accesso ai componenti.
 * 
 * @param <E> Il tipo del tensore stesso
 * @param <K> Il tipo di scalare contenuto
 */
public interface TensorElement<T extends TensorElement<T, K>, K extends ScalarElement<K>> 
extends AlgebraicElement<T>
{
	/**
     * Restituisce il rango (numero di dimensioni o assi) del tensore.
     * 
     * @return il rango del tensore
     */
    int rank();

    /**
     * Restituisce la forma (shape) del tensore, ovvero la dimensione per ciascun asse.
     * 
     * @return un array di interi contenente le dimensioni del tensore
     */
    int[] getShape();

    /**
     * Restituisce il numero totale di elementi scalari contenuti nel tensore.
     * 
     * @return la dimensione complessiva (numero totale di componenti)
     */
    long size();

    /**
     * Recupera lo scalare situato in corrispondenza degli indici multidimensionali specificati.
     * 
     * @param indices la sequenza di indici per ciascuna dimensione
     * @return l'elemento scalare all'indirizzo specificato
     */
    K get(int... indices);
}