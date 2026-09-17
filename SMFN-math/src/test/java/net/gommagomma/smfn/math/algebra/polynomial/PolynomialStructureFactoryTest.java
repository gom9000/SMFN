package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

@DisplayName("PolynomialStructureFactory: classificazione algebrica dello scalare")
class PolynomialStructureFactoryTest
{
	@Test
	@DisplayName("Field -> EuclideanPolynomialRing")
	void fieldGivesEuclideanPolynomialRing() {
		ScalarStructure<Polynomial<net.gommagomma.smfn.math.algebra.numerics.Rational>> chosen =
			PolynomialStructureFactory.getStructureFor(RationalField.INSTANCE);
		assertTrue(chosen instanceof EuclideanPolynomialRing);
	}

	@Test
	@DisplayName("CommutativeRing non-Field -> CommutativePolynomialRing (non Euclideo)")
	void commutativeRingGivesCommutativePolynomialRing() {
		ScalarStructure<Polynomial<SignedInt>> chosen = PolynomialStructureFactory.getStructureFor(IntegerRing.INSTANCE);
		assertTrue(chosen instanceof CommutativePolynomialRing);
		assertFalse(chosen instanceof EuclideanPolynomialRing, "Z non e' un campo: niente divisione euclidea");
	}

	@Test
	@DisplayName("Semiring puro (non Ring) -> PolynomialSemiring (nessun negate)")
	void semiringGivesPolynomialSemiring() {
		ScalarStructure<Polynomial<Natural>> chosen = PolynomialStructureFactory.getStructureFor(NaturalSemiring.INSTANCE);
		assertTrue(chosen instanceof PolynomialSemiring);
		assertFalse(chosen instanceof Ring, "N non e' un gruppo additivo: niente negate() sui polinomi risultanti");
	}

	@Test
	@DisplayName("Ring puro non-commutativo -> PolynomialRing semplice (non Commutative, non Euclideo)")
	void plainRingGivesPlainPolynomialRing() {
		ScalarStructure<Polynomial<SignedInt>> chosen = PolynomialStructureFactory.getStructureFor(new NonCommutativeIntegerRingFacade());
		assertTrue(chosen instanceof PolynomialRing);
		assertFalse(chosen instanceof CommutativeRing, "La facciata dichiara solo Ring: niente commutativita' garantita a valle");
	}

	/**
	 * Facciata di test: delega tutto a IntegerRing ma dichiara solo Ring<SignedInt>,
	 * non CommutativeRing -- serve solo a esercitare quel ramo di getStructureFor,
	 * dato che in algebra.* non esiste (giustamente) nessun anello non commutativo reale.
	 */
	private static final class NonCommutativeIntegerRingFacade
	implements Ring<SignedInt>, ScalarStructure<SignedInt>
	{
		private final IntegerRing delegate = IntegerRing.INSTANCE;

		@Override public String getName() { return "Non-commutative facade over " + delegate.getName(); }
		@Override public boolean contains(SignedInt e) { return delegate.contains(e); }
		@Override public boolean areEqual(SignedInt a, SignedInt b) { return delegate.areEqual(a, b); }
		@Override public SignedInt zero() { return delegate.zero(); }
		@Override public SignedInt add(SignedInt a, SignedInt b) { return delegate.add(a, b); }
		@Override public SignedInt one() { return delegate.one(); }
		@Override public SignedInt multiply(SignedInt a, SignedInt b) { return delegate.multiply(a, b); }
		@Override public SignedInt negate(SignedInt e) { return delegate.negate(e); }
		@Override public net.gommagomma.smfn.math.algebra.numerics.Real magnitude(SignedInt e) { return delegate.magnitude(e); }
		@Override public boolean isExact() { return true; }
	}
}
