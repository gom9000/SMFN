

package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.core.MetricConvergenceTest;
import net.gommagomma.smfn.math.analysis.core.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.IterativeSystem;
import net.gommagomma.smfn.math.analysis.core.MetricSolver;
import net.gommagomma.smfn.math.linearalgebra.core.structures.MetricSpace;


public class MandelbrotSolver
implements MetricSolver<Complex, Integer>
{
	private static final double DIVERGENCE_RADIUS_SQ = 4.0;


    @Override
    public Integer solve(Complex initial, IterativeSystem<Complex> system, MetricConvergenceTest<Complex> test, ConvergenceParameters params, MetricSpace<Complex> space)
    {
        Complex currentZ = initial;
        Complex previousZ = null;

        for (int iterations = 0; iterations < params.maxIterations; iterations++)
        {
            // Controlla se la condizione di stop (divergenza) è soddisfatta
            // Il test riceve currentZ e previousZ
            if (test.isConverged(currentZ, previousZ, params, iterations, space)) {
                return iterations; // Ritorna il numero di iterazioni prima della divergenza
            }
            
            // Prepara per la prossima iterazione
            previousZ = currentZ;
            currentZ = system.nextIteration(currentZ);
        }
        
        // Se si esce dal loop, significa che non ha divergito entro maxIterations
        return params.maxIterations; 
    }


    /**
     * Helper specifico per Mandelbrot che incapsula la logica di divergenza.
     */
    public int evaluateMandelbrotPoint(Complex c, int maxIterations)
    {
        // Il sistema specifico per Mandelbrot: z_{n+1} = z_n^2 + c
        IterativeSystem<Complex> system = current -> current.multiply(current).add(c);
        
     // Il test di convergenza/divergenza (|z_n|^2 > 4)
        MetricConvergenceTest<Complex> divergenceTest = (current, previous, params, iteration, space) -> {
        	if (current == null) return false; // Prima iterazione
        	return current.modulusSquared() > DIVERGENCE_RADIUS_SQ;
        };        

        ConvergenceParameters params = new ConvergenceParameters(new Real(0.0), maxIterations);
        return solve(Complex.ZERO, system, divergenceTest, params, null);
    }
}
