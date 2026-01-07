package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator.CoefficientSequence;

public class HornerEvaluator<K extends AlgebraicElement<K>, S extends Ring<K>, F extends Mapping<K, K> & CoefficientSequence<K>> 
implements EvaluationFunctional<F, K, K>
{
	private final S structure;

    public HornerEvaluator(S structure) {
        this.structure = structure;
    }

    @Override
    public K evaluate(F function, K point) {
        int n = function.degree();
        if (n < 0) return structure.zero();

        K result = function.getCoefficient(n);
        for (int i = n - 1; i >= 0; i--) {
            result = structure.add(structure.multiply(result, point), function.getCoefficient(i));
        }
        return result;
    }

    public interface CoefficientSequence<K> {
        int degree();
        K getCoefficient(int index);
    }
}
