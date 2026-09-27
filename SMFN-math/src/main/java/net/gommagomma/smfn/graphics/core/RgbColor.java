package net.gommagomma.smfn.graphics.core;

/**
 * Colore RGBA indipendente da toolkit grafico concreto.
 */
public final class RgbColor
{
	public final int r;
	public final int g;
	public final int b;
	public final int a;

	public RgbColor(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public RgbColor(int r, int g, int b, int a) {
		this.r = clamp(r);
		this.g = clamp(g);
		this.b = clamp(b);
		this.a = clamp(a);
	}

	private static int clamp(int channel) {
		if (channel < 0 || channel > 255) {
			throw new IllegalArgumentException("Componente di colore fuori range [0,255]: " + channel);
		}
		return channel;
	}

	// Costanti colore
	public static final RgbColor BLACK     = new RgbColor(0, 0, 0);
	public static final RgbColor WHITE     = new RgbColor(255, 255, 255);
	public static final RgbColor RED       = new RgbColor(255, 0, 0);
	public static final RgbColor GREEN     = new RgbColor(0, 255, 0);
	public static final RgbColor BLUE      = new RgbColor(0, 0, 255);
	public static final RgbColor CYAN      = new RgbColor(0, 255, 255);
	public static final RgbColor DARK_GRAY = new RgbColor(64, 64, 64);

	/**
	 * Costruisce un colore a partire da hue/saturation/brightness.
	 *
	 * @param hue tonalita' in [0,1] (ciclica: 0 e 1 sono lo stesso rosso)
	 * @param saturation saturazione in [0,1]
	 * @param brightness luminosita' in [0,1]
	 */
	public static RgbColor ofHsb(float hue, float saturation, float brightness)
	{
		int rr = 0, gg = 0, bb = 0;
		if (saturation == 0) {
			rr = gg = bb = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - saturation * (1.0f - f));
			switch ((int) h) {
				case 0: rr = (int) (brightness * 255.0f + 0.5f); gg = (int) (t * 255.0f + 0.5f); bb = (int) (p * 255.0f + 0.5f); break;
				case 1: rr = (int) (q * 255.0f + 0.5f); gg = (int) (brightness * 255.0f + 0.5f); bb = (int) (p * 255.0f + 0.5f); break;
				case 2: rr = (int) (p * 255.0f + 0.5f); gg = (int) (brightness * 255.0f + 0.5f); bb = (int) (t * 255.0f + 0.5f); break;
				case 3: rr = (int) (p * 255.0f + 0.5f); gg = (int) (q * 255.0f + 0.5f); bb = (int) (brightness * 255.0f + 0.5f); break;
				case 4: rr = (int) (t * 255.0f + 0.5f); gg = (int) (p * 255.0f + 0.5f); bb = (int) (brightness * 255.0f + 0.5f); break;
				case 5: rr = (int) (brightness * 255.0f + 0.5f); gg = (int) (p * 255.0f + 0.5f); bb = (int) (q * 255.0f + 0.5f); break;
				default: break;
			}
		}
		return new RgbColor(rr, gg, bb);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RgbColor)) return false;
		RgbColor other = (RgbColor) o;
		return r == other.r && g == other.g && b == other.b && a == other.a;
	}

	@Override
	public int hashCode() {
		return ((r * 31 + g) * 31 + b) * 31 + a;
	}

	@Override
	public String toString() {
		return "RgbColor(" + r + ", " + g + ", " + b + ", " + a + ")";
	}
}
