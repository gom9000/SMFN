package net.gommagomma.smfn.math.algebra.polynomials;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;

public class CommutativePolynomialRing<K extends ScalarElement<K>> 
extends PolynomialRing<K> 
implements CommutativeRing<Polynomial<K>>
{
	public CommutativePolynomialRing(CommutativeRing<K> kCommutativeRing) {
        super(kCommutativeRing);
    }
	

    @Override // AlgebraicStructure impls (override)
    public String getName() {
        return "Commutative Polynomial Ring over " + kRing.getName();
    }
}
