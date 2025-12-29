package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Absolutable;

public interface EuclideanDomain<E extends AlgebraicElement<E>, N extends AlgebraicElement<N>>
extends CommutativeRing<E>
{
	E quotient(E a, E b);
	E remainder(E a, E b);
	N degree(E e);

	default E gcd(E a, E b) {
        E x = a;
        E y = b;
        while (!isZero(y)) {
            E temp = y;
            y = remainder(x, y);
            x = temp;
        }
        return normalize(x);
    }

	default E lcm(E a, E b) {
		if (isZero(a) || isZero(b)) return zero();
		E gcd = gcd(a, b);
		return normalize(multiply(quotient(a, gcd), b));
	}

	@SuppressWarnings("unchecked")
	default E normalize(E element) {
	    if (element instanceof Absolutable) {
	        return (E) ((Absolutable<?>) element).abs();
	    }
	    return element;
	}
}
