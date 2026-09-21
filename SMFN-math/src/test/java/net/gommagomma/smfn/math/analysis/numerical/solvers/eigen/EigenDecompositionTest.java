package net.gommagomma.smfn.math.analysis.numerical.solvers.eigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

@DisplayName("EigenDecomposition: costruzione e vista reale")
class EigenDecompositionTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private final VectorSpace<Complex, ComplexField> space = new VectorSpace<>(C, 2);

	private Vector<Complex> complexVector(Complex a, Complex b) {
		return VectorElementFactory.of(space, a, b);
	}

	@Test
	@DisplayName("toRealDecomposition() riesce quando tutte le componenti sono reali entro la tolleranza")
	void toRealDecompositionSucceedsWhenEverythingIsReal() {
		EigenDecomposition decomposition = new EigenDecomposition(
			List.of(new Complex(1.0, 0.0), new Complex(3.0, 0.0)),
			List.of(complexVector(new Complex(1, 0), new Complex(0, 0)),
			        complexVector(new Complex(0, 0), new Complex(1, 0)))
		);

		RealEigenDecomposition real = decomposition.toRealDecomposition(new Real(1e-9));
		assertEquals(1.0, real.getEigenvalues().get(0).getValue(), 1e-12);
		assertEquals(3.0, real.getEigenvalues().get(1).getValue(), 1e-12);
	}

	@Test
	@DisplayName("toRealDecomposition() lancia se un autovalore e' genuinamente complesso")
	void toRealDecompositionThrowsForComplexEigenvalue() {
		EigenDecomposition decomposition = new EigenDecomposition(
			List.of(new Complex(0, 1), new Complex(0, -1)),
			List.of(complexVector(new Complex(1, 0), new Complex(0, 0)),
			        complexVector(new Complex(0, 0), new Complex(1, 0)))
		);

		assertThrows(IllegalStateException.class, () -> decomposition.toRealDecomposition(new Real(1e-9)));
	}

	@Test
	@DisplayName("toRealDecomposition() lancia se un autovettore ha una componente genuinamente complessa, anche con autovalori reali")
	void toRealDecompositionThrowsForComplexEigenvectorComponent() {
		EigenDecomposition decomposition = new EigenDecomposition(
			List.of(new Complex(1.0, 0.0), new Complex(2.0, 0.0)),
			List.of(complexVector(new Complex(1, 0), new Complex(0, 1)), // componente immaginaria non nulla
			        complexVector(new Complex(0, 0), new Complex(1, 0)))
		);

		assertThrows(IllegalStateException.class, () -> decomposition.toRealDecomposition(new Real(1e-9)));
	}
}
