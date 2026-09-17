package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

@DisplayName("Polynomial<K>: comportamento dell'elemento (normalizzazione, grado, uguaglianza)")
class PolynomialTest
{
	private final RealField R = RealField.INSTANCE;

	@Test
	@DisplayName("La normalizzazione scarta gli zeri finali e riduce il grado")
	void normalizationDropsTrailingZeros() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, List.of(R.of(1.0), R.of(2.0), R.of(0.0), R.of(0.0)));
		assertEquals(1, p.degree());
	}

	@Test
	@DisplayName("Il polinomio nullo ha grado -1")
	void zeroPolynomialHasDegreeMinusOne() {
		Polynomial<Real> zero = PolynomialElementFactory.of(R, List.of());
		assertEquals(-1, zero.degree());
		assertTrue(R.areEqual(zero.getCoefficient(0), R.zero()));
	}

	@Test
	@DisplayName("getCoefficient oltre il grado restituisce zero, non un errore")
	void getCoefficientOutOfRangeReturnsZero() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, 1.0, 2.0); // grado 1
		assertTrue(R.areEqual(p.getCoefficient(5), R.zero()));
		assertTrue(R.areEqual(p.getCoefficient(-1), R.zero()));
	}

	@Test
	@DisplayName("equals/hashCode dipendono solo dai coefficienti normalizzati")
	void equalsAndHashCode() {
		Polynomial<Real> p1 = PolynomialElementFactory.of(R, 1.0, 2.0);
		Polynomial<Real> p2 = PolynomialElementFactory.of(R, 1.0, 2.0);
		Polynomial<Real> p3 = PolynomialElementFactory.of(R, 1.0, 2.0, 0.0); // stesso valore, coefficiente finale nullo

		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
		assertEquals(p1, p3, "Uno zero finale non deve cambiare l'uguaglianza dopo la normalizzazione");

		Polynomial<Real> different = PolynomialElementFactory.of(R, 1.0, 3.0);
		assertFalse(p1.equals(different));
	}

	@Test
	@DisplayName("copy() restituisce la stessa istanza, essendo Polynomial immutabile")
	void copyReturnsThis() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, 1.0, 2.0);
		assertSame(p, p.copy());
	}

	@Test
	@DisplayName("toString produce la forma leggibile dal grado piu' alto al piu' basso")
	void toStringFormat() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, 1.0, 1.0); // 1 + 1x
		assertEquals("(1.0)x + (1.0)", p.toString());
	}

	@Test
	@DisplayName("toString del polinomio nullo e' \"0\"")
	void toStringOfZero() {
		Polynomial<Real> zero = PolynomialElementFactory.of(R, List.of());
		assertEquals("0", zero.toString());
	}
}
