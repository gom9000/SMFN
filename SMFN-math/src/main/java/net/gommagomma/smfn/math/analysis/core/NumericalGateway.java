package net.gommagomma.smfn.math.analysis.core;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.analysis.core.problems.AnalysisProblem;

public class NumericalGateway {
    // Calcola la derivata numerica in un punto usando il rapporto incrementale
    public <K extends ScalarElement<K>> K derivativeAt(Morphism<K, K> f, K point, K epsilon) {
        // (f(point + epsilon) - f(point)) / epsilon
    }
    
    // Risolve un problema usando un solutore specifico
    public <P extends AnalysisProblem<T>, T, R> R solve(P problem, Solver<P, R> solver) {
        return solver.solve(problem);
    }
}
