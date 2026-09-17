package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Capacit di una struttura di invertire alcuni dei propri elementi -- a
 * differenza di MultiplicativeGroup, che garantisce un inverso per ogni
 * elemento non nullo, qui l'invertibilit va verificata elemento per
 * elemento (l'esempio tipico sono le matrici quadrate: non ogni matrice
 * non nulla  invertibile, anche se lo scalare sottostante  un campo).
 */
public interface InvertibleElements<E extends AlgebraicElement<E>>
{
	/**
	 * Calcola l'inverso di e, se esiste.
	 * @throws ArithmeticException se e non  invertibile.
	 */
	E inverse(E e);

	boolean isInvertible(E e);
}
