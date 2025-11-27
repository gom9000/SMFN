// net.gommagomma.smfn.math.analysis.fractals.JuliaFunction.java
package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.core.MathFunction;
import net.gommagomma.smfn.math.analysis.core.IterativeSystem;
import net.gommagomma.smfn.math.analysis.core.MetricConvergenceTest;

/**
 * Rappresenta la funzione matematica per un set di Julia specifico (definito da una costante C).
 */
public class JuliaFunction implements MathFunction<Complex, Real> {
    
    // Il solutore che esegue il conteggio delle iterazioni
    private final MandelbrotSolver solver = new MandelbrotSolver();
    private final int maxIterations;
    private final Complex constantC; // La costante C specifica per questo set di Julia

    public JuliaFunction(Complex constantC, int maxIterations) {
        this.constantC = constantC;
        this.maxIterations = maxIterations;
    }

    @Override
    public Real evaluate(Complex input) {
        // A differenza di Mandelbrot dove input=c e z0=0, 
        // in Julia input=z0 e C è la costante fissa.
        
        // Definiamo il sistema iterativo specifico per questo C fisso
        IterativeSystem<Complex> juliaSystem = new IterativeSystem<Complex>() {
            @Override
            public Complex nextIteration(Complex current) {
                // z_{n+1} = z_n^2 + C
                return current.multiply(current).add(constantC);
            }
        };

        // Definiamo il test di convergenza standard (|z|^2 > 4)
        MetricConvergenceTest<Complex> divergenceTest = current -> current.modulusSquared() > 4.0;

        // Eseguiamo il solve, partendo da input (che è z0)
        int iterations = solver.solve(input, juliaSystem, divergenceTest, maxIterations);
        
        return new Real((double) iterations);
    }
}
