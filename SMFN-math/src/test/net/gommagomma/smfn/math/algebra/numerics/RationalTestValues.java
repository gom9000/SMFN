package net.gommagomma.smfn.math.algebra.numerics;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo condiviso di valori {@link Rational} per i test di questo tipo: nessun
 * assert qui dentro, solo dati. L'idea e' che aggiungere un valore dimenticato sia
 * una riga sola, che si propaga automaticamente a tutti i test che consumano il
 * catalogo, invece di dover ricordarsi di ripeterlo in ogni classe di test.
 * <p>
 * {@link #standardValues()} e' a magnitudine moderata: nessuna combinazione a coppie
 * o terne va in overflow in add()/multiply() (utile per verificare leggi di chiusura
 * come commutativita'/associativita'/distributivita' senza incappare in un overflow
 * genuino e atteso, che e' un comportamento corretto, non un'anomalia da segnalare).
 * <p>
 * {@link #extremeValues()} include i casi limite (Long.MAX_VALUE, Long.MIN_VALUE,
 * denominatori enormi, coprimi enormi): utile per invarianti che devono reggere anche
 * ai bordi (compareTo, identita' additiva/moltiplicativa) o per isolare esplicitamente
 * dove un'operazione e' un limite rappresentazionale genuino (numeratore Long.MIN_VALUE
 * per abs()/negate()/inverse()).
 */
final class RationalTestValues
{
	private RationalTestValues() {}

	static List<Rational> standardValues() {
		return List.of(
			new Rational(0, 1),
			new Rational(1, 1),
			new Rational(-1, 1),
			new Rational(2, 1),
			new Rational(-2, 1),
			new Rational(1, 2),
			new Rational(-1, 2),
			new Rational(3, 4),
			new Rational(-3, 4),
			new Rational(5, 7),
			new Rational(-5, 7),
			new Rational(100, 3),
			new Rational(-100, 3)
		);
	}

	static List<Rational> extremeValues() {
		return List.of(
			new Rational(Long.MAX_VALUE, 1),
			new Rational(Long.MIN_VALUE, 1),
			new Rational(1, Long.MAX_VALUE),
			new Rational(-1, Long.MAX_VALUE),
			new Rational(Long.MAX_VALUE, Long.MAX_VALUE - 1),
			new Rational(Long.MAX_VALUE - 1, Long.MAX_VALUE - 2),
			new Rational(Long.MIN_VALUE, Long.MIN_VALUE), // riduce banalmente a 1/1
			new Rational(999999999989L, 999999999967L)    // coprimi enormi (primi, verificato con gcd)
		);
	}

	static List<Rational> allValues() {
		List<Rational> all = new ArrayList<>(standardValues());
		all.addAll(extremeValues());
		return all;
	}

	/**
	 * Come {@link #allValues()}, escludendo l'unico valore per cui negate()/abs() sono
	 * un limite rappresentazionale genuino (numeratore Long.MIN_VALUE, la cui negazione
	 * -2^63 -> +2^63 non sta in un long). Quel caso va verificato a parte, come
	 * eccezione attesa, non incluso in una verifica generica "non lancia mai".
	 */
	static List<Rational> valuesSafeForNegationAndAbs() {
		List<Rational> result = new ArrayList<>();
		for (Rational r : allValues()) {
			if (r.getNumerator() != Long.MIN_VALUE) {
				result.add(r);
			}
		}
		return result;
	}
}
