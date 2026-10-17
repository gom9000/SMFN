package net.gommagomma.smfn.graphics.drivers.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;

/**
 * Implementazione di {@link Renderer} su un'immagine in memoria ({@link BufferedImage}): non ha
 * finestra e non richiede uno schermo, quindi funziona ovunque (server, test automatici, riga di
 * comando), e il risultato si puo' salvare su file con {@link #save(File)}.
 * <p>
 * Tutte le primitive disegnano sull'immagine. {@code drawPoint} scrive direttamente l'array di
 * pixel sottostante, bypassando {@link Graphics2D}: e' la primitiva usata per milioni di punti da
 * un plot di campo o da un frattale, e qui ogni chiamata evitata per pixel conta. Le altre
 * primitive (linee, testo, riempimenti) sono poche per frame e passano da un {@code Graphics2D}
 * dell'immagine.
 * <p>
 * Trasparenza: ogni primitiva rispetta il canale alfa del colore corrente, componendolo sopra il
 * contenuto gia' presente (alfa 255 sovrascrive, alfa 0 non cambia nulla).
 * <p>
 * Antialiasing: spento di default ({@link #setAntialiasing}). Acceso, linee e testo hanno i bordi
 * sfumati; i riempimenti no, perche' due poligoni adiacenti sfumati lasciano fra loro una
 * sottile fessura da cui traspare lo sfondo (le facce di una superficie sono proprio cosi').
 * <p>
 * Il driver {@code SwingRenderer} si appoggia a questa classe per tutte le primitive e aggiunge
 * solo la presentazione su schermo.
 */
public class ImageRenderer
implements Renderer
{
    private final BufferedImage image;
    private final int[] pixelData;
    private final Graphics2D graphics;
    private final int width;
    private final int height;

    private boolean antialiasing = false;
    private Color currentColor = Color.BLACK;
    private int currentArgb = 0xFF000000;


    /**
     * @param width larghezza in pixel (almeno 1)
     * @param height altezza in pixel (almeno 1)
     * @throws IllegalArgumentException se una dimensione e' minore di 1
     */
    public ImageRenderer(int width, int height)
    {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException(
                "Dimensioni non valide: width=" + width + ", height=" + height + " (richiesti entrambi >= 1).");
        }
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.pixelData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        this.graphics = image.createGraphics();
    }


    /**
     * Accende o spegne l'antialiasing di linee e testo (i riempimenti non ne sono toccati). Vale
     * per le primitive disegnate dopo la chiamata.
     */
    public void setAntialiasing(boolean enabled)
    {
        this.antialiasing = enabled;
    }

    public boolean isAntialiasing() { return antialiasing; }

    private void applyAntialiasing(boolean enabled)
    {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            enabled && antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            enabled && antialiasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    /** L'immagine su cui si disegna (non una copia): riflette tutto cio' che viene tracciato. */
    public BufferedImage getImage() { return image; }

    /**
     * Salva l'immagine in formato PNG.
     *
     * @throws IOException se la scrittura fallisce
     */
    public void save(File file) throws IOException
    {
        if (!ImageIO.write(image, "png", file)) {
            throw new IOException("Nessun codificatore PNG disponibile.");
        }
    }


    @Override public void startDrawing() {}
    @Override public void endDrawingAndFlush() {}

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }


    // Unico punto di conversione fra il colore neutro di graphics.core e java.awt.Color:
    // nessun altro punto di questo driver (e nessun punto di graphics.core/graphics.plotting)
    // dovrebbe mai costruire un java.awt.Color direttamente.
    private static Color toAwt(RgbColor color) {
        return new Color(color.r, color.g, color.b, color.a);
    }

    // Stessa conversione, nel formato ARGB impaccato dell'array di pixel (TYPE_INT_ARGB).
    private static int toArgb(RgbColor color) {
        return (color.a << 24) | (color.r << 16) | (color.g << 8) | color.b;
    }


    @Override
    public void clear(RgbColor color) {
        // Sostituisce il contenuto (non lo compone): e' il punto di partenza di un frame.
        Composite previous = graphics.getComposite();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(toAwt(color));
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(previous);
    }

    @Override
    public void setColor(RgbColor color) {
        this.currentColor = toAwt(color);
        this.currentArgb = toArgb(color);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        applyAntialiasing(true);
        graphics.setColor(currentColor);
        graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawPoint(int x, int y) {
        // Il clipping va fatto a mano: scrivendo l'array nessuno ritaglia le coordinate esterne.
        if (x < 0 || x >= width || y < 0 || y >= height) return;

        int alpha = currentArgb >>> 24;
        int index = y * width + x;
        if (alpha == 255) {
            pixelData[index] = currentArgb;
        } else if (alpha != 0) {
            pixelData[index] = composeOver(currentArgb, pixelData[index]);
        }
    }

    /** Composizione "sorgente sopra destinazione" di due colori ARGB non premoltiplicati. */
    private static int composeOver(int source, int destination) {
        int sourceAlpha = source >>> 24;
        int destinationAlpha = destination >>> 24;

        // Contributo della destinazione, attenuato da cio' che la sorgente non copre
        int destinationWeight = destinationAlpha * (255 - sourceAlpha) / 255;
        int outAlpha = sourceAlpha + destinationWeight;
        if (outAlpha == 0) return 0;

        int red = blendChannel((source >> 16) & 0xFF, (destination >> 16) & 0xFF, sourceAlpha, destinationWeight, outAlpha);
        int green = blendChannel((source >> 8) & 0xFF, (destination >> 8) & 0xFF, sourceAlpha, destinationWeight, outAlpha);
        int blue = blendChannel(source & 0xFF, destination & 0xFF, sourceAlpha, destinationWeight, outAlpha);
        return (outAlpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private static int blendChannel(int source, int destination, int sourceAlpha, int destinationWeight, int outAlpha) {
        return (source * sourceAlpha + destination * destinationWeight + outAlpha / 2) / outAlpha;
    }

    @Override
    public void drawText(String text, int x, int y) {
        applyAntialiasing(true);
        graphics.setColor(currentColor);
        graphics.drawString(text, x, y);
    }

    @Override
    public void drawOverlayText(String text, int x, int y, RgbColor color) {
        applyAntialiasing(true);
        Color originalColor = graphics.getColor();
        Font originalFont = graphics.getFont();

        graphics.setColor(toAwt(color));
        graphics.setFont(new Font("Monospaced", Font.PLAIN, 12));
        graphics.drawString(text, x, y);

        graphics.setFont(originalFont);
        graphics.setColor(originalColor);
    }

    @Override
    public void fillPolygon(int[] xs, int[] ys) {
        if (xs == null || ys == null || xs.length != ys.length) {
            throw new IllegalArgumentException("Vertici non validi: xs="
                + (xs == null ? "null" : xs.length + " elementi") + ", ys="
                + (ys == null ? "null" : ys.length + " elementi") + " (servono due array non nulli di pari lunghezza)");
        }
        if (xs.length < 3) return; // poligono degenere: niente da riempire
        applyAntialiasing(false);
        graphics.setColor(currentColor);
        graphics.fillPolygon(xs, ys, xs.length);
    }

    // Override dell'implementazione di default (che passa da fillPolygon): AWT ha il
    // riempimento di rettangoli nativo, piu' rapido di un poligono generico a quattro vertici.
    @Override
    public void fillRect(int x, int y, int width, int height) {
        applyAntialiasing(false);
        graphics.setColor(currentColor);
        graphics.fillRect(x, y, width, height);
    }
}
