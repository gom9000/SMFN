package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.polynomial.EuclideanPolynomialRing;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDifferentiationProvider;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.functions.PolynomialFunction;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceDifferentiator;

/**
 * Trova tutte le radici di un polinomio per Newton-Raphson + deflazione:
 * trova una radice, divide per (x - radice) con la divisione euclidea
 * esatta, ripete sul quoziente.
 *
 * La completezza (trovare esattamente n radici per un polinomio di grado n)
 * e' garantita solo se K e' algebricamente chiuso (es. Complex) -- e' il
 * teorema fondamentale dell'algebra, e non vale su un campo come Real. Su
 * un campo non chiuso l'algoritmo si ferma comunque correttamente non
 * appena il quoziente non ha piu' radici in K, restituendo solo quelle
 * trovate fino a quel punto.
 */
public class PolynomialRootSolver<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K> & NumericFactory<K>>
{
	private final S scalarStructure;
	private final EuclideanPolynomialRing<K, S> ring;
	private final PolynomialDifferentiationProvider<K, S> diff;
	private final HornerEvaluator<K, S, PolynomialFunction<K, S>> horner;
	private final MetricSpace<K> space;
	private final NewtonRaphsonSolver<K> solver;
	private final K initialGuess;
	private final ConvergenceParameters params;

	/**
	 * @param scalarStructure il campo K su cui si cercano le radici (es. ComplexField.INSTANCE)
	 * @param fallbackDifferentiationStep passo h per il differenziatore numerico di fallback
	 *        (usato solo se la derivata analitica, gia' fornita qui, non bastasse)
	 * @param space distanza su K, per il criterio di convergenza (es. modulo per Complex)
	 * @param initialGuess punto di partenza per Newton-Raphson, riusato ad ogni deflazione
	 * @param params tolleranza e numero massimo di iterazioni
	 */
	public PolynomialRootSolver(S scalarStructure, K fallbackDifferentiationStep, MetricSpace<K> space, K initialGuess, ConvergenceParameters params) {
		this.scalarStructure = scalarStructure;
		this.ring = new EuclideanPolynomialRing<>(scalarStructure);
		this.diff = new PolynomialDifferentiationProvider<>(scalarStructure);
		this.horner = new HornerEvaluator<>(scalarStructure);
		this.space = space;
		this.solver = new NewtonRaphsonSolver<>(scalarStructure, new CentralDifferenceDifferentiator<>(scalarStructure, fallbackDifferentiationStep));
		this.initialGuess = initialGuess;
		this.params = params;
	}

	public List<K> findAllRoots(Polynomial<K> polynomial) {
		List<K> roots = new ArrayList<>();
		Polynomial<K> current = polynomial;

		while (current.degree() > 0) {
			K root = solver.solve(
				problemFor(current),
				initialGuess,
				(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(),
				params,
				space
			);
			roots.add(root);

			// Deflazione: divide per (x - root), usando la divisione euclidea esatta.
			Polynomial<K> divisor = PolynomialElementFactory.of(scalarStructure, scalarStructure.negate(root), scalarStructure.one());
			current = ring.divide(current, divisor).quotient();
		}

		return roots;
	}

	private DifferentiableScalarProblem<K> problemFor(Polynomial<K> polynomial) {
		PolynomialFunction<K, S> f = new PolynomialFunction<>(polynomial, scalarStructure, horner);
		Polynomial<K> derivativePolynomial = diff.derivative(polynomial);
		PolynomialFunction<K, S> fPrime = new PolynomialFunction<>(derivativePolynomial, scalarStructure, horner);

		return new DifferentiableScalarProblem<K>() {
			@Override public K apply(K x) { return f.apply(x); }
			@Override public Mapping<K, K> getDerivative() { return fPrime; }
		};
	}
}
