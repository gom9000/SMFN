package net.gommagomma.smfn.physics.mq;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.HermitianEigenvalueSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

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
 * livelli energetici, gli autovettori gli stati stazionari corrispondenti.
 * Restituisce StationaryStates (livelli energetici, QuantumState), non
 * EigenDecomposition grezza -- stessa traduzione fisica che
 * MeasurementOutcome fa per la misura, non la macchina di
 * HermitianEigenvalueSolver esposta cosi' com'e'.
 */
public final class Hamiltonian extends Observable
{
	public Hamiltonian(SquareMatrix<Complex> operator) {
		super(operator);
	}

	public StationaryStates findStationaryStates(ConvergenceParameters params) {
		EigenDecomposition decomposition = new HermitianEigenvalueSolver().solve(asOperator(), params);
		List<Real> energyLevels = decomposition.getRealEigenvalues(params.tolerance);

		List<QuantumState> states = new ArrayList<>(decomposition.getEigenvectors().size());
		for (Vector<Complex> eigenvector : decomposition.getEigenvectors()) {
			states.add(QuantumState.from(eigenvector));
		}

		return new StationaryStates(energyLevels, states);
	}
}
