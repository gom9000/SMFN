package net.gommagomma.smfn.math.algebra.numerics;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo condiviso di valori {@link Complex} per i test di questo tipo, sullo stesso
 * schema di {@link RationalTestValues}/{@link RealTestValues}: nessun assert qui dentro,
 * solo dati.
 * <p>
 * {@link #extremeValues()} include proprio le combinazioni che hanno gia' fatto emergere
 * un bug reale in questa libreria (modulus()/inverse() su Complex(1e308, 1e308), risolto):
 * magnitudini vicine al limite di un double, e combinazioni fortemente asimmetriche tra
 * parte reale e immaginaria (Complex(1e-300, 1e300) e viceversa).
 */
final class ComplexTestValues
{
	private ComplexTestValues() {}

	static List<Complex> standardValues() {
		return List.of(
			new Complex(0.0, 0.0),
			new Complex(1.0, 0.0),
			new Complex(-1.0, 0.0),
			new Complex(0.0, 1.0),
			new Complex(0.0, -1.0),
			new Complex(1.0, 1.0),
			new Complex(-1.0, -1.0),
			new Complex(3.0, 4.0),
			new Complex(-3.0, 4.0),
			new Complex(2.5, -1.5)
		);
	}

	static List<Complex> extremeValues() {
		return List.of(
			new Complex(1e308, 1e308),     // il caso esatto della segnalazione originale (BUG-05)
			new Complex(-1e308, -1e308),
			new Complex(1e-300, 1e300),    // parte reale/immaginaria a scale opposte
			new Complex(1e300, 1e-300),
			new Complex(Double.MAX_VALUE, 0.0),
			new Complex(0.0, Double.MAX_VALUE),
			new Complex(Double.MIN_VALUE, Double.MIN_VALUE), // il piu' piccolo subnormale
			new Complex(1e-308, -1e-308)
		);
	}

	static List<Complex> allValues() {
		List<Complex> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}
}
