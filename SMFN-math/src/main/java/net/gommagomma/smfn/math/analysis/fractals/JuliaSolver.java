package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.IterativeSolver;


public class JuliaSolver
implements IterativeSolver<FixedPointProblem<Complex>, Complex, Natural>
{
    private static final double DIVERGENCE_RADIUS_SQ = 4.0;
    private ComplexField C = ComplexField.INSTANCE;
    private RealField R = RealField.INSTANCE;
    private NaturalSemiring N = NaturalSemiring.INSTANCE;
    private final Complex constantC;


    public JuliaSolver(Complex constantC) {
        this.constantC = constantC;
    }


    // Il metodo solve() è esattamente lo stesso del MandelbrotSolver
    @Override
    public Natural solve(FixedPointProblem<Complex> problem, Complex initialGuess, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<Complex> space)
    {
    	Complex currentZ = initialGuess;

        for (int iterations = 0; iterations < params.maxIterations; iterations++)
        {
        	Real divergenceMeasure = R.of(currentZ.modulusSquared());

        	// Il Solver chiama il Criterio con la misura (Real)
            if (criteria.isConverged(divergenceMeasure, params, iterations)) {
                return N.of(iterations);
            }
            
            // Prepara per la prossima iterazione
            currentZ = problem.nextIteration(currentZ);
        }
        
        return N.of(params.maxIterations);
    }


    public Natural evaluateJuliaPoint(Complex z0, int maxIterations)
    {
        // z_{n+1} = z_n^2 + c
        FixedPointProblem<Complex> problem = current -> C.add(C.multiply(current, current), constantC);
        
        // Il test di convergenza/divergenza (|z_n|^2 > 4)
        ConvergenceCriteria divergenceTest = (divergenceMeasure, params, iteration) -> {
        	return divergenceMeasure.getValue() > DIVERGENCE_RADIUS_SQ;
        };

        ConvergenceParameters params = new ConvergenceParameters(R.zero(), maxIterations);
        return solve(problem, z0, divergenceTest, params, null);
    }
}
