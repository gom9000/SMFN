package net.gommagomma.smfn.physics.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.HermitianEigenvalueSolver;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Simulatore per sistemi quantistici: il valore di aspettazione (measure(),
 * deterministico), la misura vera (performMeasurement(), probabilistica --
 * campiona un autovalore secondo la regola di Born e fa collassare lo
 * stato sull'autovettore corrispondente), e la distribuzione di probabilita'
 * completa (measurementProbabilities(), senza campionamento).
 *
 * Nessun parametro di spazio in nessun metodo: QuantumState porta gia' con
 * se' il proprio InnerProductVectorSpace, quindi passarlo di nuovo ad ogni
 * chiamata sarebbe ridondante, non solo scomodo. Restano espliciti solo i
 * parametri di convergenza per la diagonalizzazione e la sorgente di
 * casualita', come ogni altro Solver di questa libreria.
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
		// <psi|H|psi> e' garantito reale per un Observable hermitiano.
		return new Real(expectation.getRe());
	}

	/**
	 * Esegue una misura quantistica vera: diagonalizza l'osservabile (via
	 * HermitianEigenvalueSolver), calcola le probabilita' di Born
	 * |<lambda_i|psi>|^2 per ciascun autovalore, ne campiona uno secondo
	 * quella distribuzione, e restituisce sia l'autovalore ottenuto sia lo
	 * stato collassato (l'autovettore corrispondente).
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
	public MeasurementOutcome performMeasurement(Observable<Complex> observable, QuantumState state,
	                                              ConvergenceParameters eigenParams, Random random) {
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
		QuantumState collapsedState = QuantumState.from(distribution.eigenvectors.get(chosen));
		return new MeasurementOutcome(measuredValue, collapsedState);
	}

	/**
	 * La distribuzione di probabilita' completa secondo la regola di Born,
	 * senza campionare -- stesso calcolo di performMeasurement(), ma
	 * restituito per intero invece di ridotto a un solo esito casuale.
	 *
	 * @param observable l'osservabile hermitiano da misurare
	 * @param state il vettore di stato quantistico (|psi>)
	 * @param eigenParams parametri di convergenza per la diagonalizzazione
	 * @return una coppia (autovalore, probabilita') per ciascun autovalore, probabilita' normalizzate a somma 1
	 */
	public List<MeasurementProbability> measurementProbabilities(Observable<Complex> observable, QuantumState state,
	                                                               ConvergenceParameters eigenParams) {
		BornDistribution distribution = computeBornDistribution(observable, state, eigenParams);

		List<MeasurementProbability> result = new ArrayList<>(distribution.eigenvalues.size());
		for (int i = 0; i < distribution.eigenvalues.size(); i++) {
			Real value = new Real(distribution.eigenvalues.get(i).getRe());
			Real probability = new Real(distribution.probabilities[i] / distribution.total);
			result.add(new MeasurementProbability(value, probability));
		}
		return result;
	}

	private BornDistribution computeBornDistribution(Observable<Complex> observable, QuantumState state, ConvergenceParameters eigenParams) {
		SolverResult<EigenDecomposition> result = new HermitianEigenvalueSolver().solve(observable.asOperator(), eigenParams);
		if (result.getStatus() != ConvergenceStatus.CONVERGED) {
			throw new IllegalStateException("Eigenvalue decomposition did not converge: " + result.getStatus());
		}
		EigenDecomposition decomposition = result.getValue();
		List<Complex> eigenvalues = decomposition.getEigenvalues();
		List<Vector<Complex>> eigenvectors = decomposition.getEigenvectors();
		int n = eigenvalues.size();

		double[] probabilities = new double[n];
		double total = 0.0;
		for (int i = 0; i < n; i++) {
			QuantumState eigenstate = QuantumState.from(eigenvectors.get(i));
			Complex amplitude = eigenstate.innerProduct(state); // <lambda_i|psi>
			double p = amplitude.getRe() * amplitude.getRe() + amplitude.getIm() * amplitude.getIm();
			probabilities[i] = p;
			total += p;
		}
		if (total <= 0.0) {
			throw new IllegalArgumentException("The state vector has zero norm; cannot compute measurement probabilities.");
		}

		return new BornDistribution(eigenvalues, eigenvectors, probabilities, total);
	}

	private static final class BornDistribution {
		final List<Complex> eigenvalues;
		final List<Vector<Complex>> eigenvectors;
		final double[] probabilities;
		final double total;

		BornDistribution(List<Complex> eigenvalues, List<Vector<Complex>> eigenvectors, double[] probabilities, double total) {
			this.eigenvalues = eigenvalues;
			this.eigenvectors = eigenvectors;
			this.probabilities = probabilities;
			this.total = total;
		}
	}
}
