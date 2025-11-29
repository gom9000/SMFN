package net.gommagomma.smfn.math.analysis.differential;

import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.core.algebra.structures.Field;
import net.gommagomma.smfn.math.core.linearalgebra.elements.VectorElement;

import java.util.function.Function; 


public class RungeKutta4Solver<K extends FieldElement<K>, T extends VectorElement<K, T>> 
implements ODESolver<K, T>
{
    // Funzione che converte un Real in K (es. Real -> Complex, Real -> Real, Real -> Rational)
    private final Function<Real, K> realToKScalarConverter;
    private final Field<K> fieldInstance;

    /**
     * @param fieldInstance L'istanza del Campo K.
     * @param realToKScalarConverter La funzione per convertire un Real in K.
     */
    public RungeKutta4Solver(Field<K> fieldInstance, Function<Real, K> realToKScalarConverter) {
        this.fieldInstance = fieldInstance;
        this.realToKScalarConverter = realToKScalarConverter;
    }

    // Helper per ottenere costanti K da double (richiede ancora Field.valueOf(double))
    private K val(double v) {
       return fieldInstance.valueOf(v); 
    }
    
    @Override
    public T step(DifferentialSystem<K, T> system, T currentState, Real currentTime, Real deltaTime) {
        
        // Convertiamo i Real usati per i passi temporali nel tipo scalare K
        K dt = realToKScalarConverter.apply(deltaTime); 
        
        // Definiamo le costanti necessarie usando l'API Real
        Real realHalf = new Real(0.5);
        Real realOneSixth = new Real(1.0/6.0);
        K two = val(2.0); // Ancora utile per la moltiplicazione scalare

        // --- Calcolo K1 ---
        // t_k1 = currentTime
        // k1_rate = f(t_k1, currentState)
        T k1_rate = system.derivative(currentState, currentTime);
        // k1 = k1_rate * dt (nello spazio K)
        T k1 = k1_rate.multiplyByScalar(dt); 

        // --- Calcolo K2 ---
        // t_k2 = currentTime + deltaTime / 2
        Real time_k2 = currentTime.add(deltaTime.multiply(realHalf)); // Uso corretto di realHalf
        // state_k2 = currentState + k1_rate * (deltaTime / 2)
        K dtHalfAsK = realToKScalarConverter.apply(deltaTime.multiply(realHalf)); // Uso corretto di realHalf
        T state_k2 = currentState.add(k1_rate.multiplyByScalar(dtHalfAsK));
        // k2_rate = f(t_k2, state_k2)
        T k2_rate = system.derivative(state_k2, time_k2);
        // k2 = k2_rate * dt
        T k2 = k2_rate.multiplyByScalar(dt);

        // --- Calcolo K3 ---
        // t_k3 = currentTime + deltaTime / 2 (uguale a t_k2)
        Real time_k3 = time_k2; 
        // state_k3 = currentState + k2_rate * (deltaTime / 2)
        // Riutilizziamo dtHalfAsK
        T state_k3 = currentState.add(k2_rate.multiplyByScalar(dtHalfAsK));
        // k3_rate = f(t_k3, state_k3)
        T k3_rate = system.derivative(state_k3, time_k3);
        // k3 = k3_rate * dt
        T k3 = k3_rate.multiplyByScalar(dt);
        
        // --- Calcolo K4 ---
        // t_k4 = currentTime + deltaTime
        Real time_k4 = currentTime.add(deltaTime);
        // state_k4 = currentState + k3_rate * deltaTime
        T state_k4 = currentState.add(k3_rate.multiplyByScalar(dt)); // Usiamo dt in K
        // k4_rate = f(t_k4, state_k4)
        T k4_rate = system.derivative(state_k4, time_k4);
        // k4 = k4_rate * dt
        T k4 = k4_rate.multiplyByScalar(dt);

        // --- Calcolo nextState ---
        // nextState = currentState + 1/6 * (K1 + 2*K2 + 2*K3 + K4)
        T sum = k1.add(k2.multiplyByScalar(two))
                  .add(k3.multiplyByScalar(two))
                  .add(k4);
        
        // Moltiplichiamo la somma per 1/6 (convertito in K)
        K oneSixthAsK = realToKScalarConverter.apply(realOneSixth);
        return currentState.add(sum.multiplyByScalar(oneSixthAsK));
    }
    
    @Override
    public T integrate(DifferentialSystem<K, T> system, T initialState, Real startTime, Real endTime, Real deltaTime) {
        T currentState = initialState;
        Real currentTime = startTime;
        
        // Questo loop ora funziona perché usa solo Real e i metodi ComparableElement di Real
        while (currentTime.isLessThan(endTime) || currentTime.isEqual(endTime)) {
            Real stepDt = (currentTime.add(deltaTime).isGreaterThan(endTime)) ? 
                          (Real) endTime.subtract(currentTime) : deltaTime;
            
            currentState = step(system, currentState, currentTime, stepDt);
            currentTime = currentTime.add(stepDt);

            if (stepDt.isZero()) break;
        }
        return currentState;
    }
}
