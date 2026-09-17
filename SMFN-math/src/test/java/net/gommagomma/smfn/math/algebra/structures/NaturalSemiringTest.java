package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.SemiringAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

@DisplayName("NaturalSemiring: assiomi di Semianello (N)")
class NaturalSemiringTest extends SemiringAxiomContract<Natural>
{
	private final NaturalSemiring naturalSemiring = NaturalSemiring.INSTANCE;

	@Override
	protected Semiring<Natural> structure() {
		return naturalSemiring;
	}

	@Override
	protected Natural a() { return new Natural(2); }
	@Override
	protected Natural b() { return new Natural(3); }
	@Override
	protected Natural c() { return new Natural(5); }

	@Test
	@DisplayName("Singleton")
	void isSingleton() {
		assertSame(NaturalSemiring.INSTANCE, naturalSemiring);
	}

	@Test
	@DisplayName("Nome")
	void name() {
		assertTrue("Natural Semiring (N)".equals(naturalSemiring.getName()));
	}

	@Test
	@DisplayName("Moltiplicazione commutativa (non richiesta da Semiring, ma vera per N)")
	void multiplicationHappensToBeCommutative() {
		assertTrue(naturalSemiring.areEqual(
			naturalSemiring.multiply(a(), b()),
			naturalSemiring.multiply(b(), a())
		));
	}

	// Nota: NaturalSemiring e' final e implementa solo Semiring, non AdditiveGroup --
	// e' il compilatore stesso a garantire che non possa mai esporre negate()/subtract(),
	// quindi non serve (e non si potrebbe) verificarlo con un instanceof a runtime:
	// il tipo lo impedisce gia' staticamente. Vedi la dichiarazione della classe.
}
