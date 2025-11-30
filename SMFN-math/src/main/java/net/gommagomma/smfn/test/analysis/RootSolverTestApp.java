package net.gommagomma.smfn.test.analysis;

import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.models.IterativeSystem;
import net.gommagomma.smfn.math.analysis.solvers.core.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.solvers.core.ConvergenceTest;
import net.gommagomma.smfn.math.analysis.solvers.differential.CentralDifferenceDifferentiator;
import net.gommagomma.smfn.math.analysis.solvers.differential.NumericalDifferentiator;
import net.gommagomma.smfn.math.analysis.solvers.iterative.NewtonRaphsonSolver;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.MetricSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.ScalarMetricSpace;
import net.gommagomma.smfn.math.linearalgebra.real.RealVectorSpace;

public class RootSolverTestApp
{
	public static void main(String[] args)
	{
		// Esempio: Trovare la radice di f(x) = x^2 - 4, derivata f'(x) = 2x

		// 1. Definisci le funzioni (supponendo di avere classi per questo)
		MathFunction<Real, Real> f_x = input -> input.multiply(input).subtract(new Real(4.0));
		MathFunction<Real, Real> f_prime_x = input -> input.multiply(new Real(2.0));

		// 2.A Scegli il differenziatore numerico e Crea il sistema iterativo Newton-Raphson
		NumericalDifferentiator<Real> differentiator = new CentralDifferenceDifferentiator<>();
		Real h = new Real(0.00001); // Step size piccolo
		IterativeSystem<Real> newtonSystemA = NewtonRaphsonSolver.createSystemNumerical(f_x, differentiator, h);

		// 2.B Crea il sistema iterativo Newton-Raphson
		IterativeSystem<Real> newtonSystemB = NewtonRaphsonSolver.createSystem(f_x, f_prime_x);

		// 3. Definisci il test di convergenza
        Real tolerance = new Real(0.000001); // Precisione richiesta per la soluzione
        int maxIter = 100;                   // Limite massimo di passi
        ConvergenceParameters params = new ConvergenceParameters(tolerance, maxIter);
        ConvergenceTest<Real> convergenceTest = (current, previous, convParams, iteration, space) -> {    
            // Controlla se il limite di iterazioni è stato raggiunto
            if (iteration >= convParams.maxIterations) {
                System.out.println("[SOLVER] Non convergente: superato il numero max di iterazioni.");
                return true; 
            }
            
            // Ignora il controllo della distanza alla prima iterazione
            if (previous == null) {
                return false;
            }
            
            // Controlla se la distanza tra due passi successivi è minore della tolleranza
            Real distance = space.distance(current, previous);
            if (distance.modulus() < convParams.tolerance.modulus()) {
                System.out.println("[SOLVER] Convergenza raggiunta in " + iteration + " iterazioni.");
                return true;
            }
            
            return false;
        };

		// 4. Esegui il solver
        NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>();
        Real puntoIniziale = new Real(10.0); // Partiamo da x0 = 10.0

        MetricSpace<Real> realSpace = new ScalarMetricSpace<>("Euclidean Scalar Space"); 

        Real root = solver.solve(
            puntoIniziale,
            newtonSystemB, 
            convergenceTest,
            params,                          
            realSpace
        );

        System.out.println("Parametri: x0=" + puntoIniziale + ", Tolleranza=" + tolerance);
		System.out.println("La radice trovata è: " + root); // Dovrebbe essere vicino a 2.0

	}
}
