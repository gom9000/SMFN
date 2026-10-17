package net.gommagomma.smfn.graphics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test per {@link Light}. Una luce con elevazione 90 e' sopra la scena: una faccia orizzontale
 * (normale verso l'alto) la guarda in pieno, una verticale di taglio.
 */
@DisplayName("Light: intensita' per inclinazione, ambiente, colore, validazione")
class LightTest
{
	private static final double EPSILON = 1e-9;

	@Test
	@DisplayName("Luce dall'alto: faccia orizzontale piena, faccia verticale solo ambiente, faccia capovolta solo ambiente")
	void lightFromAbove() {
		Light light = new Light(0, 90, 0.25);
		assertEquals(1.0, light.intensity(0, 0, 1), EPSILON);
		assertEquals(0.25, light.intensity(1, 0, 0), EPSILON);
		assertEquals(0.25, light.intensity(0, -3, 0), EPSILON);
		assertEquals(0.25, light.intensity(0, 0, -1), EPSILON);
	}

	@Test
	@DisplayName("Luce all'orizzonte: la faccia orizzontale e' di taglio, quella rivolta alla luce e' piena")
	void lightAtHorizon() {
		// azimut 90, elevazione 0: la luce sta dal lato di x minimo
		Light light = new Light(90, 0, 0.0);
		assertEquals(0.0, light.intensity(0, 0, 1), EPSILON);
		assertEquals(1.0, light.intensity(-1, 0, 0), EPSILON);
		assertEquals(0.0, light.intensity(1, 0, 0), EPSILON);
	}

	@Test
	@DisplayName("L'intensita' non dipende dalla lunghezza della normale")
	void intensityIgnoresNormalLength() {
		Light light = new Light(35, 40, 0.2);
		assertEquals(light.intensity(0.3, -0.2, 1.0), light.intensity(3.0, -2.0, 10.0), EPSILON);
		assertEquals(light.intensity(0.3, -0.2, 1.0), light.intensity(0.003, -0.002, 0.01), EPSILON);
	}

	@Test
	@DisplayName("Ambiente estremi: 0 -> ombra nera, 1 -> sempre piena luminosita'")
	void ambientExtremes() {
		assertEquals(0.0, new Light(0, 90, 0.0).intensity(0, 0, -1), EPSILON);
		for (double[] n : new double[][] { { 0, 0, 1 }, { 1, 0, 0 }, { 0, 0, -1 }, { -2, 5, 1 } }) {
			assertEquals(1.0, new Light(20, 30, 1.0).intensity(n[0], n[1], n[2]), EPSILON);
		}
	}

	@Test
	@DisplayName("L'intensita' sta sempre fra ambiente e 1")
	void intensityBounds() {
		Light light = new Light(-70, 25, 0.3);
		for (double az = -180; az <= 180; az += 30) {
			for (double el = -90; el <= 90; el += 30) {
				double[] n = Viewport3D.directionToward(az, el);
				double i = light.intensity(n[0], n[1], n[2]);
				assertTrue(i >= 0.3 - EPSILON && i <= 1.0 + EPSILON, "intensita' " + i);
			}
		}
	}

	@Test
	@DisplayName("illuminate scala r, g, b e lascia intatto l'alfa; intensita' piena non cambia il colore")
	void illuminateScalesColor() {
		Light light = new Light(0, 90, 0.5);
		RgbColor base = new RgbColor(200, 100, 50, 77);

		RgbColor lit = light.illuminate(base, 0, 0, 1);
		assertEquals(base, lit);

		RgbColor shadow = light.illuminate(base, 1, 0, 0); // intensita' 0.5
		assertEquals(new RgbColor(100, 50, 25, 77), shadow);
	}

	@Test
	@DisplayName("Parametri non validi: IllegalArgumentException")
	void invalidArguments() {
		assertThrows(IllegalArgumentException.class, () -> new Light(Double.NaN, 0, 0.5));
		assertThrows(IllegalArgumentException.class, () -> new Light(Double.POSITIVE_INFINITY, 0, 0.5));
		assertThrows(IllegalArgumentException.class, () -> new Light(0, 90.1, 0.5));
		assertThrows(IllegalArgumentException.class, () -> new Light(0, -90.1, 0.5));
		assertThrows(IllegalArgumentException.class, () -> new Light(0, Double.NaN, 0.5));
		assertThrows(IllegalArgumentException.class, () -> new Light(0, 0, -0.01));
		assertThrows(IllegalArgumentException.class, () -> new Light(0, 0, 1.01));
		assertThrows(IllegalArgumentException.class, () -> new Light(0, 0, Double.NaN));

		Light light = new Light(0, 0, 0.5);
		assertThrows(IllegalArgumentException.class, () -> light.intensity(0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> light.intensity(Double.NaN, 0, 1));
	}
}
