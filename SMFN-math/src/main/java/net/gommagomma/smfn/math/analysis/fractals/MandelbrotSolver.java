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
 * Solutore iterativo specializzato per il calcolo dell'insieme di Mandelbrot.
 * 
 * A differenza dei solutori iterativi standard orientati alla convergenza,
 * questo solutore applica una semantica inversa: il criterio di arresto valuta il superamento del raggio di fuga per rilevare la divergenza.
 * Il risultato restituito dal metodo {@link #solve} e' un numero naturale N (istanza di {@link Natural}) che rappresenta:
 * Il numero di iterazioni impiegate per la divergenza) o il valore limite di iterazioni massime specificato nei parametri.
 */
public class MandelbrotSolver
implements IterativeSolver<FixedPointProblem<Complex>, Complex, Natural>
{
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
            // Usa il raggio quadrato dal centro (il valore di test) come Real.
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
