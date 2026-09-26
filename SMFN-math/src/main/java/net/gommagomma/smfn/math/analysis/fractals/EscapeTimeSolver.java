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
 * Solutore iterativo generico basato sull'algoritmo del tempo di fuga (Escape Time)
 * per mappe dinamiche nel piano complesso.
 * 
 * A differenza dei solutori iterativi standard orientati alla convergenza, questo solutore
 * applica una semantica inversa: il criterio di arresto valuta il superamento del raggio di fuga
 * per rilevare la divergenza di un'orbita z_{k+1} = G(z_k).
 * Il risultato restituito e' un numero naturale N che rappresenta il numero di iterazioni impiegate per la divergenza
 * o il valore limite di iterazioni massime specificato nei parametri.
 */
public class EscapeTimeSolver
implements IterativeSolver<FixedPointProblem<Complex>, Complex, Natural>
{
    private final RealField R = RealField.INSTANCE;
    private final NaturalSemiring N = NaturalSemiring.INSTANCE;

    /**
     * Valuta l'orbita complessa generata dal problema di punto fisso fino alla fuga oltre il raggio critico
     * o fino al limite massimo di iterazioni.
     *
     * @param problem Il problema di punto fisso che definisce la mappa complessa z_{k+1} = G(z_k)
     * @param initialGuess Il valore complesso iniziale z_0 per il tracciamento dell'orbita
     * @param criteria Il criterio di arresto (utilizzato per valutare se la misura di divergenza supera la soglia di fuga)
     * @param params I parametri numerici che specificano il numero massimo di iterazioni N_max
     * @param space Lo spazio metrico sui numeri complessi per la misurazione delle distanze
     * @return Il conteggio delle iterazioni eseguite fino alla fuga o N_max come tipo {@link Natural}
     */
    @Override
    public Natural solve(FixedPointProblem<Complex> problem, Complex initialGuess, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<Complex> space)
    {
        Complex currentZ = initialGuess;

        for (int iterations = 0; iterations < params.maxIterations; iterations++)
        {
            Real divergenceMeasure = R.of(currentZ.modulusSquared());

            if (criteria.isConverged(divergenceMeasure, params, iterations)) {
                return N.of(iterations);
            }
            
            currentZ = problem.nextIteration(currentZ);
        }

        return N.of(params.maxIterations);
    }
}