package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Catalogo condiviso di funzioni scalari con derivata analitica nota, per gli invarianti di
 * CentralDifferenceDifferentiator, sullo stesso spirito delle altre catalog class del pacchetto
 * analysis: nessun assert qui dentro, solo dati.
 */
final class DifferentiationTestValues
{
	private DifferentiationTestValues() {}

	private static final RealField R = RealField.INSTANCE;

	static final class FunctionCase
	{
		final String name;
		final Mapping<Real, Real> f;
		final DoubleUnaryOperator analyticDerivative;
		final double[] evaluationPoints;

		FunctionCase(String name, Mapping<Real, Real> f, DoubleUnaryOperator analyticDerivative, double... evaluationPoints) {
			this.name = name;
			this.f = f;
			this.analyticDerivative = analyticDerivative;
			this.evaluationPoints = evaluationPoints;
		}

		@Override
		public String toString() { return name; }
	}

	static List<FunctionCase> cases() {
		return List.of(
			new FunctionCase("f(x) = x^2, f'(x) = 2x",
				x -> R.multiply(x, x), x -> 2.0 * x, -3.0, 0.0, 1.0, 5.0),

			new FunctionCase("f(x) = x^3 - 2x, f'(x) = 3x^2 - 2",
				x -> R.subtract(R.multiply(R.multiply(x, x), x), R.multiply(new Real(2.0), x)),
				x -> 3.0 * x * x - 2.0, -2.0, 0.5, 2.0),

			new FunctionCase("f(x) = sin(x), f'(x) = cos(x)",
				x -> new Real(Math.sin(x.getValue())), Math::cos, 0.0, Math.PI / 4.0, Math.PI, 3.0),

			new FunctionCase("f(x) = e^x, f'(x) = e^x",
				x -> new Real(Math.exp(x.getValue())), Math::exp, -1.0, 0.0, 1.0, 2.0),

			new FunctionCase("f(x) = costante 7, f'(x) = 0",
				x -> new Real(7.0), x -> 0.0, -100.0, 0.0, 100.0),

			new FunctionCase("f(x) = 1/x (x != 0), f'(x) = -1/x^2",
				x -> R.divide(R.one(), x), x -> -1.0 / (x * x), 1.0, 2.0, -3.0)
		);
	}
}
