package net.gommagomma.smfn.math.algebra.polynomial.core;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.HasScalarStructure;

public abstract class AbstractPolynomialRing<K extends SemiringElement<K>, P extends AbstractPolynomial<K, P>> 
implements HasScalarStructure<K>
{
	protected final Semiring<K> scalarStructure;
	protected AbstractPolynomialRing(Semiring<K> scalarStructure) { this.scalarStructure = scalarStructure; }

	@Override
	public Semiring<K> getScalarStructure() { return this.scalarStructure; }
}