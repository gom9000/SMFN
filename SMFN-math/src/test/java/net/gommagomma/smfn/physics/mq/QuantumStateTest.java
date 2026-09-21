package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

@DisplayName("QuantumState: stato quantistico che porta con se' il proprio spazio")
class QuantumStateTest
{
	@Test
	@DisplayName("norm(): |0> ha norma 1, (1,1) non normalizzato ha norma sqrt(2)")
	void normIsCorrect() {
		QuantumState zero = QuantumState.of(new Complex(1, 0), new Complex(0, 0));
		assertEquals(1.0, zero.norm().getValue(), 1e-9);

		QuantumState unnormalized = QuantumState.of(new Complex(1, 0), new Complex(1, 0));
		assertEquals(Math.sqrt(2), unnormalized.norm().getValue(), 1e-9);
	}

	@Test
	@DisplayName("normalize(): dopo la normalizzazione la norma e' esattamente 1")
	void normalizeGivesUnitNorm() {
		QuantumState unnormalized = QuantumState.of(new Complex(3, 0), new Complex(4, 0));
		QuantumState normalized = unnormalized.normalize();
		assertEquals(1.0, normalized.norm().getValue(), 1e-9);
		// direzione preservata: (3,4)/5 = (0.6, 0.8)
		assertEquals(0.6, normalized.asVector().get(0).getRe(), 1e-9);
		assertEquals(0.8, normalized.asVector().get(1).getRe(), 1e-9);
	}

	@Test
	@DisplayName("innerProduct(): <0|1> = 0 (basi ortogonali), <0|0> = 1")
	void innerProductOfOrthogonalStatesIsZero() {
		QuantumState zero = QuantumState.of(new Complex(1, 0), new Complex(0, 0));
		QuantumState one = QuantumState.of(new Complex(0, 0), new Complex(1, 0));

		Complex orthogonal = zero.innerProduct(one);
		assertEquals(0.0, orthogonal.getRe(), 1e-9);
		assertEquals(0.0, orthogonal.getIm(), 1e-9);

		Complex self = zero.innerProduct(zero);
		assertEquals(1.0, self.getRe(), 1e-9);
	}

	@Test
	@DisplayName("plus() e scale(): costruire |+> = (|0>+|1>)/sqrt(2) componendo dai due pezzi")
	void plusAndScaleComposePlusState() {
		QuantumState zero = QuantumState.of(new Complex(1, 0), new Complex(0, 0));
		QuantumState one = QuantumState.of(new Complex(0, 0), new Complex(1, 0));

		QuantumState plus = zero.plus(one).normalize();

		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		assertEquals(invSqrt2, plus.asVector().get(0).getRe(), 1e-9);
		assertEquals(invSqrt2, plus.asVector().get(1).getRe(), 1e-9);
	}

	@Test
	@DisplayName("normalize() su uno stato di norma zero lancia, non restituisce NaN")
	void normalizeThrowsOnZeroNorm() {
		QuantumState zeroState = QuantumState.of(new Complex(0, 0), new Complex(0, 0));
		assertThrows(IllegalStateException.class, zeroState::normalize);
	}

	@Test
	@DisplayName("from(): costruito a partire da un Vector<Complex>, coincide con of() sugli stessi valori")
	void fromMatchesOf() {
		QuantumState direct = QuantumState.of(new Complex(1, 0), new Complex(0, 0));
		QuantumState viaFrom = QuantumState.from(direct.asVector());
		assertTrue(direct.asVector().equals(viaFrom.asVector()));
	}
}
