package net.gommagomma.smfn.math.algebra.core.elements;

/**
 * Rappresenta un elemento scalare di tipo approssimato (es. numeri in virgola mobile 
 * a precisione singola o doppia, o rappresentazioni reali/complesse soggette a tolleranze numeriche).
 *
 * @param <K> il tipo concreto dell'elemento approssimato
 */
public interface ApproximateElement<K extends ApproximateElement<K>>
extends ScalarElement<K>
{}
