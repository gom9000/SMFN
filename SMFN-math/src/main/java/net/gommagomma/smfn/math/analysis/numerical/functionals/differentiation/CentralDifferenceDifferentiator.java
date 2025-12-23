package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;


import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.analysis.core.functionals.Functional;


public class CentralDifferenceDifferentiator<K extends FieldElement<K>> 
implements Functional<K, K, K>
{
	private final K h;


	public CentralDifferenceDifferentiator(K h) {
		if (h.isZero()) {
            throw new IllegalArgumentException("Step size 'h' cannot be zero.");
        }
        this.h = h;
    }


    @Override
    public K evaluate(Mapping<K, K> f, K x0) {
        // f(x0 + h)
        K x0_plus_h = x0.add(h);
        K f_x0_plus_h = f.apply(x0_plus_h);
        
        // f(x0 - h)
        K x0_minus_h = x0.subtract(h);
        K f_x0_minus_h = f.apply(x0_minus_h);
        
        // f(x0 + h) - f(x0 - h)
        K numerator = f_x0_plus_h.subtract(f_x0_minus_h);
        
        // 2h
        K two_h = h.add(h); 
        
        // (f(x0 + h) - f(x0 - h)) / (2h)
        return numerator.divide(two_h);
    }
}
