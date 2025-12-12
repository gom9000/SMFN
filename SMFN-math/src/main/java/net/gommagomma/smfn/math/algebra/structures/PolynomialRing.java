package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;

/**
 * Rappresenta la struttura algebrica dell'Anello dei Polinomi K[x].
 * Agisce come Factory per creare elementi polinomiali validi su K.
 *
 * @param <K> Il tipo dei coefficienti.
 */
public class PolynomialRing<K extends SemiringElement<K>>
implements CommutativeRing<Polynomial<K>>
{
    private final Semiring<K> scalarStructure;

    /**
     * Costruisce l'anello dei polinomi su una data struttura scalare.
     * @param scalarStructure La struttura K (es. IntegerRing, RationalField).
     */
    public PolynomialRing(Semiring<K> scalarStructure) {
        this.scalarStructure = scalarStructure;
    }

    public Semiring<K> getScalarStructure() {
        return scalarStructure;
    }

    @Override
    public String getName() {
        return scalarStructure.getName() + "[x]";
    }

    @Override
    public boolean contains(Polynomial<K> e) {
        // Una verifica rigorosa controllerebbe se tutti i coefficienti appartengono a scalarStructure.
        // Qui ci accontentiamo del controllo di tipo Java.
        return e != null;
    }

    // --- Factory Methods ---

    /**
     * Crea un polinomio dai coefficienti forniti.
     * @param coeffs Array di coefficienti [c0, c1, ..., cn]
     */
    public Polynomial<K> create(K[] coeffs) {
        return Polynomial.of(coeffs, scalarStructure.zero());
    }

    /**
     * Crea un monomio c * x^deg.
     */
    public Polynomial<K> createMonomial(K coefficient, int degree) {
        return Polynomial.monomial(coefficient, degree, scalarStructure.zero());
    }

    @Override
    public Polynomial<K> additiveIdentity() {
        return Polynomial.zero(scalarStructure.zero());
    }

    @Override
    public Polynomial<K> multiplicativeIdentity() {
        return Polynomial.one(scalarStructure.one(), scalarStructure.zero());
    }

	@Override
	public Polynomial<K> of(double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Polynomial<K> of(long value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Polynomial<K> of(int value) {
		// TODO Auto-generated method stub
		return null;
	}
}