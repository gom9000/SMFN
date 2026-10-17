package net.gommagomma.smfn.graphics.core;


/**
 * Trasformazione tra coordinate matematiche (x, y, z) e coordinate pixel dello schermo piatto,
 * con proiezione ortografica: il parallelo tridimensionale di {@link Viewport2D}.
 * <p>
 * Convenzioni:
 * <ul>
 *   <li>L'asse z e' quello verticale (l'altezza di una superficie z = f(x, y)), x va verso destra
 *       e y in profondita'.</li>
 *   <li>Il riquadro [minX, maxX] x [minY, maxY] x [minZ, maxZ] viene normalizzato in un cubo,
 *       ogni asse indipendentemente dagli altri (come fanno i normali grafici di superfici): le
 *       proporzioni reali fra gli assi non sono conservate, cosi' una superficie bassa e larga
 *       resta leggibile. Il cubo viene poi ruotato e scalato per stare per intero nello schermo (zoom 1), o ingrandito
 *       attorno al suo centro con {@code zoom} e spostato sullo schermo con {@code offsetX} e
 *       {@code offsetY}.</li>
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
    public final double zoom;
    public final double offsetX;
    public final double offsetY;

    private final double cosAz;
    private final double sinAz;
    private final double cosEl;
    private final double sinEl;
    private final double scale;


    /** Una viewport senza ingrandimento (zoom 1): la scena sta per intero nello schermo. */
    public Viewport3D(double minX, double maxX, double minY, double maxY, double minZ, double maxZ,
                      int pixelWidth, int pixelHeight, double azimuth, double elevation)
    {
        this(minX, maxX, minY, maxY, minZ, maxZ, pixelWidth, pixelHeight, azimuth, elevation, 1.0);
    }

    /** Come sopra, con un ingrandimento (vedi il costruttore completo) e senza spostamento. */
    public Viewport3D(double minX, double maxX, double minY, double maxY, double minZ, double maxZ,
                      int pixelWidth, int pixelHeight, double azimuth, double elevation, double zoom)
    {
        this(minX, maxX, minY, maxY, minZ, maxZ, pixelWidth, pixelHeight, azimuth, elevation, zoom, 0.0, 0.0);
    }

    /**
     * @param zoom ingrandimento della scena rispetto all'adattamento allo schermo: 1 la fa stare per
     *        intero, 2 la ingrandisce al doppio (attorno al suo centro, quindi i bordi escono dallo schermo)
     * @param offsetX spostamento orizzontale della scena sullo schermo, in pixel (positivo verso destra)
     * @param offsetY spostamento verticale della scena sullo schermo, in pixel (positivo verso il basso)
     * @throws IllegalArgumentException se {@code zoom} non e' finito e maggiore di zero, o se uno
     *         spostamento non e' finito
     */
    public Viewport3D(double minX, double maxX, double minY, double maxY, double minZ, double maxZ,
                      int pixelWidth, int pixelHeight, double azimuth, double elevation,
                      double zoom, double offsetX, double offsetY)
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

        if (!Double.isFinite(zoom) || zoom <= 0.0) {
            throw new IllegalArgumentException("Zoom non valido: " + zoom + " (richiesto un valore finito maggiore di zero).");
        }

        if (!Double.isFinite(offsetX) || !Double.isFinite(offsetY)) {
            throw new IllegalArgumentException("Spostamento non valido: offsetX=" + offsetX + ", offsetY=" + offsetY + " (richiesti valori finiti).");
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
        this.zoom = zoom;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.cosAz = Math.cos(Math.toRadians(azimuth));
        this.sinAz = Math.sin(Math.toRadians(azimuth));
        this.cosEl = Math.cos(Math.toRadians(elevation));
        this.sinEl = Math.sin(Math.toRadians(elevation));

        // Semi-estensione orizzontale e verticale del cubo [-1,1]^3 ruotato (il massimo si
        // raggiunge in uno dei suoi vertici), per scegliere la scala che lo fa stare nello schermo.
        double horizontalSpan = Math.abs(cosAz) + Math.abs(sinAz);
        double verticalSpan = Math.abs(cosEl) + Math.abs(sinEl) * horizontalSpan;
        this.scale = zoom * FIT * Math.min((pixelWidth / 2.0) / horizontalSpan, (pixelHeight / 2.0) / verticalSpan);
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

    /**
     * Coordinata x nel cubo [-1, 1]^3 della scena: -1 su {@code minX}, +1 su {@code maxX}. Le
     * grandezze geometriche che dipendono da come la scena appare (come l'inclinazione di una
     * faccia, per illuminarla) vanno calcolate in queste coordinate, non in quelle matematiche,
     * perche' e' il cubo che viene ruotato e proiettato.
     */
    public double normalizedX(double x) { return normX(x); }

    /** Come {@link #normalizedX}, per y. */
    public double normalizedY(double y) { return normY(y); }

    /** Come {@link #normalizedX}, per z. */
    public double normalizedZ(double z) { return normZ(z); }

    /**
     * Una viewport con lo stesso riquadro e le stesse dimensioni, vista da un altro punto di vista.
     * Serve a ruotare la scena senza ricostruirla a mano.
     */
    public Viewport3D withView(double azimuth, double elevation)
    {
        return new Viewport3D(minX, maxX, minY, maxY, minZ, maxZ, pixelWidth, pixelHeight, azimuth, elevation, zoom, offsetX, offsetY);
    }

    /** Una viewport con lo stesso riquadro, dimensioni e punto di vista, ma un altro ingrandimento. */
    public Viewport3D withZoom(double zoom)
    {
        return new Viewport3D(minX, maxX, minY, maxY, minZ, maxZ, pixelWidth, pixelHeight, azimuth, elevation, zoom, offsetX, offsetY);
    }

    /** Una viewport con lo stesso riquadro, dimensioni, punto di vista e ingrandimento, ma un altro spostamento. */
    public Viewport3D withOffset(double offsetX, double offsetY)
    {
        return new Viewport3D(minX, maxX, minY, maxY, minZ, maxZ, pixelWidth, pixelHeight, azimuth, elevation, zoom, offsetX, offsetY);
    }

    /**
     * Direzione unitaria (nelle coordinate del cubo della scena) dal centro della scena verso un
     * osservatore con questi azimut ed elevazione. Un osservatore con azimut 0 sta dal lato di y
     * minimo e guarda verso +y; l'elevazione lo alza sopra la scena.
     */
    public static double[] directionToward(double azimuth, double elevation)
    {
        double az = Math.toRadians(azimuth);
        double el = Math.toRadians(elevation);
        return new double[] { -Math.sin(az) * Math.cos(el), -Math.cos(az) * Math.cos(el), Math.sin(el) };
    }

    /** Direzione unitaria dalla scena verso l'osservatore di questa viewport. */
    public double[] towardViewer()
    {
        return directionToward(azimuth, elevation);
    }


    // Metodi di conversione Math -> Pixel (double: l'arrotondamento a pixel e' a carico di chi disegna)
    public double projectToPixelX(double x, double y, double z)
    {
        double rotatedX = normX(x) * cosAz - normY(y) * sinAz;
        return pixelWidth / 2.0 + offsetX + scale * rotatedX;
    }

    public double projectToPixelY(double x, double y, double z)
    {
        // Y dello schermo cresce verso il basso, "su" cresce verso l'alto: va invertita.
        double up = normZ(z) * cosEl + horizontalDepth(x, y) * sinEl;
        return pixelHeight / 2.0 + offsetY - scale * up;
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
