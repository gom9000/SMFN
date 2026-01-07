package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.ScalarRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.ForwardDifferenceDifferentiator;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.NewtonRaphsonSolver;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMorphism;

/**
 * Classe client per dimostrare l'utilizzo del NewtonRaphsonSolver.
 * Risolve la radice di f(x) = x^3 - 2, ovvero cerca x t.c. x = sqrt^3(3).
 */
public class CubicRootSolverClient
{
	static RealField R = RealField.INSTANCE;

	private static class CubicFunction implements Mapping<Real, Real> {
        @Override
        public Real apply(Real x) {
            return R.subtract(R.multiply(R.multiply(x, x), x), R.of(2)); // f(x) = x^3 - 2
        }
    }

    private static class CubicRootProblem implements ScalarRootFindingProblem<Real> {
        private final CubicFunction function = new CubicFunction();

        @Override
        public Mapping<Real, Real> getFunction() {
            return function;
        }

        @Override
        public Mapping<Real, Real> getDerivative() { // qui devi passare l'oggetto SymbolicDifferentialOperator che implementa la derivata simbolica... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! oppure l'oggetto che esegue la derivata sul punto...
            // f'(x) = 3 * x^2
            return x -> R.multiply(R.multiply(x, x), R.of(3)); // 1.2599210498948732
        }

//        @Override
//        public Mapping<Real, Real> getDerivative() {
//            throw new UnsupportedOperationException("Analytic derivative not provided."); //5:  1.2599210498948732
//        }
    }

    private static class AbsoluteDifferenceCriteria implements ConvergenceCriteria
    {
        @Override
        public boolean isConverged(Real distance, ConvergenceParameters params, int iteration) { // qui serve misurare la distanza di uno spazio, in modo da generalizzare il criterio
            return distance.compareTo(params.getTolerance()) <= 0;
        }
    }

	public static void main(String[] args)
	{
		// --- Setup del Problema ---
        CubicRootProblem problem = new CubicRootProblem();
        Real initialGuess = new Real(1.0); // Tentativo iniziale per sqrt(3)
        
        // --- Setup dei Parametri di Convergenza ---
        Real tolerance = new Real(1e-10);
        int maxIterations = 50;
        ConvergenceParameters params = new ConvergenceParameters(tolerance, maxIterations);


        // --- Setup dei Componenti Analitici ---
        
        // Criterio (test logico)
        ConvergenceCriteria criteria = new AbsoluteDifferenceCriteria();

        // Spazio Metrico (calcolo della distanza)
        MetricSpace<Real> space = new RealMetricSpace("Euclidean Real Space");

        // Funzionale (calcola la derivata numerica f'(x)
        Real differentiationStepSize = new Real(1e-6); // h piccolo
        ForwardDifferenceDifferentiator<Real> differentiator = new ForwardDifferenceDifferentiator<>(differentiationStepSize);

        // --- Setup del Solutore ---
        NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(differentiator);

        System.out.println("--- Avvio del Solutore di Newton-Raphson ---");
        System.out.println("Problema: f(x) = x^2 - 2 (radice: sqrt(2))");
        System.out.println("Guess Iniziale: " + initialGuess);
        System.out.println("Tolleranza: " + tolerance);

        try {
            // --- Risoluzione ---
            Real result = solver.solve(problem, initialGuess, criteria, params, space);
            
            System.out.println("\n--- Risultato ---");
            System.out.println("Radice trovata (x): " + result);
            System.out.println("Valore di verifica f(x): " + problem.getFunction().apply(result));
            System.out.println("Valore Atteso (Math.sqrt(2)): " + Math.sqrt(2.0));
            
        } catch (Exception e) {
            System.err.println("Errore durante la soluzione: " + e.getMessage());
        }
	}
}
