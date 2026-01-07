package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.SymbolicDifferentiationProvider;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public class PolynomialRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends PolynomialSemiring<K, S>
implements Ring<Polynomial<K>>, SymbolicDifferentiationProvider<Polynomial<K>>
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

        return new Polynomial<>(this, scalarStructure, negatedCoeffs);
    }


    @Override // AlgebraicStructure impls (override)
    public String getName() {
        return "Polynomial Ring over " + scalarStructure.getName();
    }


	@Override
	public Polynomial<K> derivative(Polynomial<K> p) {
		ScalarStructure<K> s = p.getScalarStructure();
        if (p.degree() <= 0) {
            return new PolynomialSemiring<>(s).zero();
        }

        if (!(s instanceof NumericFactory)) {
            throw new UnsupportedOperationException("Cannot derive: scalar structure is not a NumericFactory");
        }
        NumericFactory<K> factory = (NumericFactory<K>) s;

        List<K> derivCoeffs = new ArrayList<>();
        for (int i = 1; i <= p.degree(); i++) {
        	K nAsScalar = factory.of(i); 
            derivCoeffs.add(s.multiply(nAsScalar, p.getCoefficient(i)));
        }

        return new Polynomial<>(this, s, derivCoeffs);
	}
}