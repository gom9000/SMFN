package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


public interface Field<E extends FieldElement<E, N>, N extends ComparableElement<N>>
extends EuclideanDomain<E, N>
{
	@Override
    default E quotient(E a, E b) {
        if (b.isZero()) throw new ArithmeticException("Division by zero");
        return a.multiply(b.inverse());
    }

    @Override
    default E remainder(E a, E b) {
        if (b.isZero()) throw new ArithmeticException("Division by zero");
        return zero();
    }
}
