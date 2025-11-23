package net.gommagomma.smfn.graphics.core;


/**
 * Interfaccia che definisce la strategia per la mappatura di un valore numerico (risultato)
 * in un colore RGB intero standard (es. 0xRRGGBB).
 */
public interface ColorMapper
{
    /**
     * Converte un valore double (es. numero di iterazioni o magnitude) 
     * in un colore intero RGB.
     */
    int toRGB(double value);
}
