package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/** Test per {@link SurfaceGrid}: campioni, coordinate dei vertici, valori non finiti, campioni al centro. */
@DisplayName("SurfaceGrid: campionamento, vertici, centri, campioni non finiti")
class SurfaceGridTest
{
	private static final Viewport3D BOX = new Viewport3D(-2, 4, 10, 11, 0, 1, 200, 200, 0, 0);
	private static final Mapping<Complex, Real> SUM = c -> new Real(c.getRe() + 10.0 * c.getIm());

	private static SurfaceGrid<Real> grid(Mapping<Complex, Real> f, int cx, int cy) {
		return SurfaceGrid.sample(BOX, f, Complex::new, Real::getValue, cx, cy);
	}

	@Test
	@DisplayName("Coordinate dei vertici: da min a max, l'ultimo esattamente su max")
	void vertexCoordinates() {
		SurfaceGrid<Real> g = grid(SUM, 3, 5);
		assertEquals(-2.0, g.x(0), 0.0);
		assertEquals(0.0, g.x(1), 1e-12);
		assertEquals(4.0, g.x(3), 0.0);
		assertEquals(10.0, g.y(0), 0.0);
		assertEquals(11.0, g.y(5), 0.0);
	}

	@Test
	@DisplayName("Altezza e valore di ogni vertice sono quelli della funzione")
	void vertexHeightsAndValues() {
		SurfaceGrid<Real> g = grid(SUM, 3, 5);
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 5; j++) {
				double expected = g.x(i) + 10.0 * g.y(j);
				assertEquals(expected, g.height(i, j), 1e-12);
				assertEquals(expected, g.vertexValue(i, j).getValue(), 1e-12);
				assertTrue(g.isFinite(i, j));
			}
		}
	}

	@Test
	@DisplayName("Valore al centro di una cella: la funzione nel punto medio")
	void centerValues() {
		SurfaceGrid<Real> g = grid(SUM, 2, 2);
		assertEquals(2.5 + 10.0 * 10.25, g.centerValue(1, 0).getValue(), 1e-12);
		assertEquals(-0.5 + 10.0 * 10.75, g.centerValue(0, 1).getValue(), 1e-12);
	}

	@Test
	@DisplayName("Intervallo delle altezze: minimo e massimo dei valori finiti; nessun valore finito: null")
	void heightRange() {
		SurfaceGrid<Real> g = grid(SUM, 3, 5);
		double[] range = g.heightRange();
		assertNotNull(range);
		assertEquals(-2.0 + 100.0, range[0], 1e-12);
		assertEquals(4.0 + 110.0, range[1], 1e-12);
		assertNull(grid(c -> new Real(Double.NaN), 3, 3).heightRange());
	}

	@Test
	@DisplayName("Vertici non finiti: segnalati, e le celle che li toccano non sono complete")
	void nonFiniteVertices() {
		// con 2 x 2 celle su [-1, 1]^2 il vertice (1, 1) e' l'origine
		Viewport3D box = new Viewport3D(-1, 1, -1, 1, 0, 1, 200, 200, 0, 0);
		SurfaceGrid<Real> g = SurfaceGrid.sample(box, c -> new Real(c.getRe() == 0.0 && c.getIm() == 0.0 ? Double.POSITIVE_INFINITY : 0.0),
			Complex::new, Real::getValue, 2, 2);
		assertEquals(false, g.isFinite(1, 1));
		assertEquals(true, g.isFinite(0, 0));
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++) assertEquals(false, g.isCellComplete(i, j));
	}

	@Test
	@DisplayName("Celle meno di 1: eccezione")
	void invalidCells() {
		assertThrows(IllegalArgumentException.class, () -> grid(SUM, 0, 3));
		assertThrows(IllegalArgumentException.class, () -> grid(SUM, 3, 0));
	}
}
