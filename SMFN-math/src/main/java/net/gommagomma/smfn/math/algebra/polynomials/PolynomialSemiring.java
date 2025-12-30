package net.gommagomma.smfn.math.algebra.polynomials;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.CompositeStructure;
import net.gommagomma.smfn.math.algebra.core.structures.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

public class PolynomialSemiring<K extends ScalarElement<K>> 
implements Semiring<Polynomial<K>>, CompositeStructure<K, Polynomial<K>>
{
	protected final Semiring<K> kSemiring;
	private final Polynomial<K> zero;
    private final Polynomial<K> one;

	public PolynomialSemiring(Semiring<K> kSemiring) {
        this.kSemiring = kSemiring;
        this.zero = new Polynomial<>(kSemiring, List.of());
        this.one = new Polynomial<>(kSemiring, List.of(kSemiring.one()));
    }

	@SuppressWarnings("unchecked")
	@Override // CompositeStructure impls
    public ScalarStructure<K> getScalarStructure() { return (ScalarStructure<K>) kSemiring;	}


	@Override // AdditiveMonoid impls
    public Polynomial<K> zero() {
        return zero;
    }

    @Override
    public Polynomial<K> add(Polynomial<K> a, Polynomial<K> b) {
        int maxDegree = Math.max(a.degree(), b.degree());
        List<K> resultCoeffs = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= maxDegree; i++) {
            K sum = kSemiring.add(a.getCoefficient(i), b.getCoefficient(i));
            resultCoeffs.add(sum);
        }

        return new Polynomial<>(kSemiring, resultCoeffs);
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
            resultCoeffs.add(kSemiring.zero());
        }

        // algoritmo di convoluzione standard: c_k = sum_{i+j=k} (a_i * b_j)
        for (int i = 0; i <= a.degree(); i++) {
            K ai = a.getCoefficient(i);
            if (kSemiring.isZero(ai)) continue;

            for (int j = 0; j <= b.degree(); j++) {
                K bj = b.getCoefficient(j);
                if (kSemiring.isZero(bj)) continue;

                int k = i + j;
                K currentTerm = kSemiring.multiply(ai, bj);
                K accumulated = kSemiring.add(resultCoeffs.get(k), currentTerm);
                resultCoeffs.set(k, accumulated);
            }
        }

        return new Polynomial<>(kSemiring, resultCoeffs);
    }


    // AlgebraicStructure impls
    @Override
    public String getName() {
        return "PolynomialSemiring over " + kSemiring.getName();
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
            if (!kSemiring.areEqual(a.getCoefficient(i), b.getCoefficient(i))) {
                return false;
            }
        }
        return true;
    }
}
