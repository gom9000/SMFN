package net.gommagomma.smfn.math.analysis.solvers.ode;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.models.DynamicSystem;
import net.gommagomma.smfn.math.analysis.solvers.core.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.solvers.core.IntervalSolver;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.NormedVectorElement;


/**
 * Un solutore ODESolver che utilizza un metodo Runge-Kutta embedded (RK2/RK1, tipo Heun) a passo adattivo.
 * Adatta dinamicamente la dimensione del passo per mantenere l'errore al di sotto di una tolleranza specificata.
 * 
 * @param <K> Il tipo di campo (es. Complex, Real) per gli scalari del sistema. Deve essere Normable.
 * @param <T> Il tipo di vettore (es. ComplexVector) che rappresenta lo stato del sistema. Deve essere NormedVector.
 */
public class EmbeddedRK23Solver<K extends FieldElement<K> & NormableElement<Real, K>, T extends NormedVectorElement<K, T>> 
implements IntervalSolver<K, T>
{
	private final Field<K> field;


    /**
     * @param field L'istanza del Campo K.
     */
    public EmbeddedRK23Solver(Field<K> field) {
        this.field = field;
    }

    // Helper per ottenere costanti K da double (richiede ancora Field.valueOf(double))
    private K val(double v) {
       return field.valueOf(v); 
    }

    /**
     * Esegue un singolo passo utilizzando il metodo embedded RK2/RK1 (Heun).
     * Restituisce il risultato di ordine superiore (RK2) e l'errore stimato locale.
     * 
     * @param system Il sistema differenziale.
     * @param currentState Lo stato attuale.
     * @param currentTime Il tempo attuale.
     * @param deltaTime La dimensione del passo da tentare.
     * @return Un oggetto coppia contenente [Risultato RK2, Errore stimato]
     */
    private Pair<T, Real> adaptiveStep(DynamicSystem<K, T> system, T currentState, Real currentTime, Real deltaTime) {
        
        K dtAsK = val(deltaTime.getValue());
        Real halfDtAsReal = new Real(0.5);
        K halfDtAsK = val(halfDtAsReal.multiply(deltaTime).getValue());

        // --- Calcolo RK1 (Eulero) e K1 ---
        T k1_rate = system.derivative(currentState, currentTime);
        T resultRK1 = currentState.add(k1_rate.multiplyByScalar(dtAsK));

        // --- Calcolo RK2 (Heun) ---
        Real t_plus_dt = (Real) currentTime.add(deltaTime);
        T state_k2 = currentState.add(k1_rate.multiplyByScalar(halfDtAsK));
        T k2_rate = system.derivative(state_k2, t_plus_dt);
        
        K halfAsK = val(halfDtAsReal.getValue());
        T avg_rate = k1_rate.add(k2_rate).multiplyByScalar(halfAsK);
        T resultRK2 = currentState.add(avg_rate.multiplyByScalar(dtAsK));

        // Calcolo dell'errore stimato locale usando la norma (metrica)
        T errorVector = resultRK2.subtract(resultRK1);
        Real errorMagnitude = errorVector.norm(); // Usa la tua interfaccia NormedVectorElement

        return new Pair<>(resultRK2, errorMagnitude);
    }

    @Override
    public T integrate(DynamicSystem<K, T> system, T initialState, Real startTime, Real endTime, IntegrationParameters params) {
        
        if (params.convergenceParams == null) {
            throw new IllegalArgumentException("EmbeddedRK23Solver richiede ConvergenceParameters.");
        }
        
        Real tolerance = params.convergenceParams.tolerance;
        double safetyFactor = 0.9; 
        double minStepSize = 1e-12;
        double maxStepSize = 0.01;

        T currentState = initialState.copy();
        Real currentTime = startTime;
        Real currentStepSize = new Real(0.01); 

        boolean forward = endTime.isGreaterThan(startTime);
        
        while ((forward && currentTime.isLessThan(endTime)) || (!forward && currentTime.isGreaterThan(endTime))) {
            
            Real remainingTime = (Real) endTime.subtract(currentTime);
            Real actualStep = (currentStepSize.modulus() > remainingTime.modulus()) ? 
                              remainingTime : currentStepSize;
            
            if (actualStep.isZero()) break;

            // --- Esecuzione del passo adattivo ---
            Pair<T, Real> stepResult = adaptiveStep(system, currentState, currentTime, actualStep);
            T resultRK2 = stepResult.getFirst();
            Real errorMagnitude = stepResult.getSecond();

            // --- Adattamento del Passo ---
            double scaleFactor = safetyFactor * Math.pow(tolerance.modulus() / errorMagnitude.modulus(), 0.5); 
            
            if (errorMagnitude.isLessThan(tolerance) || errorMagnitude.isMathematicallyEqualTo(tolerance)) {
                // Accetta il passo
                currentState = resultRK2;
                currentTime = (Real) currentTime.add(actualStep);
                // Aumenta il passo se possibile, limitato da maxStepSize
                double nextStepValue = actualStep.modulus() * Math.max(1.0/safetyFactor, scaleFactor);
                currentStepSize = new Real(Math.min(nextStepValue, maxStepSize));
            } else {
                // Rifiuta il passo, riprova con un passo più piccolo
                double nextStepValue = actualStep.modulus() * Math.max(minStepSize / actualStep.modulus(), Math.min(safetyFactor, scaleFactor));
                currentStepSize = new Real(nextStepValue);
            }
        }
        
        return currentState;
    }

    class Pair<U, V> {
        private final U first;
        private final V second;

        public Pair(U first, V second) {
            this.first = first;
            this.second = second;
        }

        public U getFirst() { return first; }
        public V getSecond() { return second; }
    }
}
