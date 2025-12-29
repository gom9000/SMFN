package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.MetricSpace;

/**
 * Risolutore specializzato per il Set di Mandelbrot.
 * Implementa IterativeSolver<Problema, Risultato>.
 * P = FixedPointProblem<Complex> (il sistema z_{n+1} = z_n^2 + c)
 * R = Integer (il numero di iterazioni prima della divergenza)
 */
public class MandelbrotSolver
implements IterativeSolver<FixedPointProblem<Complex>, Complex, Natural>
{
	private static final double DIVERGENCE_RADIUS_SQ = 4.0;


    @Override
    public Natural solve(FixedPointProblem<Complex> problem, Complex initialGuess, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<?, Complex> space)
    {
        Complex currentZ = initialGuess;

        for (int iterations = 0; iterations < params.maxIterations; iterations++)
        {
        	// 1. Calcola la "distanza" che ha significato per Mandelbrot.
            // La distanza qui è usata per misurare la divergenza, non la convergenza.
            // Usiamo il raggio quadrato dal centro (il valore di test) come Real.
            Real divergenceMeasure = new Real(currentZ.modulusSquared());

        	// Il Solver chiama il Criterio con la misura (Real)
            if (criteria.isConverged(divergenceMeasure, params, iterations)) {
                return new Natural(iterations);
            }
            
            // Prepara per la prossima iterazione
            currentZ = problem.nextIteration(currentZ);
        }

        return new Natural(params.maxIterations);
    }


    /**
     * Helper specifico per Mandelbrot che incapsula la logica di divergenza.
     */
    public Natural evaluateMandelbrotPoint(Complex c, int maxIterations)
    {
        // z_{n+1} = z_n^2 + c
        FixedPointProblem<Complex> problem = current -> current.multiply(current).add(c);
        
         // Il test di convergenza/divergenza (|z_n|^2 > 4)
        ConvergenceCriteria divergenceTest = (divergenceMeasure, params, iteration) -> {
        	return divergenceMeasure.getValue() > DIVERGENCE_RADIUS_SQ;
        };        

        ConvergenceParameters params = new ConvergenceParameters(RealField.getInstance().zero(), maxIterations);
        return solve(problem, new Complex(0.0, 0.0), divergenceTest, params, null);
    }
}
