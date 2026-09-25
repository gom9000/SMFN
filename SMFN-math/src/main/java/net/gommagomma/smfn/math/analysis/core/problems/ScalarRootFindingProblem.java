package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Modellizza un problema di ricerca degli zeri per funzioni scalari ad una singola variabile:
 * f(x) = 0 dove x in T e T è un tipo appartenente ad un anello o campo algebrico.
 *
 * @param <T> Il tipo dello scalare appartenente al dominio e al codominio del residuo
 */
public interface ScalarRootFindingProblem<T extends ScalarElement<T>>
extends RootFindingProblem<T>
{}
