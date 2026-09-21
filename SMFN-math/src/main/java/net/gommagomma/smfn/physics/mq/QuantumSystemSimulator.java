package net.gommagomma.smfn.physics.mq;

import java.util.List;
import java.util.Random;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.EigenDecomposition;
import net.gommagomma.smfn.math.analysis.numerical.solvers.eigen.HermitianEigenvalueSolver;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Simulatore per sistemi quantistici: sia il valore di aspettazione
 * (measure(), deterministico) sia la misura vera (performMeasurement(),
 * probabilistica -- campiona un autovalore secondo la regola di Born e fa
 * collassare lo stato sull'autovettore corrispondente).
 *
 * Nessuno stato interno: sia la sorgente di casualita' sia i parametri di
 * convergenza per la diagonalizzazione si passano espliciti a ogni
 * chiamata, come ogni altro Solver di questa libreria.
 */
public final class QuantumSystemSimulator
{
	/**
     * Calcola il valore di aspettazione di un osservabile per uno stato quantistico specificato, 
     * sfruttando il prodotto interno hermitiano dello spazio vettoriale sottostante.
     * 
     * @param space lo spazio vettoriale con prodotto interno che ospita lo stato
     * @param observable l'osservabile hermitiano da misurare (H)
     * @param state il vettore di stato quantistico normalizzato o non normalizzato (|psi>)
     * @return il valore di aspettazione sotto forma di Real (garantito reale per operatori hermitiani)
     */
	public Real measure(InnerProductVectorSpace<Complex, ?> space, Observable observable, Vector<Complex> state) {
		Vector<Complex> H_psi = observable.asOperator().apply(state);
		Complex expectation = space.innerProduct(state, H_psi);
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
	 * @param space lo spazio vettoriale con prodotto interno che ospita lo stato
	 * @param observable l'osservabile hermitiano da misurare
	 * @param state il vettore di stato quantistico (|psi>)
	 * @param eigenParams parametri di convergenza per la diagonalizzazione
	 * @param random sorgente di casualita' per il campionamento secondo Born
	 * @return l'esito della misura: autovalore ottenuto e stato collassato
	 */
	public MeasurementOutcome performMeasurement(InnerProductVectorSpace<Complex, ?> space, Observable observable,
	                                              Vector<Complex> state, ConvergenceParameters eigenParams, Random random) {
		EigenDecomposition decomposition = new HermitianEigenvalueSolver().solve(observable.asOperator(), eigenParams);
		List<Complex> eigenvalues = decomposition.getEigenvalues();
		List<Vector<Complex>> eigenvectors = decomposition.getEigenvectors();
		int n = eigenvalues.size();

		double[] probabilities = new double[n];
		double total = 0.0;
		for (int i = 0; i < n; i++) {
			Complex amplitude = space.innerProduct(eigenvectors.get(i), state); // <lambda_i|psi>
			double p = amplitude.getRe() * amplitude.getRe() + amplitude.getIm() * amplitude.getIm();
			probabilities[i] = p;
			total += p;
		}
		if (total <= 0.0) {
			throw new IllegalArgumentException("The state vector has zero norm; cannot compute measurement probabilities.");
		}

		double r = random.nextDouble() * total; // campiona su [0, total) invece di normalizzare prima, stesso risultato, un giro in meno
		double cumulative = 0.0;
		int chosen = n - 1; // ripiego per arrotondamento in virgola mobile sull'ultimo passo
		for (int i = 0; i < n; i++) {
			cumulative += probabilities[i];
			if (r < cumulative) {
				chosen = i;
				break;
			}
		}

		Real measuredValue = new Real(eigenvalues.get(chosen).getRe());
		Vector<Complex> collapsedState = eigenvectors.get(chosen);
		return new MeasurementOutcome(measuredValue, collapsedState);
	}
}
