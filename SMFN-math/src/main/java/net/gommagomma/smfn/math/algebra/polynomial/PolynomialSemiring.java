package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.CompositeStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

public class PolynomialSemiring<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> 
implements Semiring<Polynomial<K>>, CompositeStructure<K, Polynomial<K>, S>, ScalarStructure<Polynomial<K>>
{
	protected final S scalarStructure;
	private final Polynomial<K> zero;
    private final Polynomial<K> one;

	public PolynomialSemiring(S scalarStructure) {
        this.scalarStructure = scalarStructure;
        this.zero = new Polynomial<>(scalarStructure, List.of());
        this.one = new Polynomial<>(scalarStructure, List.of(scalarStructure.one()));
    }


	@Override // CompositeStructure impls
    public boolean isExact() {
        return scalarStructure.isExact();
    }

	@Override 
    public S getScalarStructure() { return scalarStructure;	}


    @Override // ScalarStructure impls
    public Real magnitude(Polynomial<K> element) {
        if (element == null) return RealField.INSTANCE.zero();
        
        double sumOfSquares = 0.0;
        // Iteriamo sui coefficienti del polinomio
        for (K coeff : element.getCoefficients()) {
            // Usiamo la magnitudo della struttura dello scalare K
            Real m = scalarStructure.magnitude(coeff);
            double val = m.getValue();
            sumOfSquares += val * val;
        }
        
        return RealField.INSTANCE.of(Math.sqrt(sumOfSquares));
    }
 

	@Override // AdditiveMonoid impls
    public Polynomial<K> zero() {
        return zero;
    }

    @Override
    public Polynomial<K> add(Polynomial<K> a, Polynomial<K> b) {
        int maxDegree = Math.max(a.degree(), b.degree());
        List<K> resultCoeffs = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= maxDegree; i++) {
            K sum = scalarStructure.add(a.getCoefficient(i), b.getCoefficient(i));
            resultCoeffs.add(sum);
        }

        return new Polynomial<>(scalarStructure, resultCoeffs);
    }


    @Override // MultiplicativeMonoid impls
    public Polynomial<K> one() {
        return one;
    }

    @Override
    public Polynomial<K> multiply(Polynomial<K> a, Polynomial<K> b) {
        if (isZero(a) || isZero(b)) return zero();

        int newDegree = a.degree() + b.degree();
        List<K> resultCoeffs = new ArrayList<>(newDegree + 1);
        
        // Inizializza con lo zero del semianello
        for (int i = 0; i <= newDegree; i++) {
            resultCoeffs.add(scalarStructure.zero());
        }

        // algoritmo di convoluzione standard: c_k = sum_{i+j=k} (a_i * b_j)
        for (int i = 0; i <= a.degree(); i++) {
            K ai = a.getCoefficient(i);
            if (scalarStructure.isZero(ai)) continue;

            for (int j = 0; j <= b.degree(); j++) {
                K bj = b.getCoefficient(j);
                if (scalarStructure.isZero(bj)) continue;

                int k = i + j;
                K currentTerm = scalarStructure.multiply(ai, bj);
                K accumulated = scalarStructure.add(resultCoeffs.get(k), currentTerm);
                resultCoeffs.set(k, accumulated);
            }
        }

        return new Polynomial<>(scalarStructure, resultCoeffs);
    }


    // AlgebraicStructure impls
    @Override
    public String getName() {
        return "PolynomialSemiring over " + scalarStructure.getName();
    }

    @Override
    public boolean contains(Polynomial<K> e) {
        return e != null && e.getScalarStructure().equals(this.getScalarStructure());
    }

    @Override
    public boolean areEqual(Polynomial<K> a, Polynomial<K> b) {
    	if (a == b) return true;
        if (a.degree() != b.degree()) return false;
        for (int i = 0; i <= a.degree(); i++) {
            if (!scalarStructure.areEqual(a.getCoefficient(i), b.getCoefficient(i))) {
                return false;
            }
        }
        return true;
    }
}
