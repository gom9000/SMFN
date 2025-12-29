package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

/**
 * Rappresenta la funzione matematica dell'insieme di Mandelbrot.
 * Accetta un punto complesso 'c' e restituisce il numero di iterazioni prima della divergenza come SignedInt.
 */
public class MandelbrotFunction
implements Mapping<Complex, Natural>
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
    public Natural apply(Complex input) {
        return solver.evaluateMandelbrotPoint(input, maxIterations);
    }
}
