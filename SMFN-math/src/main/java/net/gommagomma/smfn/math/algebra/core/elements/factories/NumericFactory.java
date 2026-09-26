package net.gommagomma.smfn.math.algebra.core.elements.factories;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Factory per la generazione e l'istanziazione di elementi numerici scalari 
 * associati a una struttura algebrica.
 * 
 * @param <K> il tipo concreto dell'elemento scalare
 */
public interface NumericFactory<K extends ScalarElement<K>>
{
	/**
     * Restituisce l'elemento neutro rispetto all'addizione (lo zero algebrico).
     * 
     * @return lo scalare rappresentante lo zero
     */
    K zero();

    /**
     * Restituisce l'elemento neutro rispetto alla moltiplicazione (l'unità algebrica).
     * 
     * @return lo scalare rappresentante l'uno
     */
    K one();

    /**
     * Crea un elemento scalare a partire da un valore in virgola mobile a doppia precisione.
     * 
     * @param value il valore numerico double
     * @return lo scalare corrispondente
     */
    K of(double value);

    /**
     * Crea un elemento scalare a partire da un valore intero a 64 bit.
     * 
     * @param value il valore numerico long
     * @return lo scalare corrispondente
     */
    K of(long value);

    /**
     * Crea un elemento scalare a partire da un valore intero a 32 bit.
     * 
     * @param value il valore numerico int
     * @return lo scalare corrispondente
     */
    K of(int value);
}
