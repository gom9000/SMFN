package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IntervalODEStepSolver;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

/**
 * Solutore ODE Embedded Runge-Kutta di ordine 2(3) (Bogacki–Shampine).
 * Utilizza la stima dell'errore per il controllo del passo adattivo.
 */
public class EmbeddedRK23Solver<K extends FieldElement<K>, T extends VectorElement<K, T> & Normable<Real, T>>
implements IntervalODEStepSolver<K, T>
{
    private final NumericFactory<K> scalarFactory;

    // --- Coefficienti RKF2(3) (Bogacki–Shampine) ---
    private static final double A21 = 0.5;
    private static final double A32 = 0.75;
    private static final double A41 = 2.0 / 9.0;
    private static final double A42 = 1.0 / 3.0;
    private static final double A43 = 4.0 / 9.0;

    // Ordine 3 (usato come stima di ordine superiore)
    private static final double C31 = 2.0 / 9.0;
    private static final double C32 = 1.0 / 3.0;
    private static final double C33 = 4.0 / 9.0;

    // Ordine 2 (usato per l'errore)
    private static final double C21 = 7.0 / 24.0;
    private static final double C22 = 1.0 / 4.0;
    private static final double C23 = 1.0 / 3.0;
    private static final double C24 = 1.0 / 8.0;

    // --- Costruttore ---
    
    public EmbeddedRK23Solver(NumericFactory<K> scalarFactory) { 
        this.scalarFactory = scalarFactory;
    }

    private K val(double v) {
       return scalarFactory.of(v);
    }
    
    /**
     * Struttura dati per il risultato di un singolo passo embedded (RKF).
     */
    private static class EmbeddedStep<T extends VectorElement<?, T>>
    {
        final T result;
        final T errorEstimate;

        EmbeddedStep(T result, T errorEstimate) {
            this.result = result;
            this.errorEstimate = errorEstimate;
        }
    }
    
    // --- Metodo Interno di Passo Embedded ---
    private EmbeddedStep<T> embeddedStep(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime)
    {
        K dt = val(deltaTime.abs().getValue());

        // ... (La logica RK è identica e corretta) ...
        T k1_rate = system.derivative(currentState, currentTime);
        
        Real t2 = currentTime.add(deltaTime.multiply(new Real(A21)));
        T y2 = currentState.add(k1_rate.scale(val(A21).multiply(dt)));
        T k2_rate = system.derivative(y2, t2);
        
        Real t3 = currentTime.add(deltaTime.multiply(new Real(A32))); 
        T y3 = currentState.add(k2_rate.scale(val(A32).multiply(dt)));
        T k3_rate = system.derivative(y3, t3);

        Real t4 = currentTime.add(deltaTime);
        T term41 = k1_rate.scale(val(A41).multiply(dt));
        T term42 = k2_rate.scale(val(A42).multiply(dt));
        T term43 = k3_rate.scale(val(A43).multiply(dt));
        T y4 = currentState.add(term41).add(term42).add(term43);
        T k4_rate = system.derivative(y4, t4);
        
        T term31 = k1_rate.scale(val(C31).multiply(dt));
        T term32 = k2_rate.scale(val(C32).multiply(dt));
        T term33 = k3_rate.scale(val(C33).multiply(dt));
        T result = currentState.add(term31).add(term32).add(term33);

        T term21 = k1_rate.scale(val(C21).multiply(dt));
        T term22 = k2_rate.scale(val(C22).multiply(dt));
        T term23 = k3_rate.scale(val(C23).multiply(dt));
        T term24 = k4_rate.scale(val(C24).multiply(dt));
        T resultOrder2 = currentState.add(term21).add(term22).add(term23).add(term24);

        T errorEstimate = result.subtract(resultOrder2);

        return new EmbeddedStep<>(result, errorEstimate);
    }
    
    // --- 3. Implementazione di step() (Corretta) ---
    @Override
    public T step(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime)
    {
        return embeddedStep(system, currentState, currentTime, deltaTime).result;
    }

    // --- 4. Implementazione di integrate() (Logica di controllo del passo CORRETTA senza .min()) ---
    
    @Override
    public T integrate(InitialValueProblem<K, T> problem, Real endTime, IntegrationParameters params)
    {
        // Recupero delle condizioni iniziali
        T currentState = problem.getInitialState().copy();
        Real currentTime = problem.getStartTime();
        
        // Recupero dei parametri adattivi (Assumiamo che getValue() restituisca double)
        Real tolerance = params.tolerance;
        Real maxStepSize = params.maxStepSize;
        Real minStepSize = params.minStepSize;
        Real realTen = new Real(10.0);

        if (tolerance == null || maxStepSize == null || minStepSize == null) {
             throw new IllegalArgumentException("EmbeddedRK23Solver richiede tolerance, maxStepSize e minStepSize.");
        }
        
        final double SAFETY = 0.9;
        final double P_INV = 1.0 / 3.0; 
        
        boolean forward = endTime.isGreaterThan(currentTime);
        Real timeDirection = forward ? new Real(1.0) : new Real(-1.0);

        // h_abs: Passo ottimale assoluto (dimensione suggerita).
        // Inizializzazione: guess iniziale
        Real h_abs = (params.fixedStepSize != null) ? params.fixedStepSize : maxStepSize.divide(realTen); 
        
        // Limita il guess iniziale [h_min, h_max]
        h_abs = new Real(Math.min(h_abs.getValue(), maxStepSize.getValue()));
        h_abs = new Real(Math.max(h_abs.getValue(), minStepSize.getValue()));

        
        while ((forward && currentTime.isLessThan(endTime)) || (!forward && currentTime.isGreaterThan(endTime)))
        {
            Real remainingTime = endTime.subtract(currentTime);
            
            // --- 1. Calcolo di h_to_execute_modulus (Passo effettivo limitato) ---
            
            // remaining_modulus è il limite dato dalla fine dell'intervallo
            double remaining_modulus_val = remainingTime.getValue();
            
            // Limita h_abs al tempo rimanente e a h_max
            double h_to_execute_modulus_val = Math.min(h_abs.getValue(), remaining_modulus_val);
            h_to_execute_modulus_val = Math.min(h_to_execute_modulus_val, maxStepSize.getValue());
            
            Real h_to_execute_modulus = new Real(h_to_execute_modulus_val);
            
            // Se il passo effettivo è zero, usciamo
            if (h_to_execute_modulus.isZero()) break;

            // Applica la direzione: Questo è l'h Effettivo da eseguire.
            Real h_to_execute = h_to_execute_modulus.multiply(timeDirection);

            // 2. Loop per il controllo dell'errore (ritenta il passo se rifiutato)
            while (true) {
                
                // Esegue il passo
                EmbeddedStep<T> stepResult = embeddedStep(problem, currentState, currentTime, h_to_execute);
                T errorEstimate = stepResult.errorEstimate;

                // Calcola l'errore normalizzato: ||e|| / (tolerance * ||y||)
                double errorNorm = errorEstimate.norm().getValue(); 
                double stateNorm = currentState.norm().getValue();
                double errorRatio = errorNorm / (tolerance.getValue() * Math.max(stateNorm, 1.0));

                // 3. Calcola il nuovo fattore di scala e il passo ottimale (h_new_abs)
                double scaleFactor = (errorRatio <= 1e-12) ? 5.0 : SAFETY * Math.pow(errorRatio, P_INV);
                Real newScaleFactorReal = new Real(scaleFactor); 
                
                // Calcolo del passo ottimale per il prossimo tentativo
                Real h_new_abs = h_to_execute_modulus.multiply(newScaleFactorReal); 

                // 4. Test di accettazione
                if (errorRatio <= 1.0) {
                    // Passo accettato
                    currentState = stepResult.result;
                    currentTime = currentTime.add(h_to_execute); 
                    
                    // Prepara h_abs per il loop esterno (limitandolo min/max)
                    h_abs = new Real(Math.min(h_new_abs.getValue(), maxStepSize.getValue()));
                    h_abs = new Real(Math.max(h_abs.getValue(), minStepSize.getValue()));
                    break; 
                } else {
                    // Passo rifiutato: Riprova con passo ridotto
                    
                    // h_abs diventa il nuovo passo ridotto (limitato min/max)
                    h_abs = new Real(Math.min(h_new_abs.getValue(), maxStepSize.getValue()));
                    h_abs = new Real(Math.max(h_abs.getValue(), minStepSize.getValue()));

                    // Controllo di fallimento
                    if (h_abs.isLessThan(minStepSize)) { 
                        throw new RuntimeException("Risoluzione ODE fallita: passo adattivo (" + h_abs + ") troppo piccolo.");
                    }
                    
                    // Prepara il passo h_to_execute per il prossimo tentativo
                    h_to_execute_modulus_val = Math.min(h_abs.getValue(), remaining_modulus_val);
                    h_to_execute_modulus_val = Math.min(h_to_execute_modulus_val, maxStepSize.getValue());
                    h_to_execute = new Real(h_to_execute_modulus_val).multiply(timeDirection);
                }
            }
        }
        
        return currentState;
    }
}