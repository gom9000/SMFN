package net.gommagomma.smfn.math.algebra.polynomials;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.ScalarStructure;

public class CommutativePolynomialRing<K extends ScalarElement<K>, S extends CommutativeRing<K> & ScalarStructure<K>> 
extends PolynomialRing<K, S> 
implements CommutativeRing<Polynomial<K>>
{
	public CommutativePolynomialRing(S scalarStructure) {
        super(scalarStructure);
    }


    @Override // AlgebraicStructure impls (override)
    public String getName() {
        return "Commutative Polynomial Ring over " + scalarStructure.getName();
    }
}
