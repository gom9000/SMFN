package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;

/**
 * Solutore iterativo generico basato sull'algoritmo del tempo di fuga (Escape Time)
 * per mappe dinamiche nel piano complesso.
 */
public class EscapeTimeSolver
implements IterativeSolver<FixedPointProblem<Complex>, Complex, Complex>
{
    private final RealField R = RealField.INSTANCE;

    /**
     * Valuta l'orbita complessa generata dal problema di punto fisso fino alla fuga oltre il raggio critico
     * o fino al limite massimo di iterazioni.
     *
     * @param problem Il problema di punto fisso che definisce la mappa complessa z_{k+1} = G(z_k)
     * @param initialGuess Il valore complesso iniziale z_0 per il tracciamento dell'orbita
     * @param criteria Il criterio di arresto (utilizzato per valutare se la misura di divergenza supera la soglia di fuga)
     * @param params I parametri numerici che specificano il numero massimo di iterazioni N_max
     * @param space Lo spazio metrico sui numeri complessi, usato per calcolare la distanza tra gli ultimi due iterati
     * @return Il SolverResult con l'ultimo iterato calcolato, il numero di iterazioni eseguite e l'esito (DIVERGED o MAX_ITERATIONS_REACHED)
     */
    @Override
    public SolverResult<Complex> solve(FixedPointProblem<Complex> problem, Complex initialGuess, StoppingCriteria criteria, StoppingParameters params, MetricSpace<Complex> space)
    {
        Complex currentZ = initialGuess;
        Complex previousZ = initialGuess;

        for (int iterations = 0; iterations < params.maxIterations; iterations++)
        {
            Real divergenceMeasure = R.of(currentZ.modulusSquared());

            if (criteria.shouldStop(divergenceMeasure, params, iterations)) {
                Real finalStepDistance = space.distance(currentZ, previousZ);
                return new IterativeSolverResult<>(currentZ, TerminationStatus.DIVERGED, iterations, finalStepDistance);
            }

            previousZ = currentZ;
            currentZ = problem.nextIteration(currentZ);
        }

        Real finalStepDistance = space.distance(currentZ, previousZ);
        return new IterativeSolverResult<>(currentZ, TerminationStatus.MAX_ITERATIONS_REACHED, params.maxIterations, finalStepDistance);
    }
}
