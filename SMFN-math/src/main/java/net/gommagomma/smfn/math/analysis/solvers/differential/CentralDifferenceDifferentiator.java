package net.gommagomma.smfn.math.analysis.solvers.differential;


import net.gommagomma.smfn.math.algebra.core.MathFunction;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


public class CentralDifferenceDifferentiator<K extends FieldElement<K, ?>> 
implements NumericalDifferentiator<K>
{
    @Override
    public K derivativeAt(MathFunction<K, K> function, K x, K h) {
        if (h.isZero()) {
            throw new IllegalArgumentException("Step size 'h' cannot be zero.");
        }
        
        // f(x + h)
        K x_plus_h = x.add(h);
        K f_x_plus_h = function.evaluate(x_plus_h);
        
        // f(x - h)
        K x_minus_h = x.subtract(h);
        K f_x_minus_h = function.evaluate(x_minus_h);
        
        // f(x + h) - f(x - h)
        K numerator = f_x_plus_h.subtract(f_x_minus_h);
        
        // 2h
        K two_h = h.add(h); 
        
        // (f(x + h) - f(x - h)) / (2h)
        return numerator.divide(two_h);
    }
}
