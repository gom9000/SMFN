package net.gommagomma.smfn.math.analysis.core.problems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.functions.DifferentiableMultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.functions.MultivariateFunction;
import net.gommagomma.smfn.math.analysis.numerical.problems.MultivariateFunctionSystemProblem;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

@DisplayName("MultivariateFunctionSystemProblem: assembla F e la Jacobiana da funzioni multivariate qualsiasi")
class MultivariateFunctionSystemProblemTest
{
	private static final RealField R = RealField.INSTANCE;
	private final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

	private Vector<Real> point(double x, double y) {
		return V2.of(new Real[] { new Real(x), new Real(y) });
	}

	// g(x,y) = x + 2y, gradiente esatto costante (1,2): implementa la capacita' opzionale.
	private final DifferentiableMultivariateFunction<Real> exactLinear = new DifferentiableMultivariateFunction<Real>() {
		@Override public Real apply(Vector<Real> v) { return R.add(v.get(0), R.multiply(new Real(2.0), v.get(1))); }
		@Override public Mapping<Vector<Real>, Vector<Real>> getGradient() {
			Vector<Real> g = V2.of(new Real[] { R.one(), new Real(2.0) });
			return v -> g;
		}
	};

	// f(x,y) = x^2 + y^2, gradiente vero (2x,2y) -- NON implementa la capacita' opzionale:
	// deve ricadere sulla stima numerica.
	private final MultivariateFunction<Real> plainQuadratic = v ->
		R.add(R.multiply(v.get(0), v.get(0)), R.multiply(v.get(1), v.get(1)));

	@Test
	@DisplayName("apply(): assembla F(v) = (f1(v), f2(v)) correttamente")
	void applyAssemblesFunctionValues() {
		MultivariateFunctionSystemProblem<Real, RealField> problem =
			new MultivariateFunctionSystemProblem<>(List.of(exactLinear, plainQuadratic), R, new Real(1e-6));

		Vector<Real> result = problem.apply(point(3.0, 4.0));

		assertEquals(11.0, result.get(0).getValue(), 1e-9); // 3 + 2*4 = 11
		assertEquals(25.0, result.get(1).getValue(), 1e-9); // 3^2 + 4^2 = 25
	}

	@Nested
	@DisplayName("getJacobian()")
	class JacobianTests
	{
		@Test
		@DisplayName("Riga esatta: una DifferentiableMultivariateFunction usa il proprio gradiente, non la stima numerica")
		void usesExactGradientWhenAvailable() {
			// Due copie della stessa funzione lineare: la Jacobiana attesa e' costante,
			// identica in ogni punto, esattamente (1,2; 1,2) -- nessun errore di stima possibile.
			MultivariateFunctionSystemProblem<Real, RealField> problem =
				new MultivariateFunctionSystemProblem<>(List.of(exactLinear, exactLinear), R, new Real(1e-6));

			SquareMatrix<Real> jacobian = problem.getJacobian().apply(point(100.0, -50.0)); // punto qualsiasi, il gradiente e' costante

			assertEquals(1.0, jacobian.get(0, 0).getValue(), 0.0); // esatto, zero tolleranza
			assertEquals(2.0, jacobian.get(0, 1).getValue(), 0.0);
			assertEquals(1.0, jacobian.get(1, 0).getValue(), 0.0);
			assertEquals(2.0, jacobian.get(1, 1).getValue(), 0.0);
		}

		@Test
		@DisplayName("Riga numerica: una MultivariateFunction semplice ricade sulla stima per differenze centrali")
		void fallsBackToNumericEstimateWhenGradientNotProvided() {
			MultivariateFunctionSystemProblem<Real, RealField> problem =
				new MultivariateFunctionSystemProblem<>(List.of(exactLinear, plainQuadratic), R, new Real(1e-6));

			SquareMatrix<Real> jacobian = problem.getJacobian().apply(point(3.0, 4.0));

			// Riga 0 (exactLinear): esatta, (1,2)
			assertEquals(1.0, jacobian.get(0, 0).getValue(), 1e-9);
			assertEquals(2.0, jacobian.get(0, 1).getValue(), 1e-9);

			// Riga 1 (plainQuadratic, fallback numerico): vero gradiente (2x,2y) = (6,8) in (3,4),
			// approssimato per differenze centrali -- tolleranza piu' larga, coerente con l'errore
			// atteso di un metodo numerico, non con un confronto esatto.
			assertEquals(6.0, jacobian.get(1, 0).getValue(), 1e-4);
			assertEquals(8.0, jacobian.get(1, 1).getValue(), 1e-4);
		}

		@Test
		@DisplayName("Sistema interamente a fallback numerico: entrambe le righe stimate, entrambe vicine al vero gradiente")
		void bothRowsFallBackWhenNeitherFunctionIsDifferentiable() {
			// Seconda funzione plain, diversa dalla prima: h(x,y) = x*y, gradiente vero (y,x)
			MultivariateFunction<Real> plainProduct = v -> R.multiply(v.get(0), v.get(1));

			MultivariateFunctionSystemProblem<Real, RealField> problem =
				new MultivariateFunctionSystemProblem<>(List.of(plainQuadratic, plainProduct), R, new Real(1e-6));

			SquareMatrix<Real> jacobian = problem.getJacobian().apply(point(3.0, 4.0));

			assertEquals(6.0, jacobian.get(0, 0).getValue(), 1e-4); // d(x^2+y^2)/dx = 2x = 6
			assertEquals(8.0, jacobian.get(0, 1).getValue(), 1e-4); // d(x^2+y^2)/dy = 2y = 8
			assertEquals(4.0, jacobian.get(1, 0).getValue(), 1e-4); // d(xy)/dx = y = 4
			assertEquals(3.0, jacobian.get(1, 1).getValue(), 1e-4); // d(xy)/dy = x = 3
		}
	}

	@Test
	@DisplayName("Costruttore rifiuta una lista vuota o nulla")
	void constructorRejectsEmptyOrNullList() {
		assertThrows(IllegalArgumentException.class, () -> new MultivariateFunctionSystemProblem<>(List.of(), R, new Real(1e-6)));
		assertThrows(IllegalArgumentException.class, () -> new MultivariateFunctionSystemProblem<Real, RealField>(null, R, new Real(1e-6)));
	}

	@Test
	@DisplayName("apply() e getJacobian() rifiutano un vettore di dimensione sbagliata")
	void rejectsMismatchedVectorDimension() {
		MultivariateFunctionSystemProblem<Real, RealField> problem =
			new MultivariateFunctionSystemProblem<>(List.of(exactLinear, plainQuadratic), R, new Real(1e-6));

		VectorSemimodule<Real, RealField> V3 = new VectorSemimodule<>(R, 3);
		Vector<Real> wrongSize = V3.of(new Real[] { new Real(1.0), new Real(2.0), new Real(3.0) }); // 3, non 2

		assertThrows(IllegalArgumentException.class, () -> problem.apply(wrongSize));
		assertThrows(IllegalArgumentException.class, () -> problem.getJacobian().apply(wrongSize));
	}
}
