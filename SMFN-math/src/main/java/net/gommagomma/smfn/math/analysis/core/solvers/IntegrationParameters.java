package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Parametri numerici per l'integrazione di equazioni differenziali.
 * 
 * Supporta sia solutori ad ampiezza di passo fissa (es. Eulero, Runge-Kutta 4 classico),
 * sia solutori ad ampiezza di passo adattiva/variabile.
 * Nel caso di solutori adattivi, il parametro fixedStepSize viene interpretato come
 * la stima iniziale del passo di integrazione (h_0) da utilizzare per la prima iterazione.
 */
public final class IntegrationParameters
{
	/**
     * Ampiezza del passo di integrazione fisso o stima del passo iniziale h_0 per solutori adattivi.
     */
    public final Real fixedStepSize; 
    
    /**
     * Tolleranza d'errore locale tau per il controllo adattivo del passo.
     * Puo' essere null se il solutore opera a passo fisso.
     */
    public final Real tolerance;

    /**
     * Limite superiore h_max per l'ampiezza del passo di integrazione nei solutori adattivi.
     * Puo' essere null se non e' definito alcun limite massimo.
     */
    public final Real maxStepSize;

    /**
     * Limite inferiore h_min per l'ampiezza del passo di integrazione nei solutori adattivi.
     * Puo' essere null se non e' definito alcun limite minimo.
     */
    public final Real minStepSize;

    /**
     * Costruisce una configurazione per integrazione a passo fisso.
     *
     * @param fixedStepSize L'ampiezza costante del passo di integrazione h
     */
    public IntegrationParameters(Real fixedStepSize) {
        this(fixedStepSize, null, null, null);
    }
    
    /**
     * Costruisce una configurazione completa.
     *
     * @param fixedStepSize L'ampiezza del passo fisso o la stima iniziale h_0
     * @param tolerance La tolleranza d'errore locale target tau
     * @param maxStepSize L'ampiezza massima consentita per il passo h_max
     * @param minStepSize L'ampiezza minima consentita per il passo h_min
     */
    public IntegrationParameters(Real fixedStepSize, Real tolerance, Real maxStepSize, Real minStepSize) {
        this.fixedStepSize = fixedStepSize;
        this.tolerance = tolerance;
        this.maxStepSize = maxStepSize;
        this.minStepSize = minStepSize;
    }
}
