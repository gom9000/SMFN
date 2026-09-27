package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;

/**
 * Invarianti di PolynomialRootSolver sul catalogo di PolynomialRootTestValues, basati sulle
 * formule di Vieta: per un polinomio di grado n con coefficienti a_0..a_n,
 * somma delle radici == -a_(n-1)/a_n e prodotto delle radici == (-1)^n * a_0/a_n.
 * <p>
 * A differenza del test puntuale PolynomialRootSolverTest (che verifica radici note scritte a
 * mano su pochi casi), questo invariante e' verificabile su qualunque polinomio del catalogo
 * senza conoscerne le radici esatte: le formule di Vieta si derivano direttamente dai
 * coefficienti, quindi il confronto e' contro un calcolo indipendente, non contro un valore
 * annotato a mano (la stessa lezione appresa nel catalogo dei sistemi lineari). Si verifica in
 * aggiunta che ogni radice trovata soddisfi davvero P(radice) ~ 0, e che il numero di radici
 * trovate coincida col grado (completezza garantita su Complex, campo algebricamente chiuso).
 */
@DisplayName("PolynomialRootSolver: invarianti di Vieta (somma/prodotto delle radici) e completezza sul catalogo")
class PolynomialRootInvariantsTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private static final MetricSpace<Complex> SPACE = (a, b) -> new Real(C.subtract(a, b).modulus());
	private static final double EPSILON = 1e-6;

	private PolynomialRootSolver<Complex, ComplexField> newSolver() {
		return new PolynomialRootSolver<>(
			C, new Complex(1e-6, 0), SPACE, new Complex(0.4, 0.9),
			new StoppingParameters(new Real(1e-10), 200));
	}

	private static Complex vietaSum(Polynomial<Complex> p) {
		int n = p.degree();
		return C.negate(C.divide(p.getCoefficient(n - 1), p.getCoefficient(n)));
	}

	private static Complex vietaProduct(Polynomial<Complex> p) {
		int n = p.degree();
		Complex ratio = C.divide(p.getCoefficient(0), p.getCoefficient(n));
		return (n % 2 == 0) ? ratio : C.negate(ratio);
	}

	private static Complex evaluate(Polynomial<Complex> p, Complex x) {
		Complex result = C.zero();
		for (int i = p.degree(); i >= 0; i--) {
			result = C.add(C.multiply(result, x), p.getCoefficient(i));
		}
		return result;
	}

	@Test
	@DisplayName("Il numero di radici trovate coincide col grado del polinomio, su tutto il catalogo")
	void numberOfRootsMatchesDegree() {
		for (Polynomial<Complex> p : concat(PolynomialRootTestValues.monicCases(), PolynomialRootTestValues.nonMonicCases())) {
			List<Complex> roots = newSolver().findAllRoots(p);
			assertEquals(p.degree(), roots.size(), "polinomio: " + p);
		}
	}

	@Test
	@DisplayName("Ogni radice trovata soddisfa P(radice) ~ 0, verificato per valutazione diretta, su tutto il catalogo")
	void everyRootSatisfiesThePolynomial() {
		for (Polynomial<Complex> p : concat(PolynomialRootTestValues.monicCases(), PolynomialRootTestValues.nonMonicCases())) {
			List<Complex> roots = newSolver().findAllRoots(p);
			for (Complex root : roots) {
				Complex value = evaluate(p, root);
				assertTrue(value.modulus() < 1e-5, "P(" + root + ") = " + value + " dovrebbe essere ~0 per polinomio " + p);
			}
		}
	}

	@Test
	@DisplayName("Somma delle radici trovate == -a_(n-1)/a_n (formula di Vieta), su tutto il catalogo")
	void sumOfRootsMatchesVietaFormula() {
		for (Polynomial<Complex> p : concat(PolynomialRootTestValues.monicCases(), PolynomialRootTestValues.nonMonicCases())) {
			List<Complex> roots = newSolver().findAllRoots(p);

			Complex sum = C.zero();
			for (Complex root : roots) {
				sum = C.add(sum, root);
			}

			Complex expected = vietaSum(p);
			assertTrue(Math.abs(expected.getRe() - sum.getRe()) < EPSILON && Math.abs(expected.getIm() - sum.getIm()) < EPSILON,
				"polinomio: " + p + " atteso=" + expected + " ottenuto=" + sum);
		}
	}

	@Test
	@DisplayName("Prodotto delle radici trovate == (-1)^n * a_0/a_n (formula di Vieta), su tutto il catalogo")
	void productOfRootsMatchesVietaFormula() {
		for (Polynomial<Complex> p : concat(PolynomialRootTestValues.monicCases(), PolynomialRootTestValues.nonMonicCases())) {
			List<Complex> roots = newSolver().findAllRoots(p);

			Complex product = C.one();
			for (Complex root : roots) {
				product = C.multiply(product, root);
			}

			Complex expected = vietaProduct(p);
			assertTrue(Math.abs(expected.getRe() - product.getRe()) < EPSILON && Math.abs(expected.getIm() - product.getIm()) < EPSILON,
				"polinomio: " + p + " atteso=" + expected + " ottenuto=" + product);
		}
	}

	@SafeVarargs
	private static <T> List<T> concat(List<T>... lists) {
		List<T> result = new java.util.ArrayList<>();
		for (List<T> list : lists) {
			result.addAll(list);
		}
		return result;
	}
}
