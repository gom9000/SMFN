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
 * Rappresenta l'operatore Hamiltoniano H in meccanica quantistica, associato all'osservabile dell'energia totale del sistema.
 * 
 * E' rappresentato da una matrice quadrata hermitiana (o simmetrica) definita su un campo algebrico di scalari K.
 * La risoluzione dell'equazione agli autovalori indipendente dal tempo: H |\psi_n\rangle = E_n |\psi_n\rangle
 * permette di determinare gli autostati dell'energia (stati stazionari |psi_n> e i corrispondenti livelli di energia (autovalori reali E_n).
 * </p>
 *
 * @param <K> Il tipo dello scalare appartenente al campo algebrico (es. Real o Complex)
 */
public final class Hamiltonian<K extends ScalarElement<K>>
extends Observable<K>
{
	/**
     * Costruisce un operatore Hamiltoniano a partire dalla matrice quadrata dell'operatore.
     *
     * @param operator La matrice quadrata rappresentante l'operatore Hamiltoniano H
     */
	public Hamiltonian(SquareMatrix<K> operator) {
		super(operator);
	}

	/**
     * Calcola gli stati stazionari del sistema risolvendo il problema agli autovalori per l'Hamiltoniano.
     * 
     * Esegue la decomposizione spettrale tramite GeneralEigenvalueSolver. Se il solutore raggiunge
     * la convergenza entro i parametri specificati, estrae i livelli energetici reali (autovalori)
     * e converte gli autovettori corrispondenti in istanze di QuantumState.
     *
     * @param params I parametri di arresto e tolleranza numerica per il solutore di autovalori
     * @return L'oggetto StationaryStates contenente la lista dei livelli di energia e dei rispettivi autostati quantistici
     * @throws IllegalStateException Se la decomposizione spettrale non raggiunge la convergenza
     */
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
