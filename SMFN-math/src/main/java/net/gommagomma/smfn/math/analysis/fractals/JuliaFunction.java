// net.gommagomma.smfn.math.analysis.fractals.JuliaFunction.java
package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.core.algebra.MathFunction;
import net.gommagomma.smfn.math.core.algebra.numeric.Complex;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;

/**
 * Rappresenta la funzione matematica per un set di Julia specifico (definito da una costante C).
 */
public class JuliaFunction
implements MathFunction<Complex, Real>
{
    private final JuliaSolver solver;
    private final int maxIterations;


    public JuliaFunction(Complex constantC, int maxIterations) {
        this.maxIterations = maxIterations;
        solver = new JuliaSolver(constantC);
    }


    @Override
    public Real evaluate(Complex input) {
        int iterations = solver.evaluateJuliaPoint(input, maxIterations);

        return new Real(iterations);
    }
}
