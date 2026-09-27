package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import java.util.List;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.functions.DifferentiableMultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.functions.MultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.numerical.problems.MultivariateFunctionSystemProblem;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Catalogo condiviso di problemi di ricerca degli zeri con soluzione nota, sullo stesso spirito
 * delle catalog class di algebra/linearalgebra: nessun assert qui dentro, solo dati (problemi +
 * punto di partenza + soluzione attesa + budget di iterazioni).
 * <p>
 * A differenza dei cataloghi puramente algebrici, qui l'invariante non e' una legge algebrica
 * ma la convergenza stessa: ogni caso include il numero di iterazioni necessario, perche' un
 * caso a radice doppia (dove Newton converge solo linearmente, non quadraticamente) ha bisogno
 * di un budget molto piu' ampio per raggiungere la stessa tolleranza.
 */
final class RootFindingTestValues
{
	private RootFindingTestValues() {}

	private static final RealField R = RealField.INSTANCE;

	/** Un caso di test scalare: problema, punto di partenza, radice attesa, budget di iterazioni. */
	static final class ScalarCase
	{
		final String name;
		final DifferentiableScalarProblem<Real> problem;
		final Real initialGuess;
		final Real expectedRoot;
		final int maxIterations;

		ScalarCase(String name, DifferentiableScalarProblem<Real> problem, double initialGuess, double expectedRoot, int maxIterations) {
			this.name = name;
			this.problem = problem;
			this.initialGuess = new Real(initialGuess);
			this.expectedRoot = new Real(expectedRoot);
			this.maxIterations = maxIterations;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static DifferentiableScalarProblem<Real> scalarProblem(Mapping<Real, Real> f, Mapping<Real, Real> fPrime) {
		return new DifferentiableScalarProblem<Real>() {
			@Override
			public Real apply(Real x) { return f.apply(x); }

			@Override
			public Mapping<Real, Real> getDerivative() { return fPrime; }
		};
	}

	static List<ScalarCase> scalarCases() {
		return List.of(
			new ScalarCase("x^2 - 2 (radice semplice)",
				scalarProblem(x -> R.subtract(R.multiply(x, x), R.of(2.0)), x -> R.multiply(R.of(2.0), x)),
				1.5, Math.sqrt(2.0), 50),

			new ScalarCase("x^3 - 2 (radice semplice)",
				scalarProblem(x -> R.subtract(R.multiply(R.multiply(x, x), x), R.of(2.0)), x -> R.multiply(R.of(3.0), R.multiply(x, x))),
				1.0, Math.cbrt(2.0), 50),

			new ScalarCase("cos(x) - x (punto fisso trascendente)",
				scalarProblem(x -> R.subtract(R.of(Math.cos(x.getValue())), x), x -> R.subtract(R.of(-Math.sin(x.getValue())), R.one())),
				0.5, 0.7390851332151607, 50),

			new ScalarCase("x^3 - x - 2 (radice semplice)",
				scalarProblem(x -> R.subtract(R.subtract(R.multiply(R.multiply(x, x), x), x), R.of(2.0)),
					x -> R.subtract(R.multiply(R.of(3.0), R.multiply(x, x)), R.one())),
				1.5, 1.5213797068045676, 50),

			new ScalarCase("e^x - 3 (radice semplice, trascendente)",
				scalarProblem(x -> R.subtract(R.of(Math.exp(x.getValue())), R.of(3.0)), x -> R.of(Math.exp(x.getValue()))),
				1.0, Math.log(3.0), 50),

			new ScalarCase("(x-1)^2 (radice doppia: convergenza solo lineare)",
				scalarProblem(x -> {
					Real d = R.subtract(x, R.one());
					return R.multiply(d, d);
				}, x -> R.multiply(R.of(2.0), R.subtract(x, R.one()))),
				0.5, 1.0, 2000),

			new ScalarCase("x^3 - 3x + 2 = (x-1)^2(x+2), ramo della radice doppia x=1",
				scalarProblem(
					x -> R.add(R.subtract(R.multiply(R.multiply(x, x), x), R.multiply(R.of(3.0), x)), R.of(2.0)),
					x -> R.subtract(R.multiply(R.of(3.0), R.multiply(x, x)), R.of(3.0))),
				1.3, 1.0, 2000),

			new ScalarCase("x^3 - 3x + 2 = (x-1)^2(x+2), ramo della radice semplice x=-2",
				scalarProblem(
					x -> R.add(R.subtract(R.multiply(R.multiply(x, x), x), R.multiply(R.of(3.0), x)), R.of(2.0)),
					x -> R.subtract(R.multiply(R.of(3.0), R.multiply(x, x)), R.of(3.0))),
				-2.5, -2.0, 50)
		);
	}

	/** Un caso di test vettoriale (sistema 2x2): problema, punto di partenza, soluzione attesa. */
	static final class VectorCase
	{
		final String name;
		final MultivariateFunctionSystemProblem<Real, RealField> problem;
		final Vector<Real> initialGuess;
		final Vector<Real> expectedSolution;

		VectorCase(String name, MultivariateFunctionSystemProblem<Real, RealField> problem, Vector<Real> initialGuess, Vector<Real> expectedSolution) {
			this.name = name;
			this.problem = problem;
			this.initialGuess = initialGuess;
			this.expectedSolution = expectedSolution;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final VectorSpace<Real, RealField> V2 = new VectorSpace<>(R, 2);

	private static Vector<Real> point(double x, double y) {
		return V2.of(new Real[] { new Real(x), new Real(y) });
	}

	private static DifferentiableMultivariateFunction<Real> function(Mapping<Vector<Real>, Real> f, Mapping<Vector<Real>, Vector<Real>> gradient) {
		return new DifferentiableMultivariateFunction<Real>() {
			@Override
			public Real apply(Vector<Real> v) { return f.apply(v); }

			@Override
			public Mapping<Vector<Real>, Vector<Real>> getGradient() { return gradient; }
		};
	}

	static List<VectorCase> vectorCases() {
		// Sistema 1: cerchio x^2+y^2=4 intersecato con la retta x=y. Soluzioni note: (+-sqrt(2), +-sqrt(2)).
		MultivariateFunction<Real> circle = function(
			v -> R.subtract(R.add(R.multiply(v.get(0), v.get(0)), R.multiply(v.get(1), v.get(1))), R.of(4.0)),
			v -> V2.of(new Real[] { R.multiply(R.of(2.0), v.get(0)), R.multiply(R.of(2.0), v.get(1)) }));
		MultivariateFunction<Real> diagonal = function(
			v -> R.subtract(v.get(0), v.get(1)),
			v -> V2.of(new Real[] { R.one(), R.negate(R.one()) }));
		MultivariateFunctionSystemProblem<Real, RealField> circleAndLine =
			new MultivariateFunctionSystemProblem<>(List.of(circle, diagonal), R, new Real(1e-6));

		// Sistema 2: parabola y=x^2 intersecata con la retta x+y=2. Soluzioni note: (1,1) e (-2,4).
		MultivariateFunction<Real> parabola = function(
			v -> R.subtract(R.multiply(v.get(0), v.get(0)), v.get(1)),
			v -> V2.of(new Real[] { R.multiply(R.of(2.0), v.get(0)), R.negate(R.one()) }));
		MultivariateFunction<Real> line = function(
			v -> R.subtract(R.add(v.get(0), v.get(1)), R.of(2.0)),
			v -> V2.of(new Real[] { R.one(), R.one() }));
		MultivariateFunctionSystemProblem<Real, RealField> parabolaAndLine =
			new MultivariateFunctionSystemProblem<>(List.of(parabola, line), R, new Real(1e-6));

		return List.of(
			new VectorCase("cerchio x^2+y^2=4 ^ retta x=y, ramo positivo", circleAndLine, point(1.0, 2.0), point(Math.sqrt(2.0), Math.sqrt(2.0))),
			new VectorCase("cerchio x^2+y^2=4 ^ retta x=y, ramo negativo", circleAndLine, point(-1.0, -2.0), point(-Math.sqrt(2.0), -Math.sqrt(2.0))),
			new VectorCase("parabola y=x^2 ^ retta x+y=2, ramo (1,1)", parabolaAndLine, point(0.5, 1.5), point(1.0, 1.0)),
			new VectorCase("parabola y=x^2 ^ retta x+y=2, ramo (-2,4)", parabolaAndLine, point(-1.5, 3.5), point(-2.0, 4.0))
		);
	}
}
