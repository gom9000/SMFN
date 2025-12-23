package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;

public interface Absolutable<E extends Absolutable<E>> 
extends Orderable<E>, AbelianGroupElement<E>
{
	@SuppressWarnings("unchecked")
	default E abs() {
		return this.isLessThan(getZero()) ? this.negate() : (E) this;
	}

	default int signum() {
		if (this.isZero()) return 0;
		return this.isGreaterThan(getZero()) ? 1 : -1;
	}
}