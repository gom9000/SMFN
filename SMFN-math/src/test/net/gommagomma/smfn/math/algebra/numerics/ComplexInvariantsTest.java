package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.ComplexField;

/**
 * Invarianti algebrici di Complex sul catalogo di ComplexTestValues, sullo stesso spirito
 * di RationalInvariantsTest/RealInvariantsTest. Il catalogo estremo qui dentro include
 * proprio la combinazione che ha fatto emergere il bug di modulus()/inverse() risolto in
 * questa sessione (Complex(1e308, 1e308)): se un giorno regredisse, questi test lo
 * riprenderebbero senza bisogno di ricordarsi il caso specifico.
 */
@DisplayName("Complex: invarianti algebrici sul catalogo di valori (standard + estremi)")
class ComplexInvariantsTest
{
	private final ComplexField C = ComplexField.INSTANCE;

	@Test
	@DisplayName("Addizione commutativa, bit-esatta: a+b == b+a, su tutto il catalogo, estremi inclusi")
	void additionIsCommutative() {
		List<Complex> values = ComplexTestValues.allValues();
		for (Complex a : values) {
			for (Complex b : values) {
				assertEquals(C.add(a, b), C.add(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione commutativa, bit-esatta: a*b == b*a, su tutto il catalogo, estremi inclusi")
	void multiplicationIsCommutative() {
		List<Complex> values = ComplexTestValues.allValues();
		for (Complex a : values) {
			for (Complex b : values) {
				assertEquals(C.multiply(a, b), C.multiply(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa (a meno di arrotondamento): (a+b)+c ~= a+(b+c), su terne di valori standard")
	void additionIsAssociativeForStandardMagnitudes() {
		List<Complex> values = ComplexTestValues.standardValues();
		for (Complex a : values) {
			for (Complex b : values) {
				for (Complex c : values) {
					Complex left = C.add(C.add(a, b), c);
					Complex right = C.add(a, C.add(b, c));
					assertTrue(C.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva (a meno di arrotondamento): a*(b+c) ~= a*b + a*c, su terne di valori standard")
	void multiplicationDistributesOverAdditionForStandardMagnitudes() {
		List<Complex> values = ComplexTestValues.standardValues();
		for (Complex a : values) {
			for (Complex b : values) {
				for (Complex c : values) {
					Complex left = C.multiply(a, C.add(b, c));
					Complex right = C.add(C.multiply(a, b), C.multiply(a, c));
					assertTrue(C.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva (a meno di arrotondamento) su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (Complex a : ComplexTestValues.allValues()) {
			assertTrue(C.areEqual(a, C.add(a, C.zero())), "a=" + a);
		}
	}

	@Test
	@DisplayName("Uno e' identita' moltiplicativa (a meno di arrotondamento) su tutto il catalogo, estremi inclusi")
	void oneIsMultiplicativeIdentity() {
		for (Complex a : ComplexTestValues.allValues()) {
			assertTrue(C.areEqual(a, C.multiply(a, C.one())), "a=" + a);
		}
	}

	@Test
	@DisplayName("Inverso additivo: a + (-a) == 0 esattamente, su tutto il catalogo, estremi inclusi")
	void additiveInverseReturnsZero() {
		for (Complex a : ComplexTestValues.allValues()) {
			Complex sum = C.add(a, C.negate(a));
			assertTrue(C.areEqual(C.zero(), sum), "a=" + a + " sum=" + sum);
		}
	}

	@Test
	@DisplayName("Inverso moltiplicativo: z * z^-1 ~= 1 per ogni valore non nullo del catalogo, estremi inclusi (il caso esatto di BUG-05)")
	void multiplicativeInverseReturnsOne() {
		// Il target del confronto e' sempre 1 (scala moderata), a prescindere dalla scala di z:
		// per questo l'epsilon assoluto va bene anche per z a scala estrema, a differenza delle
		// leggi di chiusura (associativita'/distributivita'), dove serve una tolleranza relativa.
		for (Complex z : ComplexTestValues.allValues()) {
			if (C.isZero(z)) {
				continue;
			}
			Complex product = C.multiply(z, C.inverse(z));
			assertEquals(1.0, product.getRe(), 1e-6, "z=" + z + " z*inverse(z)=" + product);
			assertEquals(0.0, product.getIm(), 1e-6, "z=" + z + " z*inverse(z)=" + product);
		}
	}

	@Test
	@DisplayName("modulus() e' sempre finito e non negativo, su tutto il catalogo, estremi inclusi (regressione BUG-05)")
	void modulusIsAlwaysFiniteAndNonNegative() {
		for (Complex z : ComplexTestValues.allValues()) {
			double m = z.modulus();
			assertTrue(m >= 0.0, "z=" + z + " modulus=" + m);
			assertTrue(Double.isFinite(m), "z=" + z + " modulus=" + m + " dovrebbe restare finito per uno z finito");
		}
	}

	@Test
	@DisplayName("conjugate() e' involutiva: conj(conj(z)) == z, bit-esatta, su tutto il catalogo")
	void doubleConjugateReturnsOriginal() {
		for (Complex z : ComplexTestValues.allValues()) {
			assertEquals(z, z.conjugate().conjugate(), "z=" + z);
		}
	}

	@Test
	@DisplayName("modulus(conj(z)) == modulus(z), su tutto il catalogo (coniugare non cambia la magnitudine)")
	void conjugatePreservesModulus() {
		for (Complex z : ComplexTestValues.allValues()) {
			assertEquals(z.modulus(), z.conjugate().modulus(), 0.0, "z=" + z);
		}
	}

	@Test
	@DisplayName("z * conj(z) e' reale (parte immaginaria esattamente zero), quando il prodotto resta finito")
	void productWithConjugateIsReal() {
		for (Complex z : ComplexTestValues.allValues()) {
			Complex product = C.multiply(z, z.conjugate());
			if (!Double.isFinite(product.getRe()) || !Double.isFinite(product.getIm())) {
				continue; // |z|^2 puo' eccedere il range di un double per z estremo: limite noto, non un difetto
			}
			assertEquals(0.0, product.getIm(), "z=" + z + " z*conj(z)=" + product);
		}
	}

	@Test
	@DisplayName("LIMITE NOTO: moltiplicare due valori di modulo estremo puo' sconfinare in Infinity, correttamente rifiutato da contains()")
	void multiplicationOverflowIsRejectedByContains() {
		// A differenza di inverse() (dove il vero risultato e' piccolo anche per z enorme, e
		// l'overflow intermedio era un difetto evitabile: BUG-05), qui il prodotto di due moduli
		// entrambi vicini a Double.MAX_VALUE e' genuinamente troppo grande per un double -- non
		// c'e' riscalamento che lo eviti, e' un limite di rappresentazione, non un bug.
		Complex huge = new Complex(1e308, 1e308);
		Complex product = C.multiply(huge, huge);
		assertTrue(Double.isInfinite(product.getRe()) || Double.isInfinite(product.getIm()), "product=" + product);
		assertTrue(!C.contains(product));
	}
}
