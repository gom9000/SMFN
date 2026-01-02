package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public class PolynomialRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends PolynomialSemiring<K, S>
implements Ring<Polynomial<K>>
{
    public PolynomialRing(S scalarStructure) {
        super(scalarStructure);
    }


    @Override // AdditiveGroup impls
    public Polynomial<K> negate(Polynomial<K> e) {
        if (isZero(e)) return e;

        List<K> negatedCoeffs = new ArrayList<>(e.getCoefficients().size());
        for (K coeff : e.getCoefficients()) {
            negatedCoeffs.add(scalarStructure.negate(coeff));
        }

        return new Polynomial<>(scalarStructure, negatedCoeffs);
    }


    @Override // AlgebraicStructure impls (override)
    public String getName() {
        return "Polynomial Ring over " + scalarStructure.getName();
    }
}