package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;

@DisplayName("PolynomialElementFactory: fabbrica pura di Polynomial<K>")
class PolynomialElementFactoryTest
{
	private final RealField R = RealField.INSTANCE;
	private final ComplexField C = ComplexField.INSTANCE;

	@Test
	@DisplayName("of(struttura, List<K>) costruisce i coefficienti dati")
	void ofFromList() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, List.of(R.of(1.0), R.of(2.0)));
		assertTrue(R.areEqual(p.getCoefficient(0), R.of(1.0)));
		assertTrue(R.areEqual(p.getCoefficient(1), R.of(2.0)));
		assertTrue(p.degree() == 1);
	}

	@Test
	@DisplayName("of(struttura, K...) costruisce dagli elementi passati per varargs")
	void ofFromVarargs() {
		Polynomial<Complex> p = PolynomialElementFactory.of(C, new Complex(0.0, 1.0), C.of(2.0));
		assertTrue(C.areEqual(p.getCoefficient(0), new Complex(0.0, 1.0)));
		assertTrue(C.areEqual(p.getCoefficient(1), C.of(2.0)));
	}

	@Test
	@DisplayName("of(struttura, double...) delega al NumericFactory dello scalare")
	void ofFromDoubles() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, 3.0, -1.5, 2.0);
		assertTrue(R.areEqual(p.getCoefficient(0), new Real(3.0)));
		assertTrue(R.areEqual(p.getCoefficient(1), new Real(-1.5)));
		assertTrue(R.areEqual(p.getCoefficient(2), new Real(2.0)));
	}

	@Test
	@DisplayName("Coefficienti tutti nulli producono il polinomio zero")
	void allZeroCoefficientsProduceZeroPolynomial() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, 0.0, 0.0, 0.0);
		assertTrue(p.degree() == -1);
	}
}
