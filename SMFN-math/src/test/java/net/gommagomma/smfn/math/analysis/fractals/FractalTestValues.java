package net.gommagomma.smfn.math.analysis.fractals;

import java.util.List;

import net.gommagomma.smfn.math.algebra.numerics.Complex;

/**
 * Catalogo condiviso di punti del piano complesso per gli invarianti delle funzioni frattali
 * (Mandelbrot, Julia, Burning Ship, Newton), sullo stesso spirito delle altre catalog class del
 * progetto: nessun assert qui dentro, solo dati.
 * <p>
 * A differenza dei cataloghi puramente algebrici, qui gli invarianti non richiedono di sapere se
 * un punto specifico appartiene o no all'insieme (che dipenderebbe da una soglia arbitraria di
 * iterazioni): si usano invece punti la cui appartenenza/non appartenenza e' matematicamente
 * inequivocabile a qualunque budget ragionevole di iterazioni (l'origine e' nel cuore
 * dell'insieme di Mandelbrot; un punto a modulo maggiore di 2 diverge sempre al primo passo), e
 * proprieta' di simmetria che valgono per costruzione algebrica indipendentemente da dove si
 * trovi il confine dell'insieme.
 */
final class FractalTestValues
{
	private FractalTestValues() {}

	/** Punti notoriamente interni all'insieme di Mandelbrot: l'orbita resta limitata per sempre. */
	static List<Complex> mandelbrotInteriorPoints() {
		return List.of(
			new Complex(0.0, 0.0),     // centro della cardioide principale
			new Complex(-1.0, 0.0),      // centro del bulbo periodo-2
			new Complex(-0.5, 0.0),        // interno alla cardioide principale
			new Complex(-0.1, 0.1),          // interno alla cardioide principale
			new Complex(0.2, 0.0)             // interno alla cardioide principale, ben lontano dal bordo
		);
	}

	/** Punti notoriamente esterni all'insieme di Mandelbrot: |c| > 2 implica divergenza immediata (dimostrabile). */
	static List<Complex> mandelbrotExteriorPoints() {
		return List.of(
			new Complex(3.0, 0.0),
			new Complex(-3.0, 0.0),
			new Complex(2.0, 2.0),
			new Complex(0.0, 3.0),
			new Complex(-2.5, 1.5)
		);
	}

	/** Punti generici (interni ed esterni misti) per verificare la simmetria rispetto all'asse reale. */
	static List<Complex> genericPointsForSymmetryCheck() {
		return List.of(
			new Complex(0.3, 0.4),
			new Complex(-1.2, 0.5),
			new Complex(1.5, -0.8),
			new Complex(-0.7, 0.2),
			new Complex(0.0, 1.2)
		);
	}

	/** Costanti C reali per JuliaFunction: con C reale, l'orbita di z e quella del suo coniugato sono coniugate fra loro. */
	static List<Complex> realJuliaConstants() {
		return List.of(
			new Complex(-1.0, 0.0),
			new Complex(0.3, 0.0),
			new Complex(-0.75, 0.0)
		);
	}
}
