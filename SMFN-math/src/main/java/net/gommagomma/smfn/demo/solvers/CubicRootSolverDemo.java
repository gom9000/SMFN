package net.gommagomma.smfn.demo.solvers;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.core.problems.ScalarRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.ForwardDifferenceDifferentiator;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.NewtonRaphsonSolver;

/**
 * Risolve la radice di f(x) = x^3 - 2, cioe' la radice cubica di 2.
 *
 * Mostra entrambi i percorsi di NewtonRaphsonSolver: derivata numerica di
 * fallback (nessuna analitica fornita) e derivata analitica (fornita tramite
 * DifferentiableScalarProblem, verificata con instanceof dal solver, non con
 * un default che lancia eccezione).
 */
public class CubicRootSolverDemo
{
	private static final RealField R = RealField.INSTANCE;

	/** f(x) = x^3 - 2, senza derivata analitica: il solver ricade sul differenziatore numerico. */
	private static class CubicRootProblem implements ScalarRootFindingProblem<Real>
	{
		@Override
		public Real apply(Real x) {
			return R.subtract(R.multiply(R.multiply(x, x), x), R.of(2));
		}
	}

	/** Stessa funzione, ma fornisce la derivata analitica f'(x) = 3x^2. */
	private static class CubicRootProblemWithDerivative extends CubicRootProblem
	implements DifferentiableScalarProblem<Real>
	{
		@Override
		public Mapping<Real, Real> getDerivative() {
			return x -> R.multiply(new Real(3.0), R.multiply(x, x));
		}
	}

	public static void main(String[] args)
	{
		Real initialGuess = new Real(1.0);
		Real tolerance = new Real(1e-10);
		StoppingParameters params = new StoppingParameters(tolerance, 50);

		// Spazio metrico: distanza euclidea su Real. Nessuna classe a parte serve,
		// e' un'interfaccia funzionale -- stesso pattern gia' usato altrove.
		MetricSpace<Real> space = (a, b) -> R.subtract(a, b).abs();

		ForwardDifferenceDifferentiator<Real> numericFallback = new ForwardDifferenceDifferentiator<>(R, new Real(1e-6));
		NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, numericFallback);

		double expected = Math.cbrt(2.0);
		System.out.println("--- Newton-Raphson: radice di f(x) = x^3 - 2 (radice cubica di 2) ---");
		System.out.println("Atteso: " + expected);
		System.out.println("Guess iniziale: " + initialGuess + ", tolleranza: " + tolerance);

		System.out.println("\n[1] Senza derivata analitica (fallback numerico):");
		SolverResult<Real> outcome1 = solver.solve(new CubicRootProblem(), initialGuess,
			(distance, p, it) -> distance.compareTo(p.getTolerance()) <= 0, params, space);
		Real result1 = outcome1.getValue();
		System.out.println("Radice trovata: " + result1 + " (stato: " + outcome1.getStatus()
			+ ", iterazioni: " + outcome1.getIterationsExecuted() + ")");

		System.out.println("\n[2] Con derivata analitica (DifferentiableScalarProblem):");
		SolverResult<Real> outcome2 = solver.solve(new CubicRootProblemWithDerivative(), initialGuess,
			(distance, p, it) -> distance.compareTo(p.getTolerance()) <= 0, params, space);
		Real result2 = outcome2.getValue();
		System.out.println("Radice trovata: " + result2 + " (stato: " + outcome2.getStatus()
			+ ", iterazioni: " + outcome2.getIterationsExecuted() + ")");

		System.out.println("\nI due percorsi coincidono: " + result1.equals(result2));
	}
}
