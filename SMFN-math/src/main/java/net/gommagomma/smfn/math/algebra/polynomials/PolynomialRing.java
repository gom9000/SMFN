package net.gommagomma.smfn.math.algebra.polynomials;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;

public class PolynomialRing<K extends ScalarElement<K>>
extends PolynomialSemiring<K>
implements Ring<Polynomial<K>>
{
	protected final Ring<K> kRing;

    public PolynomialRing(Ring<K> kRing) {
        super(kRing);
        this.kRing = kRing;
    }


    @Override // AdditiveGroup impls
    public Polynomial<K> negate(Polynomial<K> e) {
        if (isZero(e)) return e;

        List<K> negatedCoeffs = new ArrayList<>(e.getCoefficients().size());
        for (K coeff : e.getCoefficients()) {
            negatedCoeffs.add(kRing.negate(coeff));
        }

        return new Polynomial<>(kRing, negatedCoeffs);
    }


    @Override // AlgebraicStructure impls (override)
    public String getName() {
        return "Polynomial Ring over " + kRing.getName();
    }
}