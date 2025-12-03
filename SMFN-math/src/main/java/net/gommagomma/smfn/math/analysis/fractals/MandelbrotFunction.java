package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
 * Rappresenta la funzione matematica dell'insieme di Mandelbrot.
 * Accetta un punto complesso 'c' e restituisce il numero di iterazioni prima della divergenza come SignedInt.
 */
public class MandelbrotFunction
implements MathFunction<Complex, Real>
{
    private final MandelbrotSolver solver = new MandelbrotSolver();
    private int maxIterations;


    public MandelbrotFunction(int maxIterations) {
        this.maxIterations = maxIterations;
    }


    public void setMaxIterations(int maxIterations) {
    	this.maxIterations = maxIterations;
    }


    @Override
    public Real evaluate(Complex input) {
        int iterations = solver.evaluateMandelbrotPoint(input, maxIterations);

        return new Real(iterations);
    }
}
