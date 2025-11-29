package net.gommagomma.smfn.math.analysis.solvers;

import net.gommagomma.smfn.math.core.algebra.MathFunction;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.analysis.ConvergenceParameters;
import net.gommagomma.smfn.math.core.analysis.IterativeSystem;
import net.gommagomma.smfn.math.core.analysis.MetricConvergenceTest;
import net.gommagomma.smfn.math.core.analysis.MetricSolver;
import net.gommagomma.smfn.math.core.analysis.NumericalDifferentiator;
import net.gommagomma.smfn.math.core.linearalgebra.structures.MetricSpace;


public class NewtonRaphsonSolver<K extends FieldElement<K>>
implements MetricSolver<K, K>
{
    @Override
    public K solve(
        K initial, 
        IterativeSystem<K> system, 
        MetricConvergenceTest<K> test, 
        ConvergenceParameters params,
        MetricSpace<K> space          
    ) {
        K current = initial;
        K previous = null;
        int iteration = 0;

        while (true) {
            // Controlla la convergenza prima di calcolare la prossima iterazione
            if (test.isConverged(current, previous, params, iteration, space)) {
                return current;
            }

            iteration++;
            previous = current;
            current = system.nextIteration(current); // Applica la formula x_{n+1}
        }
    }

    // --- Helper per creare il sistema iterativo specifico di Newton-Raphson (Analitico) ---
    
    /**
     * Crea un sistema iterativo per Newton-Raphson data la funzione f(x) e la sua derivata f'(x).
     */
    public static <K extends FieldElement<K>> IterativeSystem<K> createSystem(
        MathFunction<K, K> function, 
        MathFunction<K, K> derivative
    ) {
        return inputX -> {
            K fx = function.evaluate(inputX);
            K fPrimeX = derivative.evaluate(inputX);
            
            if (fPrimeX.isZero()) {
                throw new ArithmeticException("Derivative is zero at x=" + inputX + ". Cannot continue Newton-Raphson.");
            }
            
            K step = fx.divide(fPrimeX);
            return inputX.subtract(step); // x_{n+1} = x_n - f(x_n)/f'(x_n)
        };
    }

    // --- Helper per creare il sistema iterativo (Numerico) ---

    /**
     * Crea un sistema iterativo per Newton-Raphson usando la differenziazione numerica.
     */
    public static <K extends FieldElement<K>> IterativeSystem<K> createSystemNumerical(
        MathFunction<K, K> function,
        NumericalDifferentiator<K> differentiator,
        K stepSizeH 
    ) {
        return inputX -> {
            K fPrimeX = differentiator.derivativeAt(function, inputX, stepSizeH);
            
            if (fPrimeX.isZero()) {
                throw new ArithmeticException("Derivative is zero (numerically) at x=" + inputX + ". Cannot continue Newton-Raphson.");
            }

            K fx = function.evaluate(inputX);
            K step = fx.divide(fPrimeX);
            return inputX.subtract(step);
        };
    }
}