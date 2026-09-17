package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;

/**
 * Contratto d'assioma per un EuclideanDomain<E,N>: eredita tutti gli assiomi
 * di anello commutativo e aggiunge la verifica della divisione euclidea
 * (a = q*b + r, con grado del resto minore del grado del divisore) e le
 * propriet fondamentali di gcd/lcm.
 *
 * b() fornito dalla sottoclasse deve essere non nullo.
 */
public abstract class EuclideanDomainAxiomContract<E extends ScalarElement<E>, N extends ScalarElement<N> & Comparable<N>>
extends CommutativeRingAxiomContract<E>
{
	@Override
	protected abstract EuclideanDomain<E, N> structure();

	@Nested
	@DisplayName("Divisione Euclidea")
	class EuclideanDivisionAxioms
	{
		@Test
		@DisplayName("a = q*b + r")
		void divisionIdentity() {
			E q = structure().quotient(a(), b());
			E r = structure().remainder(a(), b());
			E reconstructed = structure().add(structure().multiply(q, b()), r);
			assertTrue(structure().areEqual(reconstructed, a()));
		}

		@Test
		@DisplayName("Il grado del resto  minore del grado del divisore, o il resto  zero")
		void remainderDegreeIsSmaller() {
			E r = structure().remainder(a(), b());
			if (!structure().isZero(r)) {
				N degreeRemainder = structure().degree(r);
				N degreeDivisor = structure().degree(b());
				assertTrue(degreeRemainder.compareTo(degreeDivisor) < 0);
			}
		}

		@Test
		@DisplayName("Divisione per zero non  definita")
		void divisionByZeroIsUndefined() {
			assertThrows(ArithmeticException.class, () -> structure().quotient(a(), structure().zero()));
		}
	}

	@Nested
	@DisplayName("Massimo Comun Divisore / minimo comune multiplo")
	class GcdLcmAxioms
	{
		@Test
		@DisplayName("gcd(a,b) divide sia a che b")
		void gcdDividesBoth() {
			E gcd = structure().gcd(a(), b());
			assertTrue(structure().isZero(structure().remainder(a(), gcd)));
			assertTrue(structure().isZero(structure().remainder(b(), gcd)));
		}

		@Test
		@DisplayName("lcm(a,b)  divisibile sia per a che per b")
		void lcmIsDivisibleByBoth() {
			E lcm = structure().lcm(a(), b());
			assertTrue(structure().isZero(structure().remainder(lcm, a())));
			assertTrue(structure().isZero(structure().remainder(lcm, b())));
		}
	}
}
