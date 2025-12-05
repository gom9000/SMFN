package net.gommagomma.smfn.math.algebra.core.elements.tensors;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


/**
 * Interfaccia fondamentale per tutti gli elementi strutturati N-dimensionali (Tensori),
 * di rango 1 o superiore. Incapsula le proprietà strutturali e l'accesso ai componenti.
 *
 * @param <K> Il tipo di scalare che compone il Tensore (il Semianello o Anello sottostante).
 */
public interface TensorElement<K extends SemiringElement<K>>
{
    /**
     * Restituisce il rango (o l'ordine/grado) del Tensore (N).
     * Esempio: Rango 1 = Vettore, Rango 2 = Matrice.
     * @return Il rango del tensore.
     */
    int getRank();

    /**
     * Restituisce la 'shape' del Tensore, ovvero un array di interi che
     * specifica la dimensione di ciascun asse.
     * Esempio: Vettore di dimensione 5 -> [5]
     * Esempio: Matrice 2x3 -> [2, 3]
     * @return L'array delle dimensioni.
     */
    int[] getShape();

    /**
     * Restituisce il numero totale di elementi scalari contenuti nel Tensore.
     * Equivale al prodotto degli elementi di getShape().
     * @return Il numero totale di scalari (long per futuri tensori molto grandi).
     */
    long size();

    /**
     * Restituisce l'elemento scalare K nella posizione specificata.
     * Il numero di indici deve corrispondere al rango del Tensore.
     * @param indices Gli indici (coordinate) del componente desiderato.
     * @return Lo scalare K nella posizione indicata.
     * @throws IllegalArgumentException se il numero di indici non corrisponde al rango.
     * @throws IndexOutOfBoundsException se gli indici non sono validi.
     */
    K get(int... indices);
}