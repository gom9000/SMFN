package net.gommagomma.smfn.math.linearalgebra.vectors;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Catalogo condiviso di valori {@link Vector}, sullo stesso schema delle catalog class di
 * {@code numerics}/{@code polynomial}: nessun assert qui dentro, solo dati.
 * <p>
 * A differenza dei tipi scalari esatti (Rational, SignedInt, ...), qui i componenti sono
 * {@link Real}/{@link Complex}: l'aritmetica in virgola mobile non lancia mai per overflow
 * (sconfina in Infinity), quindi non serve isolare un sottoinsieme "sicuro per combinazione"
 * come per Rational -- basta la consueta cautela con la tolleranza (vedi RealInvariantsTest)
 * quando si confrontano somme a scala estrema.
 * <p>
 * Tutti i vettori di questo catalogo hanno la stessa dimensione ({@link #DIMENSION}), dato che
 * add()/innerProduct() richiedono dimensioni compatibili.
 */
final class VectorTestValues
{
	private VectorTestValues() {}

	static final int DIMENSION = 3;
	static final InnerProductVectorSpace<Real, RealField> SPACE = new InnerProductVectorSpace<>(RealField.INSTANCE, DIMENSION);

	static final int COMPLEX_DIMENSION = 2;
	static final InnerProductVectorSpace<Complex, ComplexField> COMPLEX_SPACE = new InnerProductVectorSpace<>(ComplexField.INSTANCE, COMPLEX_DIMENSION);

	private static Vector<Real> v(double... components) {
		return VectorElementFactory.of(SPACE, components);
	}

	private static Vector<Complex> c(Complex... components) {
		return VectorElementFactory.of(COMPLEX_SPACE, components);
	}

	static List<Vector<Real>> standardValues() {
		return List.of(
			SPACE.zero(),           // (0,0,0)
			v(1, 0, 0),              // e1
			v(0, 1, 0),               // e2
			v(0, 0, 1),                // e3
			v(1, 1, 1),
			v(-1, -1, -1),
			v(3, 4, 0),                 // norma 5 (terna 3-4-5)
			v(1, -2, 2),                 // norma 3
			v(-3, 4, 0)
		);
	}

	static List<Vector<Real>> extremeValues() {
		return List.of(
			v(1e308, 0, 0),
			v(-1e308, 0, 0),
			v(1e-308, 1e-308, 1e-308),
			v(Double.MAX_VALUE, 0, 0),
			v(1e200, -1e200, 1e200)
		);
	}

	static List<Vector<Real>> allValues() {
		List<Vector<Real>> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}

	static List<Vector<Complex>> complexStandardValues() {
		return List.of(
			COMPLEX_SPACE.zero(),                                    // (0,0)
			c(new Complex(1, 0), new Complex(0, 0)),                   // e1
			c(new Complex(0, 0), new Complex(1, 0)),                    // e2
			c(new Complex(1, 1), new Complex(0, 2)),
			c(new Complex(2, 0), new Complex(1, -1)),
			c(new Complex(3, 4), new Complex(-1, 2)),
			c(new Complex(-1, -1), new Complex(1, 1))
		);
	}
}
