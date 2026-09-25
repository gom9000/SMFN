package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Operator;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Rappresenta una funzione matematica ad una singola variabile scalare.
 * Definisce una mappa del tipo f: K -> K, che associa a un elemento scalare del dominio
 * un singolo valore scalare appartenente allo stesso campo o anello di appartenenza.
 *
 * @param <K> Il tipo dello scalare appartenente al dominio e al codominio della funzione
 */
public interface ScalarFunction<K extends ScalarElement<K>>
extends Operator<K>
{}
