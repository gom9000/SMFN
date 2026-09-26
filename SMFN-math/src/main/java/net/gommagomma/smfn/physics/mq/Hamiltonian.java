package net.gommagomma.smfn.physics.mq;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.GeneralEigenvalueSolver;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * L'Hamiltoniana: l'Observable specifico che rappresenta l'energia totale
 * del sistema. Genera l'evoluzione temporale (equazione di Schrodinger):
 * direttamente se K=Complex, tramite Observable.toComplex() se K e' reale,
 * dato che l'evoluzione vive sempre in uno spazio di Hilbert complesso.
 *
 *
 * E' un Observable a tutti gli effetti (misurare l'energia e' una misura
 * legittima come qualunque altra) -- eredita, non compone, cosi' un
 * Hamiltonian e' sostituibile ovunque un Observable dello stesso K sia atteso.
 *
 * Generico su K come Observable: un'Hamiltoniana reale (oscillatore armonico
 * su griglia) e una complessa (spin in campo magnetico) sono entrambe
 * legittime. findStationaryStates() usa GeneralEigenvalueSolver<K>, non
 * HermitianEigenvalueSolver fisso -- cosi' un'Hamiltoniana reale usa
 * davvero JacobiEigenvalueSolver (il percorso piu' efficiente e onesto
 * sulla propria natura), non il solver complesso per coincidenza.
 *
 * findStationaryStates() risolve H|psi> = E|psi>: gli autovalori sono i
 * livelli energetici, gli autovettori gli stati stazionari corrispondenti.
 * Restituisce StationaryStates (livelli energetici, QuantumState), non
 * EigenDecomposition grezza -- stessa traduzione fisica che
 * MeasurementOutcome fa per la misura.
 */
public final class Hamiltonian<K extends ScalarElement<K>> extends Observable<K>
{
	public Hamiltonian(SquareMatrix<K> operator) {
		super(operator);
	}

	public StationaryStates findStationaryStates(StoppingParameters params) {
		SolverResult<EigenDecomposition> result = new GeneralEigenvalueSolver<K>().solve(asOperator(), params);
		if (result.getStatus() != TerminationStatus.CONVERGED) {
			throw new IllegalStateException("Eigenvalue decomposition did not converge: " + result.getStatus());
		}
		EigenDecomposition decomposition = result.getValue();
		List<Real> energyLevels = decomposition.getRealEigenvalues(params.tolerance);

		List<QuantumState> states = new ArrayList<>(decomposition.getEigenvectors().size());
		for (Vector<Complex> eigenvector : decomposition.getEigenvectors()) {
			states.add(QuantumState.from(eigenvector));
		}

		return new StationaryStates(energyLevels, states);
	}
}
