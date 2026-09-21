package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.HermitianEigenvalueSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;

/**
 * L'Hamiltoniana: l'Observable specifico che rappresenta l'energia totale
 * del sistema e genera l'evoluzione temporale (equazione di Schrodinger).
 *
 * E' un Observable a tutti gli effetti (misurare l'energia e' una misura
 * legittima come qualunque altra) -- eredita, non compone, cosi' un
 * Hamiltonian e' sostituibile ovunque un Observable sia atteso, incluso
 * il costruttore di SchrodingerEquationSystem senza modifiche.
 *
 * findStationaryStates() risolve H|psi> = E|psi>: gli autovalori sono i
 * livelli energetici, gli autovettori gli stati stazionari corrispondenti
 * -- diretto, la stessa macchina di HermitianEigenvalueSolver, solo con
 * un nome che parla di fisica invece che di algebra lineare.
 */
public final class Hamiltonian extends Observable
{
	public Hamiltonian(SquareMatrix<Complex> operator) {
		super(operator);
	}

	public EigenDecomposition findStationaryStates(ConvergenceParameters params) {
		return new HermitianEigenvalueSolver().solve(asOperator(), params);
	}
}
