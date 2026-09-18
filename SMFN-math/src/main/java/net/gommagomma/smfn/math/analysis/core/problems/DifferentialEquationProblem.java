package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Definisce la dinamica di un sistema: la derivata V' = F(t, V).
 *
 * V e' generico su LinearElement<V,K>, non fissato a Vector<K>: qualunque
 * stato che supporti addizione e scalatura funziona -- inclusi stati
 * matriciali (SquareMatrix<K> e' gia' un LinearElement), comuni nelle
 * equazioni di evoluzione lineare (es. dY/dt = A*Y in teoria del controllo,
 * o la Schrodinger a valori complessi piu' avanti).
 *
 * Non e' una Mapping<I,O>: e' genuinamente binaria (stato, tempo) -> derivata.
 */
public interface DifferentialEquationProblem<K extends ScalarElement<K>, V extends LinearElement<V, K>>
extends AnalysisProblem<V>
{
    V derivative(V currentState, Real currentTime);
}
