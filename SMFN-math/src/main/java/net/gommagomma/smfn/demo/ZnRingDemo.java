package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.numerics.ZnElement;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.algebra.structures.ZnRing;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.operators.CharacteristicPolynomialMapping;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorModule;

/**
 * Stress test di ZnRing/ZnElement: prova ogni capacita' che la libreria offre
 * per l'anello delle classi resto modulo n, dal contratto minimo (Ring, non
 * Field) fino alla sua composizione dentro Vector, SquareMatrix e Polynomial.
 */
public class ZnRingDemo
{
	static IntegerRing Z = IntegerRing.INSTANCE;

	public static void main(String[] args) {
		System.out.println("=== DEMO ZnRing: fin dove e' modellato Z/nZ in questa libreria ===\n");

		System.out.println("--- 1. Costruzione e cache delle istanze ---");
		testConstructionAndCache();

		System.out.println("\n--- 2. Normalizzazione del rappresentante ---");
		testNormalization();

		System.out.println("\n--- 3. Operazioni di anello (add, multiply, negate, subtract) ---");
		testRingOperations();

		System.out.println("\n--- 4. Elevamento a potenza (Exponentiable) ---");
		testExponentiation();

		System.out.println("\n--- 5. Sicurezza aritmetica con modulo grande (overflow di long) ---");
		testOverflowSafety();

		System.out.println("\n--- 6. Coerenza tra anelli distinti ---");
		testCrossModulusExceptions();

		System.out.println("\n--- 7. Onesta' del tipo: CommutativeRing, mai Field ---");
		testFieldHonesty();

		System.out.println("\n--- 8. Vector<ZnElement> su VectorModule ---");
		testVectorModule();

		System.out.println("\n--- 9. SquareMatrix<ZnElement> su SquareMatrixRing ---");
		testSquareMatrixRing();

		System.out.println("\n--- 10. Polynomial<ZnElement> su CommutativePolynomialRing ---");
		testPolynomialRing();

		System.out.println("\n--- 11. Polinomio caratteristico e Cayley-Hamilton su Z/nZ ---");
		testCharacteristicPolynomialAndCayleyHamilton();
	}

	private static void testConstructionAndCache() {
		ZnRing z7a = ZnRing.forModulus(Z.of(7));
		ZnRing z7b = ZnRing.forModulus(Z.of(7));
		ZnRing z12 = ZnRing.forModulus(Z.of(12));

		System.out.println("Z7 (prima chiamata):  " + z7a.getName());
		System.out.println("Z7 (seconda chiamata): " + z7b.getName());
		System.out.println("Le due istanze di Z7 sono la stessa istanza (cache): " + (z7a == z7b));
		System.out.println("Z7 e Z12 sono anelli distinti: " + !z7a.equals(z12));

		try {
			ZnRing.forModulus(Z.of(0));
			System.out.println("ERRORE: avrebbe dovuto lanciare per modulo 0");
		} catch (IllegalArgumentException e) {
			System.out.println("Modulo 0 rifiutato: " + e.getMessage());
		}
		try {
			ZnRing.forModulus(Z.of(-3));
			System.out.println("ERRORE: avrebbe dovuto lanciare per modulo negativo");
		} catch (IllegalArgumentException e) {
			System.out.println("Modulo -3 rifiutato: " + e.getMessage());
		}
	}

	private static void testNormalization() {
		ZnRing z7 = ZnRing.forModulus(Z.of(7));

		ZnElement fromOverflow = z7.getElement(new SignedInt(17)); // 17 mod 7 = 3
		ZnElement fromNegative = z7.getElement(new SignedInt(-1)); // -1 mod 7 = 6
		ZnElement fromExactMultiple = z7.getElement(new SignedInt(21)); // 21 mod 7 = 0

		System.out.println("getElement(17) su Z7 = " + fromOverflow + " (atteso 3)");
		System.out.println("getElement(-1) su Z7 = " + fromNegative + " (atteso 6, non -1: il rappresentante e' sempre in [0, n))");
		System.out.println("getElement(21) su Z7 = " + fromExactMultiple + " (atteso 0)");
		System.out.println("21 e -1 su Z7 sono lo stesso elemento di 0/6 rispettivamente: " +
			fromExactMultiple.equals(z7.zero()) + ", " + fromNegative.equals(z7.getElement(new SignedInt(6))));
	}

	private static void testRingOperations() {
		ZnRing z5 = ZnRing.forModulus(Z.of(5));
		ZnElement a = z5.getElement(new SignedInt(3));
		ZnElement b = z5.getElement(new SignedInt(4));

		System.out.println("a = " + a + ", b = " + b);
		System.out.println("a + b = " + z5.add(a, b) + " (atteso 2, cioe' 7 mod 5)");
		System.out.println("a * b = " + z5.multiply(a, b) + " (atteso 2, cioe' 12 mod 5)");
		System.out.println("-a = " + z5.negate(a) + " (atteso 2, cioe' -3 mod 5)");
		System.out.println("a - b = " + z5.subtract(a, b) + " (atteso 4, cioe' -1 mod 5; subtract e' un default di AdditiveGroup: add(a, negate(b)))");
		System.out.println("zero() = " + z5.zero() + ", isZero(zero()) = " + z5.isZero(z5.zero()));
		System.out.println("one()  = " + z5.one() + ", isOne(one()) = " + z5.isOne(z5.one()));
	}

	private static void testExponentiation() {
		ZnRing z5 = ZnRing.forModulus(Z.of(5));
		ZnElement a = z5.getElement(new SignedInt(3));

		System.out.println("a = " + a);
		System.out.println("a^0 = " + a.power(0) + " (atteso 1, per convenzione anche se a fosse 0)");
		System.out.println("a^2 = " + a.power(2) + " (atteso 4, cioe' 9 mod 5)");
		System.out.println("a^4 = " + a.power(4) + " (atteso 1: 3^4 = 81 = 16*5+1, coerente col piccolo teorema di Fermat visto che 5 e' primo e MCD(3,5)=1)");

		try {
			a.power(-1);
			System.out.println("ERRORE: avrebbe dovuto lanciare per esponente negativo");
		} catch (ArithmeticException e) {
			System.out.println("a^(-1) rifiutato: " + e.getMessage());
			System.out.println("Nota: la libreria non calcola l'inverso modulare (servirebbe Euclide Esteso), " +
				"ne' qui ne' altrove per ZnElement -- power(exp<0) e' l'unico punto dove questa mancanza e' visibile a runtime.");
		}
	}

	private static void testOverflowSafety() {
		// Un modulo vicino a Long.MAX_VALUE: a*b, prima della riduzione, supera
		// abbondantemente il range di un long anche se a,b singolarmente no.
		// ZnRing.reducedResult passa per BigInteger apposta per questo motivo.
		long bigModulusValue = Long.MAX_VALUE / 2;
		ZnRing bigRing = ZnRing.forModulus(new SignedInt(bigModulusValue));

		long halfway = bigModulusValue / 2 + 1;
		ZnElement x = bigRing.getElement(new SignedInt(halfway));

		ZnElement product = bigRing.multiply(x, x); // x*x supererebbe Long.MAX_VALUE se non si passasse per BigInteger
		System.out.println("modulo = " + bigModulusValue);
		System.out.println("x = " + halfway);
		System.out.println("x * x mod n = " + product + " (calcolato senza overflow tramite BigInteger internamente)");

		java.math.BigInteger expected = java.math.BigInteger.valueOf(halfway)
			.multiply(java.math.BigInteger.valueOf(halfway))
			.mod(java.math.BigInteger.valueOf(bigModulusValue));
		System.out.println("Verifica indipendente (BigInteger puro): " + expected +
			" -- coincide: " + (product.getValue().getValue() == expected.longValueExact()));
	}

	private static void testCrossModulusExceptions() {
		ZnRing z7 = ZnRing.forModulus(Z.of(7));
		ZnRing z12 = ZnRing.forModulus(Z.of(12));
		ZnElement a7 = z7.getElement(new SignedInt(3));
		ZnElement a12 = z12.getElement(new SignedInt(3));

		System.out.println("a7 = " + a7 + " (in Z7), a12 = " + a12 + " (in Z12) -- stesso valore, anelli diversi");
		System.out.println("a7.equals(a12) = " + a7.equals(a12) + " (mai uguali tra moduli diversi, anche a parita' di valore)");

		try {
			z7.add(a7, a12);
			System.out.println("ERRORE: avrebbe dovuto lanciare per moduli incompatibili");
		} catch (IllegalArgumentException e) {
			System.out.println("z7.add(a7, a12) rifiutato: " + e.getMessage());
		}
		try {
			z7.multiply(a7, a12);
			System.out.println("ERRORE: avrebbe dovuto lanciare per moduli incompatibili");
		} catch (IllegalArgumentException e) {
			System.out.println("z7.multiply(a7, a12) rifiutato: " + e.getMessage());
		}
	}

	private static void testFieldHonesty() {
		System.out.println("ZnRing e' final e implementa solo CommutativeRing<ZnElement>, mai Field<ZnElement>:");
		System.out.println("questa e' una garanzia data dal compilatore in base alla dichiarazione della classe,");
		System.out.println("indipendente dal fatto che n sia primo o no -- non c'e' alcun controllo a runtime");
		System.out.println("che promuova Z7 a Field solo perche' 7 e' primo. Quello che si puo' verificare e'");
		System.out.println("la CONSEGUENZA pratica di non essere mai un Field: l'assenza di un metodo per");
		System.out.println("l'inverso moltiplicativo, a prescindere da n. Per capire QUALI elementi avrebbero");
		System.out.println("comunque un inverso (se la libreria lo calcolasse), serve uscire da ZnElement e usare");
		System.out.println("IntegerRing.gcd(...) sui rappresentanti -- non e' un metodo di ZnRing.\n");

		System.out.println("Z12 (modulo composto): divisori dello zero");
		ZnRing z12 = ZnRing.forModulus(Z.of(12));
		ZnElement four = z12.getElement(new SignedInt(4));
		ZnElement three = z12.getElement(new SignedInt(3));
		System.out.println("4 * 3 mod 12 = " + z12.multiply(four, three) + " -- entrambi i fattori non nulli, il prodotto si'");
		System.out.println("Nessun x in Z12 puo' essere l'inverso di 4 (MCD(4,12) = " +
			Z.gcd(new SignedInt(4), new SignedInt(12)).getValue() + " != 1): ricerca esaustiva -> " +
			(findMultiplicativeInverse(z12, four) == null ? "nessun inverso trovato" : "trovato?! errore"));

		System.out.println("\nZ7 (modulo primo): nessun divisore dello zero, ma la libreria non lo sfrutta");
		ZnRing z7 = ZnRing.forModulus(Z.of(7));
		System.out.println("elemento | MCD(elemento,7) [via IntegerRing, fuori da ZnRing] | inverso trovato per ricerca esaustiva");
		for (int v = 1; v < 7; v++) {
			ZnElement e = z7.getElement(new SignedInt(v));
			long gcd = Z.gcd(new SignedInt(v), new SignedInt(7)).getValue();
			ZnElement inv = findMultiplicativeInverse(z7, e);
			System.out.println("   " + v + "      |            " + gcd + "                              | " +
				(inv == null ? "nessuno" : inv));
		}
		System.out.println("Ogni elemento non nullo di Z7 ha MCD 1 con 7 (perche' 7 e' primo) e infatti la ricerca");
		System.out.println("esaustiva trova sempre un inverso: Z7 SAREBBE un campo. La libreria pero' non lo");
		System.out.println("rappresenta come tale -- non fornisce ne' inverse() ne' divide() per ZnElement, mai.");
	}

	/**
	 * Ricerca esaustiva di un inverso moltiplicativo in Zn, usando solo le
	 * operazioni gia' offerte da ZnRing (multiply, one) -- nessun metodo di
	 * libreria fa questo per ZnElement, e' scritta apposta per questa demo.
	 */
	private static ZnElement findMultiplicativeInverse(ZnRing ring, ZnElement e) {
		long modulus = ring.getModulus().getValue();
		for (long candidate = 0; candidate < modulus; candidate++) {
			ZnElement c = ring.getElement(new SignedInt(candidate));
			if (ring.multiply(e, c).equals(ring.one())) {
				return c;
			}
		}
		return null;
	}

	private static void testVectorModule() {
		ZnRing z5 = ZnRing.forModulus(Z.of(5));
		VectorModule<ZnElement, ZnRing> V = new VectorModule<>(z5, 3);

		Vector<ZnElement> u = VectorElementFactory.of(V, z5.getElement(new SignedInt(4)), z5.getElement(new SignedInt(3)), z5.getElement(new SignedInt(1)));
		Vector<ZnElement> v = VectorElementFactory.of(V, z5.getElement(new SignedInt(2)), z5.getElement(new SignedInt(4)), z5.getElement(new SignedInt(4)));

		System.out.println("u = " + u);
		System.out.println("v = " + v);
		System.out.println("u + v = " + V.add(u, v) + " (componente per componente mod 5)");
		System.out.println("-u = " + V.negate(u));
		System.out.println("u - v = " + V.subtract(u, v));
		System.out.println("3 * u (scale) = " + V.scale(z5.getElement(new SignedInt(3)), u));
		System.out.println("zero() = " + V.zero());
	}

	private static void testSquareMatrixRing() {
		ZnRing z5 = ZnRing.forModulus(Z.of(5));
		SquareMatrixRing<ZnElement, ZnRing> ring = new SquareMatrixRing<>(z5, 2);

		// M = [[1,2],[3,4]] mod 5
		SquareMatrix<ZnElement> M = SquareMatrixElementFactory.of(z5,
			z5.getElement(new SignedInt(1)), z5.getElement(new SignedInt(2)),
			z5.getElement(new SignedInt(3)), z5.getElement(new SignedInt(4)));
		System.out.println("M (su Z5):\n" + M);
		System.out.println("Traccia: " + M.trace() + " (atteso 0, cioe' 5 mod 5)");
		System.out.println("Determinante (Laplace, nessuna divisione richiesta): " + ring.determinant(M) +
			" (atteso 3, cioe' 1*4 - 2*3 = -2 = 3 mod 5)");
		System.out.println("Trasposta:\n" + ring.transpose(M));
		System.out.println("M e' simmetrica: " + M.isSymmetric());

		// N simmetrica: [[1,2],[2,1]]
		SquareMatrix<ZnElement> N = SquareMatrixElementFactory.of(z5,
			z5.getElement(new SignedInt(1)), z5.getElement(new SignedInt(2)),
			z5.getElement(new SignedInt(2)), z5.getElement(new SignedInt(1)));
		System.out.println("\nN (simmetrica per costruzione):\n" + N);
		System.out.println("N e' simmetrica: " + N.isSymmetric());

		// M come operatore lineare: M * x
		VectorModule<ZnElement, ZnRing> V = new VectorModule<>(z5, 2);
		Vector<ZnElement> x = VectorElementFactory.of(V, z5.getElement(new SignedInt(1)), z5.getElement(new SignedInt(1)));
		System.out.println("\nx = " + x);
		System.out.println("M.apply(x) = M*x = " + M.apply(x) + " (atteso [3,2]: 1+2=3, 3+4=7=2 mod 5)");

		// Q ortogonale mod 5: righe e colonne ortonormali rispetto al prodotto scalare mod 5.
		// [[0,1],[1,0]] e' la sua stessa trasposta e Q^T*Q = I banalmente (matrice di permutazione).
		SquareMatrix<ZnElement> Q = SquareMatrixElementFactory.of(z5,
			z5.getElement(new SignedInt(0)), z5.getElement(new SignedInt(1)),
			z5.getElement(new SignedInt(1)), z5.getElement(new SignedInt(0)));
		System.out.println("\nQ (matrice di permutazione):\n" + Q);
		System.out.println("Q e' unitaria (Q^H*Q == I; conjugateTranspose degrada a transpose, ZnElement non e' Conjugable): " + Q.isUnitary());
	}

	private static void testPolynomialRing() {
		ZnRing z5 = ZnRing.forModulus(Z.of(5));

		Polynomial<ZnElement> p = PolynomialElementFactory.of(z5,
			z5.getElement(new SignedInt(3)), z5.getElement(new SignedInt(1)), z5.getElement(new SignedInt(2))); // 3 + x + 2x^2
		Polynomial<ZnElement> q = PolynomialElementFactory.of(z5,
			z5.getElement(new SignedInt(4)), z5.getElement(new SignedInt(2))); // 4 + 2x

		System.out.println("P(x) = " + p + "  (su Z5)");
		System.out.println("Q(x) = " + q);

		var polyRing = net.gommagomma.smfn.math.algebra.polynomial.PolynomialStructureFactory.getStructureFor(z5);
		@SuppressWarnings("unchecked")
		var ring = (net.gommagomma.smfn.math.algebra.polynomial.PolynomialRing<ZnElement, ZnRing>) polyRing;
		System.out.println("Struttura scelta automaticamente dalla factory (Z5 e' CommutativeRing, non Field): " + ring.getName());

		System.out.println("P + Q = " + ring.add(p, q));
		System.out.println("P * Q = " + ring.multiply(p, q));
		System.out.println("-P    = " + ring.negate(p));

		System.out.println("\nValutazione P(2) via Horner scritto a mano (nessun metodo evaluate() su Polynomial):");
		ZnElement two = z5.getElement(new SignedInt(2));
		ZnElement pAt2 = evaluatePolynomial(p, two, z5);
		System.out.println("P(2) = " + pAt2 + " (atteso: 3 + 2 + 2*4 = 13 = 3 mod 5)");
	}

	/** Horner puramente sulle operazioni di Ring (add, multiply): nessuna divisione richiesta. */
	private static ZnElement evaluatePolynomial(Polynomial<ZnElement> p, ZnElement x, ZnRing ring) {
		int deg = p.degree();
		if (deg < 0) return ring.zero();
		ZnElement result = p.getCoefficient(deg);
		for (int i = deg - 1; i >= 0; i--) {
			result = ring.add(ring.multiply(result, x), p.getCoefficient(i));
		}
		return result;
	}

	private static void testCharacteristicPolynomialAndCayleyHamilton() {
		ZnRing z5 = ZnRing.forModulus(Z.of(5));
		SquareMatrixRing<ZnElement, ZnRing> ring = new SquareMatrixRing<>(z5, 2);

		// Stessa matrice usata in PolynomialDemo per Cayley-Hamilton su Q, qui su Z5:
		// M = [[1,2],[3,4]], polinomio caratteristico P(x) = x^2 - 5x - 2 = x^2 + 0x + 3 mod 5.
		SquareMatrix<ZnElement> M = SquareMatrixElementFactory.of(z5,
			z5.getElement(new SignedInt(1)), z5.getElement(new SignedInt(2)),
			z5.getElement(new SignedInt(3)), z5.getElement(new SignedInt(4)));

		Polynomial<ZnElement> cp = (new CharacteristicPolynomialMapping<ZnElement, ZnRing>()).apply(M);
		System.out.println("M (su Z5):\n" + M);
		System.out.println("Polinomio caratteristico P(x) = " + cp);

		SquareMatrix<ZnElement> resultCH = evaluateMatrixPolynomial(cp, M, ring);
		System.out.println("P(M):\n" + resultCH);
		System.out.println("Verifica Cayley-Hamilton (vale su qualunque anello commutativo, non serve un Field): " +
			(ring.areEqual(resultCH, ring.zero()) ? "SUCCESSO" : "FALLITO"));
	}

	/**
	 * Horner per matrici, ma con un solo oggetto (SquareMatrixRing) invece dei due
	 * richiesti da PolynomialDemo.evaluateMatrixPolynomial (Ring + Algebra): scale()
	 * e add() sono gia' disponibili al livello SquareMatrixSemiring, che SquareMatrixRing
	 * eredita -- qui non serve affatto la capacita' di inversione di SquareMatrixAlgebra.
	 */
	private static SquareMatrix<ZnElement> evaluateMatrixPolynomial(Polynomial<ZnElement> p, SquareMatrix<ZnElement> m, SquareMatrixRing<ZnElement, ZnRing> ring) {
		int deg = p.degree();
		if (deg < 0) return ring.zero();

		SquareMatrix<ZnElement> result = ring.scale(p.getCoefficient(deg), ring.one());
		for (int i = deg - 1; i >= 0; i--) {
			result = ring.multiply(result, m);
			SquareMatrix<ZnElement> term = ring.scale(p.getCoefficient(i), ring.one());
			result = ring.add(result, term);
		}
		return result;
	}
}
