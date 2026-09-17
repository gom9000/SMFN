package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.List;

import org.junit.jupiter.api.DisplayName;

import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.RingAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;

/**
 * Verifica gli assiomi di Ring<Polynomial<Complex>> costruendo PolynomialRing
 * direttamente (non tramite PolynomialElementFactory), per isolare esattamente
 * il livello Ring dal livello CommutativeRing/EuclideanDomain che vengono
 * verificati separatamente in CommutativePolynomialRingTest ed
 * EuclideanPolynomialRingTest.
 */
@DisplayName("PolynomialRing<Complex>: assiomi di Anello")
class PolynomialRingTest extends RingAxiomContract<Polynomial<Complex>>
{
	private final ComplexField C = ComplexField.INSTANCE;
	private final PolynomialRing<Complex, ComplexField> polynomialRing = new PolynomialRing<>(C);

	@Override
	protected Ring<Polynomial<Complex>> structure() {
		return polynomialRing;
	}

	@Override
	protected Polynomial<Complex> a() {
		return new Polynomial<>(polynomialRing, C, List.of(C.of(1.0), C.of(2.0))); // 1 + 2x
	}

	@Override
	protected Polynomial<Complex> b() {
		return new Polynomial<>(polynomialRing, C, List.of(new Complex(0.0, 1.0))); // i
	}

	@Override
	protected Polynomial<Complex> c() {
		return new Polynomial<>(polynomialRing, C, List.of(C.zero(), C.zero(), C.one())); // x^2
	}
}
