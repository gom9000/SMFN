package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Rappresenta una funzione matematica a più variabili scalari.
 * Definisce una mappa del tipo f: K^n -> K, che associa a un vettore di variabili d'ingresso
 * appartenente a uno spazio vettoriale o modulo un singolo valore scalare di output.
 *
 * @param <K> Il tipo dello scalare appartenente al dominio e al codominio della funzione
 */
public interface MultivariateFunction<K extends ScalarElement<K>>
extends Mapping<Vector<K>, K>
{}
