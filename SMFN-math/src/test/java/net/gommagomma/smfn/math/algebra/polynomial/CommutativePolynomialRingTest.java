package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.CommutativeRingAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;

/**
 * Verifica gli assiomi di CommutativeRing<Polynomial<SignedInt>>.
 *
 * SignedInt (Z) e' un anello commutativo ma non un campo: e' esattamente il
 * caso che, prima della correzione in PolynomialStructureFactory, otteneva
 * silenziosamente solo un PolynomialRing semplice invece di un
 * CommutativePolynomialRing, perdendo la capacita' di commutativita' anche
 * se matematicamente dovuta. Questo test blocca quella regressione.
 */
@DisplayName("CommutativePolynomialRing<SignedInt>: assiomi di Anello Commutativo")
class CommutativePolynomialRingTest extends CommutativeRingAxiomContract<Polynomial<SignedInt>>
{
	private final IntegerRing Z = IntegerRing.INSTANCE;
	private final CommutativePolynomialRing<SignedInt, IntegerRing> ring = new CommutativePolynomialRing<>(Z);

	@Override
	protected CommutativeRing<Polynomial<SignedInt>> structure() {
		return ring;
	}

	@Override
	protected Polynomial<SignedInt> a() {
		return new Polynomial<>(ring, Z, List.of(new SignedInt(1), new SignedInt(2))); // 1 + 2x
	}

	@Override
	protected Polynomial<SignedInt> b() {
		return new Polynomial<>(ring, Z, List.of(new SignedInt(3))); // 3
	}

	@Override
	protected Polynomial<SignedInt> c() {
		return new Polynomial<>(ring, Z, List.of(Z.zero(), Z.zero(), new SignedInt(1))); // x^2
	}

	@Test
	@DisplayName("PolynomialStructureFactory sceglie CommutativePolynomialRing per scalari CommutativeRing non-Field")
	void factoryPicksCommutativeRingForIntegerCoefficients() {
		ScalarStructure<Polynomial<SignedInt>> chosen = PolynomialStructureFactory.getStructureFor(Z);
		assertTrue(chosen instanceof CommutativeRing, "Con coefficienti interi la struttura scelta deve dichiararsi CommutativeRing");
	}
}
