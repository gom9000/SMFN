package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMapping;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMorphism;

/**
 * Differenziatore numerico basato sulle differenze in avanti (Forward Difference).
 * Implementa LinearMapping per essere usato dai solutori iterativi.
 */
public class ForwardDifferenceDifferentiator<R extends FieldElement<R>> 
implements LinearMapping<R, LinearMorphism<R, R>, ForwardDifferenceDifferentiator<R>>
{    
    private final R h;

    public ForwardDifferenceDifferentiator(R h) {
        if (h == null || h.isZero()) {
            throw new IllegalArgumentException("Step size 'h' cannot be null or zero.");
        }
        this.h = h.copy();
    }

    /**
     * Applica l'operatore differenziale: f -> f'
     */
    @Override
    public LinearMorphism<R, R> apply(LinearMorphism<R, R> f) {
        return new LinearMorphism<R, R>() {
            @Override
            public R evaluate(R x) {
                // Formula Forward Difference: f'(x) ≈ [f(x + h) - f(x)] / h
                R fXPlusH = f.evaluate(x.add(h));
                R fX = f.evaluate(x);
                
                // Usiamo .scale(h.inverse()) per coerenza con la capability Scalable
                return fXPlusH.subtract(fX).scale(h.inverse());
            }
        };
    }

    @Override
    public LinearMorphism<R, R> evaluate(LinearMorphism<R, R> input) {
        return apply(input);
    }

    // --- Metodi Algebrici necessari per LinearMapping ---

    @Override
    public ForwardDifferenceDifferentiator<R> scale(R scalar) {
        throw new UnsupportedOperationException();
    }
}