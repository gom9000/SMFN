package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableVectorProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.VectorNewtonRaphsonSolver;
import net.gommagomma.smfn.math.geometry.Circle;
import net.gommagomma.smfn.math.geometry.Line;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Intersezione cerchio-retta come ricerca di radici vettoriale:
 * F(x,y) = (dist(P,centro) - raggio, distanza con segno di P dalla retta) = (0,0).
 */
public class CircleLineIntersectionDemo
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSpace<Real, RealField> V2 = new VectorSpace<>(R, 2);

	public static void main(String[] args) {
		Circle circle = new Circle(new Point(0.0, 0.0), new Real(3.0));
		Line line = Line.through(new Point(0.0, 4.0), new Point(4.0, 0.0)); // x + y = 4

		System.out.println("Cerchio: " + circle);
		System.out.println("Retta:   " + line);
		System.out.println("\nSoluzioni analitiche attese: x = " + (2 + Math.sqrt(2) / 2) + " oppure x = " + (2 - Math.sqrt(2) / 2) + "  (y = 4 - x)");

		DifferentiableVectorProblem<Real> problem = intersectionProblem(circle, line);

		SquareMatrixAlgebra<Real, RealField> matrixAlgebra = new SquareMatrixAlgebra<>(R, 2);
		VectorNewtonRaphsonSolver<Real, RealField> solver = new VectorNewtonRaphsonSolver<>(matrixAlgebra, V2);

		MetricSpace<Vector<Real>> space = (a, b) -> {
			Real dx = R.subtract(a.get(0), b.get(0));
			Real dy = R.subtract(a.get(1), b.get(1));
			return R.add(R.multiply(dx, dx), R.multiply(dy, dy)).sqrt();
		};
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-10), 100);

		// Due punti di partenza distinti: a differenza della deflazione polinomiale,
		// Newton multidimensionale non ha un modo sistematico per trovare "tutte"
		// le soluzioni -- ognuna richiede un proprio punto di partenza.
		Vector<Real> guess1 = V2.of(new Real[] { new Real(3.0), new Real(1.0) });
		Vector<Real> intersection1 = solver.solve(problem, guess1,
			(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, space);
		System.out.println("\nPartendo da (3,1): intersezione = " + toPoint(intersection1));

		Vector<Real> guess2 = V2.of(new Real[] { new Real(1.0), new Real(3.0) });
		Vector<Real> intersection2 = solver.solve(problem, guess2,
			(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, space);
		System.out.println("Partendo da (1,3): intersezione = " + toPoint(intersection2));

		System.out.println("\nVerifica isOnEntity su entrambe le figure:");
		System.out.println("  Intersezione 1 -> cerchio: " + circle.isOnEntity(toPoint(intersection1)) + ", retta: " + line.isOnEntity(toPoint(intersection1)));
		System.out.println("  Intersezione 2 -> cerchio: " + circle.isOnEntity(toPoint(intersection2)) + ", retta: " + line.isOnEntity(toPoint(intersection2)));
	}

	private static DifferentiableVectorProblem<Real> intersectionProblem(Circle circle, Line line) {
		Point center = circle.getCenter();

		Real ldx = line.getDirectionX();
		Real ldy = line.getDirectionY();
		Real lineLength = R.add(R.multiply(ldx, ldx), R.multiply(ldy, ldy)).sqrt();

		return new DifferentiableVectorProblem<Real>() {
			@Override
			public Vector<Real> apply(Vector<Real> v) {
				Point p = toPoint(v);
				return V2.of(new Real[] { circle.implicitFunctionAt(p), line.implicitFunctionAt(p) });
			}

			@Override
			public Mapping<Vector<Real>, SquareMatrix<Real>> getJacobian() {
				return v -> {
					Point p = toPoint(v);
					Real dist = p.distanceTo(center);

					// df1/dx = (x-cx)/dist, df1/dy = (y-cy)/dist  (gradiente della distanza dal centro)
					Real dxC = R.subtract(p.getX(), center.getX());
					Real dyC = R.subtract(p.getY(), center.getY());
					Real df1dx = R.divide(dxC, dist);
					Real df1dy = R.divide(dyC, dist);

					// df2/dx = -ldy/L, df2/dy = ldx/L  (la retta e' affine: Jacobiana costante)
					Real df2dx = R.divide(R.negate(ldy), lineLength);
					Real df2dy = R.divide(ldx, lineLength);

					return SquareMatrixElementFactory.of(R, df1dx, df1dy, df2dx, df2dy);
				};
			}
		};
	}

	private static Point toPoint(Vector<Real> v) {
		return new Point(v.get(0), v.get(1));
	}
}
