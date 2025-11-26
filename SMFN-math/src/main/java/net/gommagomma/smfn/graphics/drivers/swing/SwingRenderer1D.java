package net.gommagomma.smfn.graphics.drivers.swing;


import net.gommagomma.smfn.graphics.core.Renderer1D;


/**
 * Implementazione concreta di Renderer1D usando Java Swing/AWT.
 */
public class SwingRenderer1D
extends SwingRendererBase
implements Renderer1D
{    
    private static final long serialVersionUID = 1L;


    public SwingRenderer1D(int width, int height)
    {
        super(width, height);
    }
}
