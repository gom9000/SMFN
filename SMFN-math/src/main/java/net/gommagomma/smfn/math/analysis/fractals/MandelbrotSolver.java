package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;

/**
 * Risolutore specializzato per il Set di Mandelbrot.
 * Implementa IterativeSolver<Problema, Risultato>.
 * P = FixedPointProblem<Complex> (z_{n+1} = z_n^2 + c)
 * T = Complex (il tipo su cui iteriamo)
 * R = Natural (il numero di iterazioni restituito)
 */
public class MandelbrotSolver
implements IterativeSolver<FixedPointProblem<Complex>, Complex, Natural>
{
	private static final double DIVERGENCE_RADIUS_SQ = 4.0;
	private RealField R = RealField.INSTANCE;
	private NaturalSemiring N = NaturalSemiring.INSTANCE;


    @Override
    public Natural solve(FixedPointProblem<Complex> problem, Complex initialGuess, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<Complex> space)
    {
        Complex currentZ = initialGuess;

        for (int iterations = 0; iterations < params.maxIterations; iterations++)
        {
        	// 1. Calcola la "distanza" che ha significato per Mandelbrot.
            // La distanza qui è usata per misurare la divergenza, non la convergenza.
            // Usiamo il raggio quadrato dal centro (il valore di test) come Real.
            Real divergenceMeasure = R.of(currentZ.modulusSquared());

        	// Il Solver chiama il Criterio con la misura (Real)
            if (criteria.isConverged(divergenceMeasure, params, iterations)) {
                return N.of(iterations);
            }
            
            // Prepara per la prossima iterazione
            currentZ = problem.nextIteration(currentZ);
        }

        return N.of(params.maxIterations);
    }
}
