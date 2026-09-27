package net.gommagomma.smfn.math.algebra.numerics;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo condiviso di valori {@link SignedInt}, sullo stesso schema di
 * {@link RationalTestValues}: nessun assert qui dentro, solo dati.
 * <p>
 * {@link #standardValues()} e' a magnitudine moderata (nessuna combinazione a coppie
 * va in overflow in add()/multiply(), che lanciano deliberatamente su overflow reale).
 * {@link #extremeValues()} include i bordi (0, ±1, Long.MAX_VALUE, Long.MIN_VALUE):
 * l'origine di BUG-03 (IntegerRing.quotient/remainder) e BUG-06 (MathUtils.gcd).
 */
final class SignedIntTestValues
{
	private SignedIntTestValues() {}

	static List<SignedInt> standardValues() {
		return List.of(
			new SignedInt(0),
			new SignedInt(1),
			new SignedInt(-1),
			new SignedInt(2),
			new SignedInt(-2),
			new SignedInt(10),
			new SignedInt(-10),
			new SignedInt(7),
			new SignedInt(-7),
			new SignedInt(1000)
		);
	}

	static List<SignedInt> extremeValues() {
		return List.of(
			new SignedInt(Long.MAX_VALUE),
			new SignedInt(Long.MIN_VALUE),
			new SignedInt(Long.MAX_VALUE - 1),
			new SignedInt(Long.MIN_VALUE + 1)
		);
	}

	static List<SignedInt> allValues() {
		List<SignedInt> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}

	/** Come allValues(), escludendo Long.MIN_VALUE: limite genuino per negate()/abs()/degree(). */
	static List<SignedInt> valuesSafeForNegationAndAbs() {
		List<SignedInt> result = new ArrayList<>();
		for (SignedInt v : allValues()) {
			if (v.getValue() != Long.MIN_VALUE) {
				result.add(v);
			}
		}
		return result;
	}
}
