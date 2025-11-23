

package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.analysis.core.ConvergenceTest;
import net.gommagomma.smfn.math.analysis.core.IterativeSystem;
import net.gommagomma.smfn.math.analysis.core.Solver;


public class MandelbrotSolver
implements Solver<Complex, Integer>
{
    private static final double DIVERGENCE_RADIUS_SQ = 4.0;

    @Override
    public Integer solve(Complex initial, IterativeSystem<Complex> system, ConvergenceTest<Complex> test, int maxIterations)
    {
        Complex z = initial;
        int iterations = 0;

        while (iterations < maxIterations)
        {
            z = system.nextIteration(z);
            
            if (test.isConverged(z)) {
                return iterations;
            }
            iterations++;
        }
        
        return maxIterations;
    }

    // Un metodo helper per l'uso specifico di Mandelbrot
    public int evaluateMandelbrotPoint(Complex c, int maxIterations)
    {
        // Il sistema specifico per Mandelbrot: z_{n+1} = z_n^2 + c
        IterativeSystem<Complex> system = current -> current.multiply(current).add(c);
        
        // Il test di convergenza: |z_n|^2 > 4
        ConvergenceTest<Complex> divergenceTest = current -> current.modulusSquared() > DIVERGENCE_RADIUS_SQ;
        
        // Risolve partendo da z_0 = 0
        return solve(Complex.ZERO, system, divergenceTest, maxIterations);
    }
}
