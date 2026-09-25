package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * apply(P) rappresenta un residuo da annullare: si cerca P tale che apply(P) = 0.
 */

/**
 * Modellizza un problema generale di ricerca degli zeri (o radici) della forma: F(P) = 0
 * dove si ricerca un elemento P^* di P per cui la funzione residuo assume l'elemento nullo dello spazio.
 *
 * @param <P> Il tipo dell'elemento algebrico appartenente allo spazio del dominio e del residuo
 */
public interface RootFindingProblem<P extends AlgebraicElement<P>>
extends IterationProblem<P>
{}
