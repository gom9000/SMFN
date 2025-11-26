package net.gommagomma.smfn.graphics.core;


import java.awt.Color;


/**
 * Interfaccia per mappare un valore generico E in un colore AWT.
 * @param <E> Il tipo di valore in input (es. Real, Integer, Complex).
 */
public interface ColorMapper<E>
{
    /**
     * Mappa il valore di input nel colore corrispondente.
     * @param value Il valore matematico risultante.
     * @return Il colore AWT
     */
    Color map(E value);
}
