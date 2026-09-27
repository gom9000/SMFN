package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Catalogo condiviso di problemi ai valori iniziali (IVP) con soluzione analitica nota, sullo
 * stesso spirito delle catalog class del resto del progetto: nessun assert qui dentro, solo dati.
 * <p>
 * Come per RootFindingTestValues, l'invariante qui non e' una legge algebrica ma la convergenza
 * numerica alla soluzione analitica esatta, nota per ciascun caso e verificata indipendentemente
 * dal codice (le equazioni sono scelte proprio perche' hanno una soluzione elementare chiusa).
 */
final class OdeTestValues
{
	private OdeTestValues() {}

	private static final RealField R = RealField.INSTANCE;

	static final class IvpCase
	{
		final String name;
		final InitialValueProblem<Real, Vector<Real>> problem;
		final InnerProductVectorSpace<Real, RealField> space;
		final double endTime;
		final DoubleUnaryOperator[] analyticSolution; // una componente per ciascuna dimensione dello stato

		IvpCase(String name, InitialValueProblem<Real, Vector<Real>> problem, InnerProductVectorSpace<Real, RealField> space,
				double endTime, DoubleUnaryOperator... analyticSolution) {
			this.name = name;
			this.problem = problem;
			this.space = space;
			this.endTime = endTime;
			this.analyticSolution = analyticSolution;
		}

		@Override
		public String toString() { return name; }
	}

	private static InitialValueProblem<Real, Vector<Real>> ivp(InnerProductVectorSpace<Real, RealField> space,
			java.util.function.BiFunction<Vector<Real>, Real, Vector<Real>> derivative, Real[] initialState, double startTime) {
		return new InitialValueProblem<Real, Vector<Real>>() {
			@Override public Vector<Real> derivative(Vector<Real> state, Real time) { return derivative.apply(state, time); }
			@Override public Vector<Real> getInitialState() { return space.of(initialState); }
			@Override public Real getStartTime() { return new Real(startTime); }
		};
	}

	static List<IvpCase> cases() {
		InnerProductVectorSpace<Real, RealField> v1 = new InnerProductVectorSpace<>(R, 1);
		InnerProductVectorSpace<Real, RealField> v2 = new InnerProductVectorSpace<>(R, 2);

		return List.of(
			new IvpCase("y' = y, y(0) = 1 -> y(t) = e^t (crescita esponenziale)",
				ivp(v1, (state, t) -> state, new Real[] { new Real(1.0) }, 0.0),
				v1, 1.0,
				t -> Math.exp(t)),

			new IvpCase("y' = -y, y(0) = 1 -> y(t) = e^-t (decadimento esponenziale)",
				ivp(v1, (state, t) -> v1.scale(new Real(-1.0), state), new Real[] { new Real(1.0) }, 0.0),
				v1, 2.0,
				t -> Math.exp(-t)),

			new IvpCase("y' = 3y, y(0) = 2 -> y(t) = 2*e^(3t)",
				ivp(v1, (state, t) -> v1.scale(new Real(3.0), state), new Real[] { new Real(2.0) }, 0.0),
				v1, 0.5,
				t -> 2.0 * Math.exp(3.0 * t)),

			new IvpCase("y' = -(y-1), y(0) = 0 -> y(t) = 1 - e^-t (rilassamento verso un punto fisso)",
				ivp(v1, (state, t) -> v1.scale(new Real(-1.0), v1.subtract(state, v1.of(new Real[] { R.one() }))),
					new Real[] { new Real(0.0) }, 0.0),
				v1, 3.0,
				t -> 1.0 - Math.exp(-t)),

			new IvpCase("Oscillatore armonico: y1'=y2, y2'=-y1, y(0)=(1,0) -> (cos t, -sin t)",
				ivp(v2, (state, t) -> v2.of(new Real[] { state.get(1), R.negate(state.get(0)) }),
					new Real[] { new Real(1.0), new Real(0.0) }, 0.0),
				v2, Math.PI / 2.0,
				t -> Math.cos(t), t -> -Math.sin(t))
		);
	}
}
