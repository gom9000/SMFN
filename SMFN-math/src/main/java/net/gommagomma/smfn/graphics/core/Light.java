package net.gommagomma.smfn.graphics.core;

/**
 * Sorgente di luce per illuminare le facce di una superficie: una luce direzionale (come il Sole,
 * a distanza infinita) piu' una componente ambiente che schiarisce uniformemente anche le facce
 * non colpite.
 * <p>
 * La luce e' collocata con gli stessi due angoli di un punto di vista di {@link Viewport3D}: sta
 * dove starebbe un osservatore con quell'azimut e quell'elevazione. Una luce con gli stessi angoli
 * della viewport e' quindi "alle spalle di chi guarda", e ruotando la vista la luce resta ferma
 * sulla scena, come in un oggetto vero.
 * <p>
 * L'intensita' di una faccia e' {@code ambiente + (1 - ambiente) * max(0, n . L)}, con n la
 * normale della faccia e L la direzione verso la luce: da 1 (faccia rivolta alla luce) a
 * {@code ambiente} (faccia di taglio o in ombra). Applicarla a un colore ne scala le tre
 * componenti e lascia intatta la trasparenza.
 * <p>
 * Immutabile.
 */
public final class Light
{
    public final double azimuth;
    public final double elevation;
    public final double ambient;

    private final double[] toLight;


    /**
     * @param azimuth azimut in gradi della luce (stessa convenzione di {@code Viewport3D})
     * @param elevation elevazione in gradi della luce, da -90 a 90
     * @param ambient frazione di luminosita' che hanno anche le facce in ombra, da 0 a 1
     * @throws IllegalArgumentException se un valore non e' finito o e' fuori intervallo
     */
    public Light(double azimuth, double elevation, double ambient)
    {
        if (!Double.isFinite(azimuth)) {
            throw new IllegalArgumentException("Azimut non valido: " + azimuth + " (richiesto un valore finito).");
        }
        if (!Double.isFinite(elevation) || elevation < -90.0 || elevation > 90.0) {
            throw new IllegalArgumentException("Elevazione non valida: " + elevation + " (richiesto un valore in [-90, 90]).");
        }
        if (!Double.isFinite(ambient) || ambient < 0.0 || ambient > 1.0) {
            throw new IllegalArgumentException("Luce ambiente non valida: " + ambient + " (richiesto un valore in [0, 1]).");
        }
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.ambient = ambient;
        this.toLight = Viewport3D.directionToward(azimuth, elevation);
    }


    /**
     * Intensita' (da {@code ambient} a 1) di una faccia con la normale data, che non deve essere
     * unitaria ma non puo' essere nulla.
     *
     * @throws IllegalArgumentException se la normale e' nulla o non finita
     */
    public double intensity(double normalX, double normalY, double normalZ)
    {
        double length = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        if (!Double.isFinite(length) || length == 0.0) {
            throw new IllegalArgumentException(
                "Normale non valida: (" + normalX + ", " + normalY + ", " + normalZ + ").");
        }
        double cosine = (normalX * toLight[0] + normalY * toLight[1] + normalZ * toLight[2]) / length;
        return ambient + (1.0 - ambient) * Math.max(0.0, cosine);
    }

    /** Il colore dato, illuminato da una faccia con la normale data. */
    public RgbColor illuminate(RgbColor color, double normalX, double normalY, double normalZ)
    {
        double factor = intensity(normalX, normalY, normalZ);
        return new RgbColor(
            (int) Math.round(color.r * factor),
            (int) Math.round(color.g * factor),
            (int) Math.round(color.b * factor),
            color.a);
    }
}
