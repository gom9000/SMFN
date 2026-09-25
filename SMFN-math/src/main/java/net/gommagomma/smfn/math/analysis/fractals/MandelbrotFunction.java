package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;

/**
 * Rappresenta la funzione matematica dell'insieme di Mandelbrot.
 * Funzione complessa che assegna a ciascun punto del piano complesso c il corrispondente tempo di fuga (numero di iterazioni)
 * associato alla mappa quadratica di Mandelbrot z_{k+1} = z_k^2 + c a partire dall'origine z_0 = 0.
 * 
 * Incapsula al suo interno il solutore iterativo MandelbrotSolver configurando la condizione di fuga standard: |z_k|^2 > 4.0

 * Il valore restituito e' un Natural rappresentante il conteggio di iterazioni necessario a verificare
 * la divergenza, oppure N_max se il punto appartiene all'insieme di Mandelbrot.
 */
public class MandelbrotFunction
implements Mapping<Complex, Natural>
{
	private static final double DIVERGENCE_RADIUS_SQ = 4.0;
	private final ComplexField C = ComplexField.INSTANCE;
    private final RealField R = RealField.INSTANCE;
    private final MandelbrotSolver solver = new MandelbrotSolver();

    private ConvergenceParameters cachedParams;
    private final ConvergenceCriteria divergenceTest;

    /**
     * Costruisce Mandelbrot impostando il numero massimo di iterazioni per il test di fuga.
     *
     * @param maxIterations Il numero massimo di iterazioni N_max da calcolare per ciascun punto del piano
     */
    public MandelbrotFunction(int maxIterations) {
        this.divergenceTest = (measure, params, iteration) -> measure.getValue() > DIVERGENCE_RADIUS_SQ;
        setMaxIterations(maxIterations);
    }

    /**
     * Aggiorna il numero massimo di iterazioni N_max e ricrea l'oggetto di configurazione della convergenza.
     *
     * @param maxIterations Il nuovo limite massimo di iterazioni consentite
     */
    public void setMaxIterations(int maxIterations) {
    	this.cachedParams = new ConvergenceParameters(R.one(), maxIterations);
    }

    /**
     * Calcola il tempo di fuga per un dato numero complesso c nel piano.
     *
     * @param c Il punto del piano complesso c = x + i*y da valutare
     * @return Il conteggio di iterazioni fino alla fuga o N_max come istanza di Natural
     */
    @Override
    public Natural apply(Complex c) {
    	FixedPointProblem<Complex> problem = current -> C.add(C.multiply(current, current), c);
        return solver.solve(problem, C.zero(), divergenceTest, cachedParams, null);
    }
}
