package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.analysis.core.problems.ScalarRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;
import net.gommagomma.smfn.math.analysis.core.functionals.Functional;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.MetricSpace;
import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
 * Solutore per Problemi di Ricerca delle Radici SCALARI (ScalarRootFindingProblem)
 * che utilizza il metodo di Newton-Raphson.
 *
 * R: Il tipo del campo (es. Real, ComplexElement), deve essere un FieldElement.
 * Implementa IterativeSolver<Problema, Soluzione>.
 */
public class NewtonRaphsonSolver<R extends FieldElement<R>> 
implements IterativeSolver<ScalarRootFindingProblem<R>, R, R>
{            
    private final Functional<R, R, R> differentiator;


    public NewtonRaphsonSolver(Functional<R, R, R> differentiator) {
        this.differentiator = differentiator;
    }


    @Override
    public R solve(ScalarRootFindingProblem<R> problem, R initialGuess, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<?, R> space) {
        
        R current = initialGuess; // Contiene x_{k}
        R next;                   // Contiene x_{k+1}
        
        for (int k = 0; k < params.getMaxIterations(); k++) {
            
            // 1. Memorizza lo stato attuale per il controllo successivo
            R previous = current; 
            
            // 2. Calcola f(x_k) e f'(x_k)
            R f_of_x = problem.getFunction().apply(current);
            R f_prime_of_x = differentiator.evaluate(problem.getFunction(), current);
            
            // 3. Controllo di stabilità: f'(x_k) ~ 0 ?
            if (f_prime_of_x.isZero()) { 
                throw new ArithmeticException("Zero derivative encountered. Solution diverges or is near a multiple root.");
            }
            
            // 4. Calcola il nuovo stato: x_{k+1} = x_k - f(x_k) / f'(x_k)
            R step = f_of_x.divide(f_prime_of_x);
            next = current.subtract(step); 
            
            // 5. Aggiorna lo stato attuale
            current = next;

            // 6. Controlla la convergenza (dalla prima iterazione, ma dopo il calcolo)
            if (k >= 0) { 
                Real distance = space.distance(current, previous); 
                if (criteria.isConverged(distance, params, k + 1)) {
                    return current;
                }
            }
        }

        throw new IllegalStateException("Solution did not converge within " + params.getMaxIterations() + " iterations.");
    }
}