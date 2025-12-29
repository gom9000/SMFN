// net.gommagomma.smfn.math.analysis.fractals.JuliaFunction.java
package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

/**
 * Rappresenta la funzione matematica per un set di Julia specifico (definito da una costante C).
 */
public class JuliaFunction
implements Mapping<Complex, Natural>
{
    private final JuliaSolver solver;
    private final int maxIterations;


    public JuliaFunction(Complex constantC, int maxIterations) {
        this.maxIterations = maxIterations;
        solver = new JuliaSolver(constantC);
    }


    @Override
    public Natural apply(Complex input) {
        return solver.evaluateJuliaPoint(input, maxIterations);
    }
}
