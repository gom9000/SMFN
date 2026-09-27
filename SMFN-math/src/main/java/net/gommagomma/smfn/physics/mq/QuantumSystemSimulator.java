package net.gommagomma.smfn.physics.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.HermitianEigenvalueSolver;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Simulatore per processi di misurazione su sistemi quantistici in spazi di Hilbert a dimensione finita.
 * 
 * La classe fornisce metodi di utilita' per modellare gli aspetti deterministici e stocastici della misurazione:
 * <ul>
 *   <li><b>Valore di Aspettazione:</b> Calcolo del valore medio teorico \langle A \rangle = \langle \psi | A | \psi \rangle
 *       per un'osservabile hermitiana A tramite {@link #expectationValue(Observable, QuantumState)}.</li>
 *   <li><b>Distribuzione di Probabilita' di Born:</b> Calcolo della distribuzione discreta teorica P(a_i) = |\langle a_i | \psi \rangle|^2
 *       per ciascun autovalore tramite {@link #measurementProbabilities(Observable, QuantumState, StoppingParameters)}.</li>
 *   <li><b>Processo di Misurazione Stocastico:</b> Simulazione stocastica del processo di misurazione mediante campionamento
 *       secondo la regola di Born e conseguente collasso del vettore di stato tramite {@link #performMeasurement(Observable, QuantumState, StoppingParameters, Random)}.</li>
 * </ul>
 */
public final class QuantumSystemSimulator
{
	/**
     * Calcola il valore di aspettazione di un osservabile per uno stato quantistico specificato.
     *
     * @param observable l'osservabile hermitiano da misurare (H)
     * @param state il vettore di stato quantistico normalizzato o non normalizzato (|psi>)
     * @return il valore di aspettazione sotto forma di Real (garantito reale per operatori hermitiani)
     */
	public Real expectationValue(Observable<Complex> observable, QuantumState state) {
		Vector<Complex> H_psi = observable.asOperator().apply(state.asVector());
		Complex expectation = state.innerProduct(QuantumState.from(H_psi));
		Complex normSquared = state.innerProduct(state);
		if (normSquared.getRe() <= 0.0) {
			throw new IllegalArgumentException("The state vector has zero norm; cannot compute an expectation value.");
		}
		// <psi|H|psi> e' garantito reale per un Observable hermitiano.
		return new Real(expectation.getRe() / normSquared.getRe());
	}

	/**
	 * Esegue una misura quantistica vera: diagonalizza l'osservabile (via
	 * HermitianEigenvalueSolver), calcola le probabilita' di Born
	 * |<lambda_i|psi>|^2 per ciascun autovalore, ne campiona uno secondo
	 * quella distribuzione, e restituisce sia l'autovalore ottenuto sia lo
	 * stato collassato (la proiezione normalizzata di state sull'autospazio
	 * dell'autovalore campionato).
	 *
	 * Lo stato in ingresso non deve necessariamente essere normalizzato:
	 * le probabilita' vengono normalizzate internamente rispetto alla
	 * norma di state.
	 *
	 * @param observable l'osservabile hermitiano da misurare
	 * @param state il vettore di stato quantistico (|psi>)
	 * @param eigenParams parametri di convergenza per la diagonalizzazione
	 * @param random sorgente di casualita' per il campionamento secondo Born
	 * @return l'esito della misura: autovalore ottenuto e stato collassato
	 */
	public MeasurementOutcome performMeasurement(Observable<Complex> observable, QuantumState state, StoppingParameters eigenParams, Random random) {
		BornDistribution distribution = computeBornDistribution(observable, state, eigenParams);

		double r = random.nextDouble() * distribution.total; // campiona su [0, total) invece di normalizzare prima, un giro in meno
		double cumulative = 0.0;
		int chosen = distribution.eigenvalues.size() - 1; // ripiego per arrotondamento in virgola mobile sull'ultimo passo
		for (int i = 0; i < distribution.eigenvalues.size(); i++) {
			cumulative += distribution.probabilities[i];
			if (r < cumulative) {
				chosen = i;
				break;
			}
		}

		Real measuredValue = new Real(distribution.eigenvalues.get(chosen).getRe());
		QuantumState collapsedState = QuantumState.from(distribution.eigenspaceProjections.get(chosen)).normalize();
		return new MeasurementOutcome(measuredValue, collapsedState);
	}

	/**
	 * La distribuzione di probabilita' completa secondo la regola di Born.
	 *
	 * @param observable l'osservabile hermitiano da misurare
	 * @param state il vettore di stato quantistico (|psi>)
	 * @param eigenParams parametri di convergenza per la diagonalizzazione
	 * @return una coppia (autovalore, probabilita') per ciascun autovalore, probabilita' normalizzate a somma 1
	 */
	public List<MeasurementProbability> measurementProbabilities(Observable<Complex> observable, QuantumState state, StoppingParameters eigenParams) {
		BornDistribution distribution = computeBornDistribution(observable, state, eigenParams);

		List<MeasurementProbability> result = new ArrayList<>(distribution.eigenvalues.size());
		for (int i = 0; i < distribution.eigenvalues.size(); i++) {
			Real value = new Real(distribution.eigenvalues.get(i).getRe());
			Real probability = new Real(distribution.probabilities[i] / distribution.total);
			result.add(new MeasurementProbability(value, probability));
		}
		return result;
	}

	private BornDistribution computeBornDistribution(Observable<Complex> observable, QuantumState state, StoppingParameters eigenParams) {
		SolverResult<EigenDecomposition> result = new HermitianEigenvalueSolver().solve(observable.asOperator(), eigenParams);
		if (result.getStatus() != TerminationStatus.CONVERGED) {
			throw new IllegalStateException("Eigenvalue decomposition did not converge: " + result.getStatus());
		}
		EigenDecomposition decomposition = result.getValue();
		List<Complex> rawEigenvalues = decomposition.getEigenvalues();
		List<Vector<Complex>> rawEigenvectors = decomposition.getEigenvectors();
		int n = rawEigenvalues.size();
		double tolerance = eigenParams.tolerance.getValue();

		List<Complex> eigenvalues = new ArrayList<>();
		List<Vector<Complex>> eigenspaceProjections = new ArrayList<>();
		List<Double> probabilityList = new ArrayList<>();
		boolean[] grouped = new boolean[n];
		double total = 0.0;

		for (int i = 0; i < n; i++) {
			if (grouped[i]) continue;
			double lambda = rawEigenvalues.get(i).getRe();
			QuantumState projection = null;
			double p = 0.0;
			for (int j = i; j < n; j++) {
				if (grouped[j] || Math.abs(rawEigenvalues.get(j).getRe() - lambda) > tolerance) continue;
				grouped[j] = true;
				QuantumState basisState = QuantumState.from(rawEigenvectors.get(j));
				Complex amplitude = basisState.innerProduct(state); // <lambda_j|psi>
				p += amplitude.getRe() * amplitude.getRe() + amplitude.getIm() * amplitude.getIm();
				QuantumState term = basisState.scale(amplitude);
				projection = (projection == null) ? term : projection.plus(term);
			}
			eigenvalues.add(rawEigenvalues.get(i));
			eigenspaceProjections.add(projection.asVector());
			probabilityList.add(p);
			total += p;
		}
		if (total <= 0.0) {
			throw new IllegalArgumentException("The state vector has zero norm; cannot compute measurement probabilities.");
		}

		double[] probabilities = new double[probabilityList.size()];
		for (int i = 0; i < probabilities.length; i++) probabilities[i] = probabilityList.get(i);

		return new BornDistribution(eigenvalues, eigenspaceProjections, probabilities, total);
	}

	private static final class BornDistribution {
		final List<Complex> eigenvalues;
		final List<Vector<Complex>> eigenspaceProjections;
		final double[] probabilities;
		final double total;

		BornDistribution(List<Complex> eigenvalues, List<Vector<Complex>> eigenspaceProjections, double[] probabilities, double total) {
			this.eigenvalues = eigenvalues;
			this.eigenspaceProjections = eigenspaceProjections;
			this.probabilities = probabilities;
			this.total = total;
		}
	}
}
