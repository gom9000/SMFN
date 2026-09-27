package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;

/**
 * Catalogo condiviso di polinomi a coefficienti complessi per gli invarianti di
 * PolynomialRootSolver, sullo stesso spirito delle altre catalog class del progetto: nessun
 * assert qui dentro, solo dati.
 * <p>
 * A differenza del catalogo di root-finding scalare, qui non serve annotare le radici attese a
 * mano: le formule di Vieta (somma delle radici == -a_(n-1)/a_n, prodotto delle radici ==
 * (-1)^n * a_0/a_n) sono derivabili direttamente dai coefficienti del polinomio, quindi
 * l'invariante si verifica confrontando con un calcolo indipendente sui coefficienti stessi,
 * non con un valore scritto a mano (la stessa lezione appresa nel catalogo dei sistemi lineari).
 */
final class PolynomialRootTestValues
{
	private PolynomialRootTestValues() {}

	private static final ComplexField C = ComplexField.INSTANCE;

	private static Polynomial<Complex> p(double... realCoefficients) {
		Complex[] coefficients = new Complex[realCoefficients.length];
		for (int i = 0; i < realCoefficients.length; i++) {
			coefficients[i] = new Complex(realCoefficients[i], 0.0);
		}
		return PolynomialElementFactory.of(C, coefficients);
	}

	static List<Polynomial<Complex>> monicCases() {
		return List.of(
			p(-6, 11, -6, 1),        // (x-1)(x-2)(x-3), grado 3
			p(1, 0, 1),                // x^2+1, radici +-i, grado 2
			p(-6, 1, 1),                 // (x-2)(x+3) = x^2+x-6, grado 2
			p(24, -50, 35, -10, 1),        // (x-1)(x-2)(x-3)(x-4), grado 4
			p(0, -1, 0, 1)                  // x^3-x = x(x-1)(x+1), grado 3, radice zero inclusa
		);
	}

	static List<Polynomial<Complex>> nonMonicCases() {
		return List.of(
			p(4, -6, 2),              // 2(x-1)(x-2) = 2x^2-6x+4, grado 2, leading coeff 2
			p(-30, 51, -24, 3)          // 3(x-1)(x-2)(x-5) = 3x^3-24x^2+51x-30 (verificato per espansione)
		);
	}
}
