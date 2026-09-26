package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.VectorRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceJacobianEstimator;
import net.gommagomma.smfn.math.analysis.numerical.solvers.linear.GaussianEliminationSolver;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.VectorNewtonRaphsonSolver;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Intersezione di due cerchi, scritta come Mapping<Vector<Real>,Vector<Real>>
 * puro -- nessun getJacobian(). Il solver deve ricadere sul fallback numerico.
 *
 *   Cerchio 1: x^2 + y^2 = 4          (raggio 2, centro origine)
 *   Cerchio 2: (x-2)^2 + y^2 = 4      (raggio 2, centro (2,0))
 *
 * Per simmetria, le intersezioni sono (1, sqrt(3)) e (1, -sqrt(3)):
 *   1 + 3 = 4 (primo cerchio)
 *   (1-2)^2 + 3 = 1 + 3 = 4 (secondo cerchio)
 */
public class TwoCirclesFallbackDemo {
    public static void main(String[] args) {
        RealField R = RealField.INSTANCE;
        VectorSpace<Real, RealField> V2 = new VectorSpace<>(R, 2);

        VectorRootFindingProblem<Real> problem = v -> {
            Real x = v.get(0);
            Real y = v.get(1);

            Real f1 = R.subtract(R.add(R.multiply(x, x), R.multiply(y, y)), R.of(4));

            Real dx = R.subtract(x, R.of(2));
            Real f2 = R.subtract(R.add(R.multiply(dx, dx), R.multiply(y, y)), R.of(4));

            return VectorElementFactory.of(V2, f1, f2);
        };

        GaussianEliminationSolver<Real, RealField> linearSolver = new GaussianEliminationSolver<>(R, 2);
        CentralDifferenceJacobianEstimator<Real, RealField> jacobianFallback = new CentralDifferenceJacobianEstimator<>(R, new Real(1e-6));
        VectorNewtonRaphsonSolver<Real, RealField> solver = new VectorNewtonRaphsonSolver<>(linearSolver, V2, jacobianFallback);

        MetricSpace<Vector<Real>> space = (a, b) -> {
            Real dx = R.subtract(a.get(0), b.get(0));
            Real dy = R.subtract(a.get(1), b.get(1));
            return R.add(R.multiply(dx, dx), R.multiply(dy, dy)).sqrt();
        };
        ConvergenceParameters params = new ConvergenceParameters(new Real(1e-10), 100);

        double expectedY = Math.sqrt(3);
        System.out.println("Atteso: (1, " + expectedY + ") e (1, " + (-expectedY) + ")");

        Vector<Real> guess1 = VectorElementFactory.of(V2, 1.0, 1.0);
        Vector<Real> result1 = solver.solve(problem, guess1,
            (distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, space).getValue();
        System.out.println("Partendo da (1,1):   " + result1);

        Vector<Real> guess2 = VectorElementFactory.of(V2, 1.0, -1.0);
        Vector<Real> result2 = solver.solve(problem, guess2,
            (distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, space).getValue();
        System.out.println("Partendo da (1,-1):  " + result2);
    }
}
