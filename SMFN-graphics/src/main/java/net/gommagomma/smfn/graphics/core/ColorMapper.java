package net.gommagomma.smfn.graphics.core;

/**
 * Interfaccia per mappare un valore generico E in un colore.
 * @param <E> Il tipo di valore in input (es. Real, Integer, Complex).
 */
public interface ColorMapper<E>
{
    /**
     * Mappa il valore di input nel colore corrispondente.
     * @param value Il valore da mappare.
     * @return Il colore, indipendente da qualunque toolkit grafico concreto.
     */
    RgbColor map(E value);
}
