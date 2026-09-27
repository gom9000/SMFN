package net.gommagomma.smfn.math.algebra.numerics;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo condiviso di valori {@link Real} per i test di questo tipo, sullo stesso
 * schema di {@link RationalTestValues}: nessun assert qui dentro, solo dati.
 * <p>
 * {@link #standardValues()} e' a magnitudine moderata (O(1)..O(100)): l'epsilon assoluto
 * di {@code RealField} (1e-12) e' adeguato a questa scala, quindi le leggi algebriche vi
 * si possono verificare con {@code RealField.areEqual(...)} senza sorprese.
 * <p>
 * {@link #extremeValues()} include i bordi della rappresentazione in virgola mobile:
 * magnitudini vicine ai limiti di un double (1e-308, 1e308), un subnormale
 * (Double.MIN_VALUE), Double.MAX_VALUE, e lo zero con segno (-0.0). A questa scala un
 * epsilon assoluto di 1e-12 non ha senso (l'errore di arrotondamento a 1e300 e' enorme
 * in assoluto pur essendo trascurabile in relativo): le leggi che coinvolgono
 * arrotondamento (associativita', distributivita') vanno verificate con una tolleranza
 * relativa qui, non con RealField.areEqual.
 */
final class RealTestValues
{
	private RealTestValues() {}

	static List<Real> standardValues() {
		return List.of(
			new Real(0.0),
			new Real(1.0),
			new Real(-1.0),
			new Real(2.5),
			new Real(-2.5),
			new Real(100.0),
			new Real(-100.0),
			new Real(0.001),
			new Real(-0.001),
			new Real(3.14159),
			new Real(-3.14159)
		);
	}

	static List<Real> extremeValues() {
		return List.of(
			new Real(1e-308),
			new Real(1e308),
			new Real(-1e308),
			new Real(1e-200),
			new Real(1e200),
			new Real(Double.MIN_VALUE),   // il piu' piccolo subnormale positivo, ~4.9e-324
			new Real(Double.MAX_VALUE),
			new Real(-Double.MAX_VALUE),
			new Real(Double.MIN_NORMAL),
			new Real(-0.0)
		);
	}

	static List<Real> allValues() {
		List<Real> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}

	/** Solo i valori non negativi del catalogo (utile per sqrt(), che rifiuta i negativi). */
	static List<Real> nonNegativeValues() {
		List<Real> result = new ArrayList<>();
		for (Real r : allValues()) {
			if (r.getValue() >= 0.0) {
				result.add(r);
			}
		}
		return result;
	}
}
