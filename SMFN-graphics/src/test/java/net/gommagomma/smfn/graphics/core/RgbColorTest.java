package net.gommagomma.smfn.graphics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test per {@link RgbColor}: validazione dei canali, uguaglianza, conversione da HSB. */
@DisplayName("RgbColor: canali, uguaglianza, HSB")
class RgbColorTest
{
	@Test
	@DisplayName("Canali nei limiti 0 e 255; l'alfa di default e' 255")
	void channels() {
		RgbColor c = new RgbColor(0, 128, 255);
		assertEquals(0, c.r);
		assertEquals(128, c.g);
		assertEquals(255, c.b);
		assertEquals(255, c.a);
		assertEquals(10, new RgbColor(1, 2, 3, 10).a);
	}

	@Test
	@DisplayName("Un canale fuori da [0, 255] viene rifiutato")
	void outOfRange() {
		assertThrows(IllegalArgumentException.class, () -> new RgbColor(-1, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new RgbColor(0, 256, 0));
		assertThrows(IllegalArgumentException.class, () -> new RgbColor(0, 0, 0, 300));
	}

	@Test
	@DisplayName("Uguaglianza e hashCode per valore, alfa compreso")
	void equality() {
		assertEquals(new RgbColor(1, 2, 3), new RgbColor(1, 2, 3, 255));
		assertEquals(new RgbColor(1, 2, 3).hashCode(), new RgbColor(1, 2, 3).hashCode());
		assertEquals(false, new RgbColor(1, 2, 3).equals(new RgbColor(1, 2, 3, 200)));
		assertEquals(false, new RgbColor(1, 2, 3).equals(null));
	}

	@Test
	@DisplayName("HSB: i colori primari e secondari alle tinte 0, 1/6, 1/3, ...")
	void hsbPrimaries() {
		assertEquals(RgbColor.RED, RgbColor.ofHsb(0f, 1f, 1f));
		assertEquals(new RgbColor(255, 255, 0), RgbColor.ofHsb(1f / 6f, 1f, 1f));
		assertEquals(RgbColor.GREEN, RgbColor.ofHsb(1f / 3f, 1f, 1f));
		assertEquals(RgbColor.CYAN, RgbColor.ofHsb(0.5f, 1f, 1f));
		assertEquals(RgbColor.BLUE, RgbColor.ofHsb(2f / 3f, 1f, 1f));
		assertEquals(new RgbColor(255, 0, 255), RgbColor.ofHsb(5f / 6f, 1f, 1f));
	}

	@Test
	@DisplayName("HSB: saturazione 0 da' un grigio, luminosita' 0 da' il nero")
	void hsbGrayAndBlack() {
		assertEquals(new RgbColor(128, 128, 128), RgbColor.ofHsb(0.3f, 0f, 0.5f));
		assertEquals(RgbColor.BLACK, RgbColor.ofHsb(0.3f, 1f, 0f));
		assertEquals(RgbColor.WHITE, RgbColor.ofHsb(0.3f, 0f, 1f));
	}

	@Test
	@DisplayName("HSB: la tinta e' ciclica (1 e -1 come 0, 1.5 come 0.5), anche per valori minuscoli che l'arrotondamento porta a 1")
	void hsbIsCyclic() {
		assertEquals(RgbColor.RED, RgbColor.ofHsb(1f, 1f, 1f));
		assertEquals(RgbColor.RED, RgbColor.ofHsb(-1f, 1f, 1f));
		assertEquals(RgbColor.CYAN, RgbColor.ofHsb(1.5f, 1f, 1f));
		// -1e-10f - floor(-1e-10f) vale 1.0f per arrotondamento: senza un trattamento esplicito la tinta 6 darebbe il nero
		assertEquals(RgbColor.RED, RgbColor.ofHsb(-1e-10f, 1f, 1f));
		assertEquals(RgbColor.RED, RgbColor.ofHsb(-1e-30f, 1f, 1f));
	}
}
