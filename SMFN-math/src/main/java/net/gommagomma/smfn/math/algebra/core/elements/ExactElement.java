package net.gommagomma.smfn.math.algebra.core.elements;

/**
 * Rappresenta un elemento scalare di tipo esatto (es. numeri interi, frazioni razionali esatte, 
 * o polinomi con coefficienti esatti, privi di errori di arrotondamento in virgola mobile).
 *
 * @param <K> il tipo concreto dell'elemento esatto
 */
public interface ExactElement<K extends ExactElement<K>>
extends ScalarElement<K>
{}
