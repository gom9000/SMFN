package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.analysis.core.functionals.Functional;

public class ForwardDifferenceDifferentiator<R extends FieldElement<R>>
implements Functional<R, R, R>
{    
    private final R h;


    public ForwardDifferenceDifferentiator(R h) {
        this.h = h;
    }

    
    @Override // Calcola f'(x) ≈ [f(x + h) - f(x)] / h
    public R evaluate(Mapping<R, R> function, R x) {
        R xPlusH = x.add(h);
        
        R fXPlusH = function.apply(xPlusH);
        R fX = function.apply(x);
        
        R numerator = fXPlusH.subtract(fX);
        
        return numerator.divide(h);
    }
}
