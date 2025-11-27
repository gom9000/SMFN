

package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.core.MetricConvergenceTest;
import net.gommagomma.smfn.math.analysis.core.IterativeSystem;
import net.gommagomma.smfn.math.analysis.core.MetricSolver;
import net.gommagomma.smfn.math.linearalgebra.core.MetricSpace;


public class MandelbrotSolver
implements MetricSolver<Complex, Integer>
{
	private static final double DIVERGENCE_RADIUS_SQ = 4.0;


    @Override
    public Integer solve(Complex initial, IterativeSystem<Complex> system, MetricConvergenceTest<Complex> test, Real tolerance, MetricSpace<Complex> space)
    {
        Complex currentZ = initial;
        Complex previousZ = null; // Necessario per la nuova firma di isConverged

        for (int iterations = 0; iterations < test.getMaxIterations(); iterations++)
        {
            // Controlla se la condizione di stop (divergenza) è soddisfatta
            // Il test riceve currentZ e previousZ
            if (test.isConverged(currentZ, previousZ, tolerance, iterations, space)) {
                return iterations; // Ritorna il numero di iterazioni prima della divergenza
            }
            
            // Prepara per la prossima iterazione
            previousZ = currentZ;
            currentZ = system.nextIteration(currentZ);
        }
        
        // Se si esce dal loop, significa che non ha divergito entro maxIterations
        return test.getMaxIterations(); 
    }


    /**
     * Helper specifico per Mandelbrot che incapsula la logica di divergenza.
     */
    public int evaluateMandelbrotPoint(Complex c, int maxIterations)
    {
        // Il sistema specifico per Mandelbrot: z_{n+1} = z_n^2 + c
        IterativeSystem<Complex> system = current -> current.multiply(current).add(c);
        
        // Il test di convergenza/divergenza
        MetricConvergenceTest<Complex> divergenceTest = new MetricConvergenceTest<Complex>()
        {
            @Override
            public boolean isConverged(Complex current, Complex previous, Real tolerance, int iteration, MetricSpace<Complex> space)
            {
                // Per Mandelbrot, la condizione di stop è solo |z_n|^2 > 4.
                // Ignoriamo previous, tolerance, iteration, space.
                if (current == null) return false; // Prima iterazione
                return current.modulusSquared() > DIVERGENCE_RADIUS_SQ;
            }

            @Override
            public int getMaxIterations() {
                return maxIterations;
            }
        };        

        // Risolve partendo da z_0 = 0
        return solve(Complex.ZERO, system, divergenceTest, new Real(0.0), null);
    }
}
