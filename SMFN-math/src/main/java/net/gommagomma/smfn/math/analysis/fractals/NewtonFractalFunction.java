package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.NewtonRaphsonSolver;

/**
 * Funzione complessa che assegna a ciascun punto iniziale z_0 del piano complesso
 * il numero di iterazioni impiegate dal metodo di Newton-Raphson per convergere
 * (o per esaurire il budget) verso una radice del polinomio fornito.
 * <p>
 * Il polinomio -- e la sua derivata analitica -- e' un parametro del costruttore, non e' fissato
 * internamente: cosi' come {@link JuliaFunction} parametrizza la costante c, questa classe
 * parametrizza il problema differenziabile da risolvere, restando riusabile per qualunque polinomio
 * invece di richiedere una nuova classe per ognuno. {@link #forCubicMinusOne(int)} offre una
 * configurazione pronta per il classico frattale di Newton su p(z) = z^3 - 1.
 * </p>
 * <p>
 * Il conteggio delle iterazioni si legge direttamente da {@link SolverResult#getIterationsExecuted()}:
 * non serve piu' catturare eccezioni per dedurre il mancato raggiungimento della convergenza entro
 * il budget, ne' duplicare la logica del solutore per contarle a mano.
 * </p>
 */
public class NewtonFractalFunction
implements Mapping<Complex, Natural>
{
	private final ComplexField C = ComplexField.INSTANCE;
	private final RealField R = RealField.INSTANCE;
	private final NaturalSemiring N = NaturalSemiring.INSTANCE;

	private final NewtonRaphsonSolver<Complex> solver;
	private final DifferentiableScalarProblem<Complex> problem;
	private final MetricSpace<Complex> complexMetricSpace;

	private StoppingParameters cachedParams;
	private final StoppingCriteria convergenceTest;

	/**
     * Costruisce la funzione per il frattale di Newton associato al polinomio fornito.
     *
     * @param problem Il problema differenziabile p(z) = 0 di cui cercare le radici, con derivata analitica p'(z)
     * @param maxIterations Il numero massimo di iterazioni N_max da calcolare prima di arrestare il processo
     */
	public NewtonFractalFunction(DifferentiableScalarProblem<Complex> problem, int maxIterations) {
		this.problem = problem;

		// Solutore ancorato al campo complesso C: nessun fallback numerico necessario, la derivata e' sempre analitica
		this.solver = new NewtonRaphsonSolver<>(C, null);

		// Spazio metrico complesso basato sulla distanza euclidea d(z1, z2) = |z1 - z2|
		this.complexMetricSpace = (z1, z2) -> R.of(Math.sqrt(C.subtract(z1, z2).modulusSquared()));

		// Criterio di convergenza: ci fermiamo quando la distanza tra due iterati d(z_{k+1}, z_k) e' inferiore alla tolleranza
		this.convergenceTest = (distance, params, iteration) -> distance.getValue() < params.getTolerance().getValue();

		setMaxIterations(maxIterations);
	}

	/**
     * Costruisce la funzione per il classico frattale di Newton su p(z) = z^3 - 1 (radice cubica dell'unita'),
     * con derivata analitica p'(z) = 3z^2.
     *
     * @param maxIterations Il numero massimo di iterazioni N_max da calcolare prima di arrestare il processo
     * @return Una nuova istanza configurata per p(z) = z^3 - 1
     */
	public static NewtonFractalFunction forCubicMinusOne(int maxIterations) {
		ComplexField c = ComplexField.INSTANCE;
		DifferentiableScalarProblem<Complex> cubicMinusOne = new DifferentiableScalarProblem<Complex>() {
			@Override
			public Complex apply(Complex z) {
				// p(z) = z^3 - 1
				Complex zSquared = c.multiply(z, z);
				Complex zCubed = c.multiply(zSquared, z);
				return c.subtract(zCubed, c.one());
			}

			@Override
			public Mapping<Complex, Complex> getDerivative() {
				// p'(z) = 3 * z^2
				return z -> c.multiply(c.of(3.0, 0.0), c.multiply(z, z));
			}
		};
		return new NewtonFractalFunction(cubicMinusOne, maxIterations);
	}

	/**
     * Aggiorna il numero massimo di iterazioni e ricrea i parametri di convergenza.
     *
     * @param maxIterations Il nuovo limite massimo di iterazioni
     */
	public void setMaxIterations(int maxIterations) {
		// Tolleranza per la convergenza epsilon = 1e-6
		this.cachedParams = new StoppingParameters(R.of(1e-6), maxIterations);
	}

	/**
     * Calcola il numero di iterazioni eseguite dal metodo di Newton-Raphson a partire dal punto iniziale z_0,
     * fino a convergenza verso una radice o fino all'esaurimento del budget N_max.
     * <p>
     * A differenza di una ricerca di radici in senso stretto, qui la non-convergenza entro N_max non e'
     * un errore da segnalare: e' un dato utile (tipicamente indica un punto vicino a un confine tra bacini
     * di attrazione, o a una singolarita' della derivata), quindi il conteggio delle iterazioni eseguite
     * viene restituito comunque, qualunque sia lo stato di terminazione riportato dal solutore.
     * </p>
     *
     * @param z0 Il punto iniziale dell'orbita nel piano complesso
     * @return Il numero di iterazioni eseguite come tipo {@link Natural}
     */
	@Override
	public Natural apply(Complex z0) {
		SolverResult<Complex> result = solver.solve(problem, z0, convergenceTest, cachedParams, complexMetricSpace);
		return N.of(result.getIterationsExecuted());
	}
}
