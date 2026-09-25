package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Modellizza un problema di ricerca degli zeri per sistemi di equazioni non lineari a più variabili:
 * F{x} = 0, F: K^n -> K^n dove x in K^n è un vettore di variabili d'ingresso e F(x) è il vettore residuo
 * appartenente allo stesso spazio vettoriale n-dimensionale.
 *
 * @param <K> Il tipo dello scalare che compone le coordinate dei vettori di input e di residuo
 */
public interface VectorRootFindingProblem<K extends ScalarElement<K>>
extends RootFindingProblem<Vector<K>>
{}
