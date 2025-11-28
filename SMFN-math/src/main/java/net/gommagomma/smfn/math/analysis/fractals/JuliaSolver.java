package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.core.IterativeSystem;
import net.gommagomma.smfn.math.analysis.core.MetricConvergenceTest;
import net.gommagomma.smfn.math.analysis.core.MetricSolver;
import net.gommagomma.smfn.math.linearalgebra.core.structures.MetricSpace;


public class JuliaSolver
implements MetricSolver<Complex, Integer>
{
    private static final double DIVERGENCE_RADIUS_SQ = 4.0;
    private final Complex constantC;


    public JuliaSolver(Complex constantC) {
        this.constantC = constantC;
    }


    // Il metodo solve() è esattamente lo stesso del MandelbrotSolver
    @Override
    public Integer solve(Complex initial, IterativeSystem<Complex> system, MetricConvergenceTest<Complex> test, Real tolerance, MetricSpace<Complex> space)
    {
        Complex currentZ = initial;
        Complex previousZ = null; 

        for (int iterations = 0; iterations < test.getMaxIterations(); iterations++)
        {
            if (test.isConverged(currentZ, previousZ, tolerance, iterations, space)) {
                return iterations; 
            }
            
            previousZ = currentZ;
            currentZ = system.nextIteration(currentZ);
        }
        
        return test.getMaxIterations(); 
    }


    /**
     * Calcola il numero di iterazioni necessarie affinché un punto iniziale 'z0' diverga,
     * usando la costante 'c' fissata nel costruttore.
     */
    public int evaluateJuliaPoint(Complex z0_initial, int maxIterations)
    {
        final Complex c = this.constantC;
        
        // Il sistema di iterazione: z_{n+1} = z_n^2 + c
        // NOTA: 'c' è la costante fissata dal costruttore, non l'input variabile
        IterativeSystem<Complex> system = current -> current.multiply(current).add(c);
        
        // Il test di convergenza/divergenza (|z_n|^2 > 4)
        MetricConvergenceTest<Complex> divergenceTest = new MetricConvergenceTest<Complex>()
        {
            @Override
            public boolean isConverged(Complex current, Complex previous, Real tolerance, int iteration, MetricSpace<Complex> space)
            {
                if (current == null) return false;
                return current.modulusSquared() > DIVERGENCE_RADIUS_SQ;
            }

            @Override
            public int getMaxIterations() {
                return maxIterations;
            }
        };        

        // Risolve partendo dal punto iniziale z0_initial (che è l'input variabile del set di Julia)
        // Usiamo argomenti dummy per tolleranza e spazio metrico, come nel MandelbrotSolver
        return solve(z0_initial, system, divergenceTest, new Real(0.0), null);
    }
}
