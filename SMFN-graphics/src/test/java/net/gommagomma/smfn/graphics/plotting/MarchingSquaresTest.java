package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test per {@link MarchingSquares}: i segmenti attesi si leggono dai valori ai vertici
 * (v00 in basso a sinistra, v10 in basso a destra, v11 in alto a destra, v01 in alto a sinistra).
 */
@DisplayName("MarchingSquares: casi della cella, sella, valori non finiti")
class MarchingSquaresTest
{
	private static final double EPSILON = 1e-12;

	private static List<double[]> run(double v00, double v10, double v11, double v01, double level) {
		List<double[]> out = new ArrayList<>();
		MarchingSquares.segments(v00, v10, v11, v01, level, (a, b, c, d) -> out.add(new double[] { a, b, c, d }));
		return out;
	}

	@Test
	@DisplayName("Cella tutta sopra o tutta sotto il livello: nessun segmento")
	void noCrossing() {
		assertEquals(0, run(1, 1, 1, 1, 0.5).size());
		assertEquals(0, run(0, 0, 0, 0, 0.5).size());
	}

	@Test
	@DisplayName("Un solo vertice sopra: un segmento che ne taglia l'angolo, interpolato linearmente")
	void singleCorner() {
		// v00 = 1 sopra, gli altri 0; livello 0.25: il taglio sta a 0.75 dal vertice sopra
		List<double[]> s = run(1, 0, 0, 0, 0.25);
		assertEquals(1, s.size());
		double[] seg = s.get(0);
		// estremi: (0.75, 0) sul lato basso e (0, 0.75) sul lato sinistro, in un ordine o nell'altro
		double[] a = { seg[0], seg[1] };
		double[] b = { seg[2], seg[3] };
		boolean direct = near(a, 0.75, 0) && near(b, 0, 0.75);
		boolean swapped = near(b, 0.75, 0) && near(a, 0, 0.75);
		assertEquals(true, direct || swapped);
	}

	@Test
	@DisplayName("Due vertici adiacenti sopra: un segmento che attraversa la cella da un lato all'altro")
	void twoAdjacentCorners() {
		// v00 e v10 sopra (riga in basso): il livello 0.5 e' una retta orizzontale a v = 0.5
		List<double[]> s = run(1, 1, 0, 0, 0.5);
		assertEquals(1, s.size());
		assertEquals(0.5, s.get(0)[1], EPSILON);
		assertEquals(0.5, s.get(0)[3], EPSILON);
	}

	@Test
	@DisplayName("Sella: due segmenti; il valore medio decide quali vertici sono uniti")
	void saddle() {
		// 00 e 11 sopra, 10 e 01 sotto
		// media sopra il livello: 00 e 11 uniti, le curve girano attorno a 10 e 01 -> segmenti con un estremo in basso e a destra
		List<double[]> joined = run(10, 0, 10, 0, 4);
		assertEquals(2, joined.size());
		// media sotto il livello: 00 e 11 isolati, le curve girano attorno a 00 e 11
		List<double[]> split = run(5, 0, 5, 0, 4.9);
		assertEquals(2, split.size());
		// con 10 e 01 sopra e media sopra il livello, i due segmenti stanno attorno a 00 e 11
		List<double[]> other = run(0, 10, 0, 10, 4);
		assertEquals(2, other.size());
		// Le due scelte danno geometrie diverse
		assertEquals(false, sameSegments(joined, run(0, 10, 0, 10, 4)));
	}

	@Test
	@DisplayName("Un vertice non finito: nessun segmento")
	void nonFiniteCorner() {
		assertEquals(0, run(Double.NaN, 0, 1, 1, 0.5).size());
		assertEquals(0, run(0, Double.POSITIVE_INFINITY, 1, 1, 0.5).size());
	}

	@Test
	@DisplayName("Livello uguale al valore di un vertice: il vertice conta come sopra, senza segmenti spuri")
	void levelOnVertex() {
		// tutti i vertici valgono il livello: tutti sopra
		assertEquals(0, run(1, 1, 1, 1, 1).size());
		// un vertice sul livello, gli altri sotto: curva ridotta attorno a quel vertice
		assertEquals(1, run(1, 0, 0, 0, 1).size());
	}

	private static boolean near(double[] p, double u, double v) {
		return Math.abs(p[0] - u) < EPSILON && Math.abs(p[1] - v) < EPSILON;
	}

	private static boolean sameSegments(List<double[]> a, List<double[]> b) {
		if (a.size() != b.size()) return false;
		for (int i = 0; i < a.size(); i++) {
			for (int k = 0; k < 4; k++) if (Math.abs(a.get(i)[k] - b.get(i)[k]) > EPSILON) return false;
		}
		return true;
	}
}
