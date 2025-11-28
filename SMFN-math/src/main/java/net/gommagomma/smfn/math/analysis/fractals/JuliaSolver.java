package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.core.algebra.numeric.Complex;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.core.analysis.ConvergenceParameters;
import net.gommagomma.smfn.math.core.analysis.IterativeSystem;
import net.gommagomma.smfn.math.core.analysis.MetricConvergenceTest;
import net.gommagomma.smfn.math.core.analysis.MetricSolver;
import net.gommagomma.smfn.math.core.linearalgebra.structures.MetricSpace;


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
    public Integer solve(Complex initial, IterativeSystem<Complex> system, MetricConvergenceTest<Complex> test, ConvergenceParameters params, MetricSpace<Complex> space)
    {
        Complex currentZ = initial;
        Complex previousZ = null; 

        for (int iterations = 0; iterations < params.maxIterations; iterations++)
        {
            if (test.isConverged(currentZ, previousZ, params, iterations, space)) {
                return iterations; // Ritorna il numero di iterazioni prima della divergenza
            }

            previousZ = currentZ;
            currentZ = system.nextIteration(currentZ);
        }
        
        return params.maxIterations; 
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
        MetricConvergenceTest<Complex> divergenceTest = (current, previous, params, iteration, space) -> {
        	if (current == null) return false; // Prima iterazione
        	return current.modulusSquared() > DIVERGENCE_RADIUS_SQ;
        };        

        // Risolve partendo dal punto iniziale z0_initial (che è l'input variabile del set di Julia)
        ConvergenceParameters params = new ConvergenceParameters(new Real(0.0), maxIterations);
        return solve(z0_initial, system, divergenceTest, params, null);  
    }
}
