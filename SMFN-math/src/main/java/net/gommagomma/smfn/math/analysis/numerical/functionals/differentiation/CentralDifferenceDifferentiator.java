package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMapping;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMorphism;

/**
 * Differenziatore numerico basato sulle differenze centrali.
 * K deve essere un FieldElement che è intrinsecamente Scalable e CommutativeMonoid.
 */
public class CentralDifferenceDifferentiator<K extends FieldElement<K>> 
implements LinearMapping<K, LinearMorphism<K, K>, CentralDifferenceDifferentiator<K>>
{
    private final K h;

    public CentralDifferenceDifferentiator(K h) {
        if (h == null || h.isZero()) {
            throw new IllegalArgumentException("Step size 'h' cannot be null or zero.");
        }
        this.h = h.copy();
    }

    @Override
    public LinearMorphism<K, K> apply(LinearMorphism<K, K> f) {
        // Implementazione tramite classe anonima o lambda
        return x -> {
            K fPlus = f.evaluate(x.add(h));
            K fMinus = f.evaluate(x.subtract(h));
            
            K twoH = h.add(h);
            
            // Formula: (f(x+h) - f(x-h)) * (1/2h)
            return fPlus.subtract(fMinus).scale(twoH.inverse());
        };
    }

    @Override
    public LinearMorphism<K, K> evaluate(LinearMorphism<K, K> input) {
        return apply(input);
    }

    @Override
    public CentralDifferenceDifferentiator<K> scale(K scalar) {
        throw new UnsupportedOperationException("Scaling operator not yet implemented.");
    }
}