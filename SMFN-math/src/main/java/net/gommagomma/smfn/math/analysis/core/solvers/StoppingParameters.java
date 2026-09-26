package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Parametri numerici per il controllo della convergenza nei solutori iterativi.
 * 
 * Definendo una soglia di tolleranza numerica e un limite massimo per le iterazioni eseguibili,
 * garantisce che il processo iterativo termini in modo controllato, prevenendo cicli infiniti o divergenze non gestite.
 */
public final class StoppingParameters
{
	/**
     * La soglia di tolleranza d'errore per la verifica dei criteri di convergenza.
     */
    public final Real tolerance;

    /**
     * Il numero massimo di iterazioni consentite per il completamento dell'algoritmo.
     */
    public final int maxIterations;

    /**
     * Costruisce un oggetto di configurazione per la convergenza.
     *
     * @param tolerance La tolleranza d'errore target epsilon; deve essere un valore reale finito e strettamente positivo
     * @param maxIterations Il numero massimo di iterazioni consentite; deve essere strettamente maggiore di zero
     * @throws IllegalArgumentException Se tolerance e' NaN, infinita, o minore/uguale a zero, oppure se maxIterations e' minore/uguale a zero
     */
    public StoppingParameters(Real tolerance, int maxIterations)
    {
    	if (Double.isNaN(tolerance.getValue()) || Double.isInfinite(tolerance.getValue())) {
            throw new IllegalArgumentException("Tolerance must be a finite, non-NaN value.");
        }
    	if (tolerance.compareTo(RealField.INSTANCE.zero()) <= 0) {
    	    throw new IllegalArgumentException("Tolerance must be positive.");
    	}
    	if (maxIterations <= 0) {
    	    throw new IllegalArgumentException("Max iterations must be positive.");
    	}
 
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    /**
     * Restituisce la tolleranza d'errore configurata.
     *
     * @return Il valore reale della tolleranza
     */
	public Real getTolerance() {
		return tolerance;
	}

	/**
     * Restituisce il numero massimo di iterazioni consentito.
     *
     * @return L'intero indicante il limite di iterazioni
     */
	public int getMaxIterations() {
		return maxIterations;
	}
}
