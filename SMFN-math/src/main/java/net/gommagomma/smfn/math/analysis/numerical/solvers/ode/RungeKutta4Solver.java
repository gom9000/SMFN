package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IntervalODEStepSolver;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement; 


/**
 * Un solutore ODESolver che utilizza il metodo Runge-Kutta di quarto ordine (RK4) a passo fisso.
 * 
 * @param <K> Il tipo di campo (es. Complex, Real) per gli scalari del sistema.
 * @param <T> Il tipo di vettore (es. ComplexVector) che rappresenta lo stato del sistema.
 */
public class RungeKutta4Solver<K extends FieldElement<K>, T extends VectorElement<K, T>> 
implements IntervalODEStepSolver<K, T>
{
	private final NumericFactory<K> scalarFactory;


    /**
     * @param field L'istanza del Campo K.
     */
    public RungeKutta4Solver(NumericFactory<K> scalarFactory) {
	    this.scalarFactory = scalarFactory;
    }

    // Helper per ottenere costanti K da double
    private K val(double v) {
       return scalarFactory.of(v);
    }

    /**
     * Esegue un singolo passo di integrazione utilizzando il metodo Runge-Kutta di quarto ordine (RK4).
     * 
     * @param system Il sistema differenziale (fornisce la derivata dy/dt = f(t, y)).
     * @param currentState Lo stato y(t) corrente.
     * @param currentTime Il tempo t corrente.
     * @param deltaTime La dimensione del passo h.
     * @return Lo stato y(t + h) approssimato.
     */
    @Override
    public T step(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime)
    {    
        // Convertiamo i Real usati per i passi temporali nel tipo scalare K
    	K dt = val(deltaTime.abs().getValue());
        
        // Definiamo le costanti necessarie usando l'API Real
        Real realHalf = new Real(0.5);
        Real realOneSixth = new Real(1.0/6.0);
        K two = val(2.0); // Ancora utile per la moltiplicazione scalare

        // --- Calcolo K1 ---
        // t_k1 = currentTime
        // k1_rate = f(t_k1, currentState)
        T k1_rate = system.derivative(currentState, currentTime);
        // k1 = k1_rate * dt (nello spazio K)
        T k1 = k1_rate.scale(dt); 

        // --- Calcolo K2 ---
        // t_k2 = currentTime + deltaTime / 2
        Real time_k2 = currentTime.add(deltaTime.multiply(realHalf)); // Uso corretto di realHalf
        // state_k2 = currentState + k1_rate * (deltaTime / 2)
        K dtHalfAsK = val(deltaTime.multiply(realHalf).abs().getValue()); // Uso corretto di realHalf
        T state_k2 = currentState.add(k1_rate.scale(dtHalfAsK));
        // k2_rate = f(t_k2, state_k2)
        T k2_rate = system.derivative(state_k2, time_k2);
        // k2 = k2_rate * dt
        T k2 = k2_rate.scale(dt);

        // --- Calcolo K3 ---
        // t_k3 = currentTime + deltaTime / 2 (uguale a t_k2)
        Real time_k3 = time_k2; 
        // state_k3 = currentState + k2_rate * (deltaTime / 2)
        // Riutilizziamo dtHalfAsK
        T state_k3 = currentState.add(k2_rate.scale(dtHalfAsK));
        // k3_rate = f(t_k3, state_k3)
        T k3_rate = system.derivative(state_k3, time_k3);
        // k3 = k3_rate * dt
        T k3 = k3_rate.scale(dt);
        
        // --- Calcolo K4 ---
        // t_k4 = currentTime + deltaTime
        Real time_k4 = currentTime.add(deltaTime);
        // state_k4 = currentState + k3_rate * deltaTime
        T state_k4 = currentState.add(k3_rate.scale(dt)); // Usiamo dt in K
        // k4_rate = f(t_k4, state_k4)
        T k4_rate = system.derivative(state_k4, time_k4);
        // k4 = k4_rate * dt
        T k4 = k4_rate.scale(dt);

        // --- Calcolo nextState ---
        // nextState = currentState + 1/6 * (K1 + 2*K2 + 2*K3 + K4)
        T sum = k1.add(k2.scale(two))
                  .add(k3.scale(two))
                  .add(k4);
        
        // Moltiplichiamo la somma per 1/6 (convertito in K)
        K oneSixthAsK = val(realOneSixth.abs().getValue());
        return currentState.add(sum.scale(oneSixthAsK));
    }


    /**
     * Esegue l'integrazione del sistema differenziale da startTime a endTime 
     * utilizzando un passo fisso specificato in IntegrationParameters.
     * 
     * @param problem Il problem differenziale da risolvere.
     * @param initialState Lo stato iniziale al tempo startTime.
     * @param startTime Il tempo iniziale.
     * @param endTime Il tempo finale desiderato.
     * @param params I parametri di integrazione, che devono contenere un fixedStepSize non nullo.
     * @return Lo stato del sistema al tempo endTime.
     */
    @Override
    public T integrate(InitialValueProblem<K, T> problem, Real endTime, IntegrationParameters params)
    {
    	Real fixedDeltaTime = params.fixedStepSize; 
        if (fixedDeltaTime == null || fixedDeltaTime.isZero()) {
            throw new IllegalArgumentException("RungeKutta4Solver richiede un parametro fixedStepSize non nullo e non zero.");
        }

        T currentState = problem.getInitialState().copy();
        Real currentTime = problem.getStartTime();

        // Determina se stiamo integrando in avanti o indietro nel tempo
        boolean forward = endTime.isGreaterThan(currentTime);
        Real effectiveDeltaTime = forward ? fixedDeltaTime : (Real) fixedDeltaTime.negate();

        while ((forward && currentTime.isLessThan(endTime)) || (!forward && currentTime.isGreaterThan(endTime)))
        {    
            // Calcola la dimensione effettiva del passo per non superare endTime
            Real remainingTime = endTime.subtract(currentTime);
            
            // Scegli il passo minimo (in valore assoluto) tra effectiveDeltaTime e il tempo rimanente
            Real stepDt = (effectiveDeltaTime.abs().getValue() > remainingTime.abs().getValue()) ? 
                          remainingTime : effectiveDeltaTime;
            
            currentState = step(problem, currentState, currentTime, stepDt);
            currentTime = currentTime.add(stepDt);

            if (stepDt.isZero()) break;
        }
        
        return currentState;
    }
}
