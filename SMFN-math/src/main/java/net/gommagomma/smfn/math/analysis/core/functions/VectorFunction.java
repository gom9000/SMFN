package net.gommagomma.smfn.math.analysis.core.functions;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Rappresenta una funzione vettoriale a più variabili.
 * Definisce una mappa del tipo f: K^n -> K^m, che associa a un vettore d'ingresso
 * appartenente a uno spazio vettoriale un altro vettore dello stesso spazio.
 * </p>
 *
 * @param <K> Il tipo dello scalare che compone gli elementi dei vettori di dominio e codominio
 */
public interface VectorFunction<K extends ScalarElement<K>>
extends Mapping<Vector<K>, Vector<K>>
{}
