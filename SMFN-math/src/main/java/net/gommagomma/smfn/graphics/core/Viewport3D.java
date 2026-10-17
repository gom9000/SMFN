package net.gommagomma.smfn.graphics.core;


/**
 * Trasformazione tra coordinate matematiche (x, y, z) e coordinate pixel dello schermo piatto,
 * con proiezione ortografica: il parallelo tridimensionale di {@link Viewport}.
 * <p>
 * Convenzioni:
 * <ul>
 *   <li>L'asse z e' quello verticale (l'altezza di una superficie z = f(x, y)), x va verso destra
 *       e y in profondita'.</li>
 *   <li>Il riquadro [minX, maxX] x [minY, maxY] x [minZ, maxZ] viene normalizzato in un cubo,
 *       ogni asse indipendentemente dagli altri (come fanno i normali grafici di superfici): le
 *       proporzioni reali fra gli assi non sono conservate, cosi' una superficie bassa e larga
 *       resta leggibile. Il cubo viene poi ruotato e scalato per stare per intero nello schermo.</li>
 *   <li>Azimut (gradi): rotazione della scena attorno all'asse z, antioraria vista dall'alto.
 *       Con azimut 0 si guarda verso +y, con x a destra.</li>
 *   <li>Elevazione (gradi, da -90 a 90): 0 guarda orizzontalmente, 90 dall'alto, -90 dal basso.</li>
 * </ul>
 * Come per {@code Viewport}, l'oggetto e' immutabile e un punto fuori dal riquadro viene proiettato
 * comunque (puo' cadere fuori dalla superficie di disegno, dove il renderer lo ritaglia).
 */
public class Viewport3D
{
    /** Frazione dello schermo occupata dal cubo proiettato: lascia un margine attorno al disegno. */
    private static final double FIT = 0.9;

    public final double minX;
    public final double maxX;
    public final double minY;
    public final double maxY;
    public final double minZ;
    public final double maxZ;
    public final int pixelWidth;
    public final int pixelHeight;
    public final double azimuth;
    public final double elevation;

    private final double cosAz;
    private final double sinAz;
    private final double cosEl;
    private final double sinEl;
    private final double scale;


    public Viewport3D(double minX, double maxX, double minY, double maxY, double minZ, double maxZ,
                      int pixelWidth, int pixelHeight, double azimuth, double elevation)
    {
        requireRange("X", minX, maxX);
        requireRange("Y", minY, maxY);
        requireRange("Z", minZ, maxZ);
        if (pixelWidth <= 0 || pixelHeight <= 0) {
            throw new IllegalArgumentException(
                "Dimensioni pixel non valide: pixelWidth=" + pixelWidth + ", pixelHeight=" + pixelHeight
                + " (richiesti entrambi > 0).");
        }
        if (!Double.isFinite(azimuth)) {
            throw new IllegalArgumentException("Azimut non valido: " + azimuth + " (richiesto un valore finito).");
        }
        if (!Double.isFinite(elevation) || elevation < -90.0 || elevation > 90.0) {
            throw new IllegalArgumentException("Elevazione non valida: " + elevation + " (richiesto un valore in [-90, 90]).");
        }

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.azimuth = azimuth;
        this.elevation = elevation;

        this.cosAz = Math.cos(Math.toRadians(azimuth));
        this.sinAz = Math.sin(Math.toRadians(azimuth));
        this.cosEl = Math.cos(Math.toRadians(elevation));
        this.sinEl = Math.sin(Math.toRadians(elevation));

        // Semi-estensione orizzontale e verticale del cubo [-1,1]^3 ruotato (il massimo si
        // raggiunge in uno dei suoi vertici), per scegliere la scala che lo fa stare nello schermo.
        double horizontalSpan = Math.abs(cosAz) + Math.abs(sinAz);
        double verticalSpan = Math.abs(cosEl) + Math.abs(sinEl) * horizontalSpan;
        this.scale = FIT * Math.min((pixelWidth / 2.0) / horizontalSpan, (pixelHeight / 2.0) / verticalSpan);
    }

    private static void requireRange(String axis, double min, double max)
    {
        if (!Double.isFinite(min) || !Double.isFinite(max) || !(max > min)) {
            throw new IllegalArgumentException(
                "Range matematico degenere, invertito o non finito sull'asse " + axis
                + ": min=" + min + ", max=" + max + " (richiesto min < max, entrambi finiti).");
        }
    }


    // Normalizzazione di ogni coordinata nell'intervallo [-1, 1]
    private double normX(double x) { return 2.0 * (x - minX) / (maxX - minX) - 1.0; }
    private double normY(double y) { return 2.0 * (y - minY) / (maxY - minY) - 1.0; }
    private double normZ(double z) { return 2.0 * (z - minZ) / (maxZ - minZ) - 1.0; }


    // Metodi di conversione Math -> Pixel (double: l'arrotondamento a pixel e' a carico di chi disegna)
    public double projectToPixelX(double x, double y, double z)
    {
        double rotatedX = normX(x) * cosAz - normY(y) * sinAz;
        return pixelWidth / 2.0 + scale * rotatedX;
    }

    public double projectToPixelY(double x, double y, double z)
    {
        // Y dello schermo cresce verso il basso, "su" cresce verso l'alto: va invertita.
        double up = normZ(z) * cosEl + horizontalDepth(x, y) * sinEl;
        return pixelHeight / 2.0 - scale * up;
    }

    /**
     * Distanza dall'osservatore lungo la direzione di vista orizzontale, per il punto (x, y):
     * valori maggiori sono piu' lontani. Non dipende da z. Confrontando due punti permette di
     * sapere da che lato della griglia sta la parte lontana di una superficie z = f(x, y), per
     * disegnarla dalla piu' lontana alla piu' vicina.
     */
    public double horizontalDepth(double x, double y)
    {
        return normX(x) * sinAz + normY(y) * cosAz;
    }

    /**
     * Vero se, guardando la scena da questo punto di vista, il lato lontano del riquadro lungo x
     * e' quello di x massimo (falso: quello di x minimo). Con la vista parallela all'asse x (la
     * profondita' non dipende da x) il lato lontano non esiste e il valore e' indifferente.
     */
    public boolean isFarSideMaxX()
    {
        return horizontalDepth(maxX, minY) > horizontalDepth(minX, minY);
    }

    /** Come {@link #isFarSideMaxX()}, per l'asse y. */
    public boolean isFarSideMaxY()
    {
        return horizontalDepth(minX, maxY) > horizontalDepth(minX, minY);
    }
}
