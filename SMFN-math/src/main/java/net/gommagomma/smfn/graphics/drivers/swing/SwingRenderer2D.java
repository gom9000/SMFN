package net.gommagomma.smfn.graphics.drivers.swing;


import net.gommagomma.smfn.graphics.core.Renderer2D;


/**
 * Implementazione concreta di Renderer2D usando Java Swing/AWT.
 */
public class SwingRenderer2D
extends SwingRendererBase
implements Renderer2D
{    
    private static final long serialVersionUID = 1L;


    public SwingRenderer2D(int width, int height)
    {
        super(width, height);
    }


    @Override
    public void fillRect(int x, int y, int w, int h)
    {
        if (g == null) return;
        g.setColor(this.currentColor);
        g.fillRect(x, y, w, h);
    }
}