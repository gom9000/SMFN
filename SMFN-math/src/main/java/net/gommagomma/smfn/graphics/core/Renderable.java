package net.gommagomma.smfn.graphics.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

/**
 * Rappresenta un oggetto che sa come restituire un colore (RGB) 
 * per una data coordinata matematica I.
 */
public interface Renderable<I extends AlgebraicElement<I>>
{
    /**
     * Calcola il colore RGB per la coordinata matematica specificata.
     * Restituisce null se l'oggetto non disegna nulla in quella posizione,
     * permettendo al layer sottostante di essere visualizzato.
     * 
     * @param coordinate La coordinata matematica (es. Complex)
     * @return Il colore RGB come Integer, o null se trasparente/non disegnato da questo layer.
     */
    Integer renderPixel(I coordinate);
}
