package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.ScalarRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.MetricSpace;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMorphism;

/**
 * Solutore Newton-Raphson.
 * Nota: Usiamo LinearMorphism<R, R> invece di Morphism<R, R> perché 
 * i nostri differenziatori lavorano su spazi lineari/vettoriali.
 */
public class NewtonRaphsonSolver<R extends FieldElement<R>> 
    implements IterativeSolver<ScalarRootFindingProblem<R>, R, R>
{            
    // Cambiamo Morphism in LinearMorphism per coerenza con il differenziatore numerico
    private final Morphism<LinearMorphism<R, R>, LinearMorphism<R, R>> differentiator;

    public NewtonRaphsonSolver(Morphism<LinearMorphism<R, R>, LinearMorphism<R, R>> differentiator) {
        this.differentiator = differentiator;
    }

    @Override
    public R solve(ScalarRootFindingProblem<R> problem, R initialGuess, 
                    ConvergenceCriteria criteria, ConvergenceParameters params, 
                    MetricSpace<?, R> space) { 
        
        R current = initialGuess;
        
        // 1. Otteniamo la funzione dal problema
        // Se ScalarRootFindingProblem restituisce Morphism<R, R>
        Morphism<R, R> f = problem.getFunction();
        
        for (int k = 0; k < params.maxIterations; k++) {
            R previous = current; 
            
            // 2. Valore della funzione f(x_k)
            R fOfX = f.apply(current); // Usiamo apply() che è lo standard di Morphism
            
            // 3. Calcolo della derivata f'(x_k)
            R fPrimeOfX;
            try {
                // Prova la derivata fornita dal problema
                // Se getDerivative() restituisce un Morphism/Mapping, usiamo apply()
                fPrimeOfX = problem.getDerivative().apply(current);
            } catch (UnsupportedOperationException | NullPointerException e) {
                // Fallback: Differenziazione numerica
                // Il differenziatore lavora su LinearMorphism. 
                // Dobbiamo assicurarci che f sia trattabile come tale.
                if (f instanceof LinearMorphism) {
                    fPrimeOfX = differentiator.apply((LinearMorphism<R, R>) f).apply(current);
                } else {
                    // Se f non è LinearMorphism, dobbiamo "wrapparla" o gestire l'errore
                    throw new IllegalArgumentException("La funzione del problema deve essere un LinearMorphism per la differenziazione numerica.");
                }
            }
            
            // 4. Controllo di stabilità
            if (fPrimeOfX.isZero()) { 
                throw new ArithmeticException("Derivata nulla all'iterazione " + k);
            }
            
            // 5. Formula di Newton: x_{k+1} = x_k - f(x_k) / f'(x_k)
            R step = fOfX.divide(fPrimeOfX);
            current = current.subtract(step); 

            // 6. Controllo convergenza
            Real distance = space.distance(current, previous); 
            if (criteria.isConverged(distance, params, k + 1)) {
                return current;
            }
        }

        throw new IllegalStateException("Convergenza fallita dopo " + params.maxIterations + " iterazioni.");
    }
}