# Graphics Subsystem
The graphics subsystem of `SMFN-library` provides a decoupled visualization framework designed to bridge abstract mathematical models with graphical rendering. The design enforces a strict separation of concerns: mathematical models hold no knowledge of pixels or UI components, and rendering engines hold no knowledge of math.

![smfn-plot-example](smfn-plot-example.png)

## Architecture & Decoupling Philosophy
The graphics layer operates across three decoupled tiers:

$$\text{Mathematical Model } f(D) = C \quad\xrightarrow[\text{domainAdapter / colorMapper}]{\text{Viewport}}\quad \text{Plotter Orchestrator} \quad\xrightarrow[\text{Renderer}]{}\quad \text{Concrete Driver}$$

1. **Mathematical Model ($f: D \to C$):** Pure algebraic structures and functions, completely agnostic of pixels, framebuffers, or rendering tech.
2. **Plotter Orchestrator (`graphics.plotting`):** Middle tier that samples mathematical models across coordinate bounds defined by a `Viewport`, employing domain adapters and color mappers to translate mathematical values into drawing commands.
3. **Concrete Driver (`graphics.drivers`):** Platform-specific rendering implementation (e.g., AWT/Swing) that executes low-level drawing primitives on screen or image buffers.

### Package Structure
```text
net.gommagomma.smfn.graphics
  +-- core        # Rendering abstractions, primitive contracts, and viewport transformations
  +-- drivers.swing # Concrete desktop AWT/Swing driver implementations
  \-- plotting    # Mathematical plotting orchestrators and coordinate adapters
```

* **`graphics.core`**: Hardware-agnostic interfaces and coordinate space transformations.
* **`graphics.drivers.swing`**: Desktop AWT/Swing driver implementations. Interchangeable with alternative drivers (e.g., SVG, JavaFX) without affecting mathematical logic.
* **`graphics.plotting`**: Functional orchestrators bridging generic algebraic elements to drawing primitives.


## Core Infrastructure & Rendering Primitives (`graphics.core`)

### Renderer Contracts and Primitives
The `Renderer` interface defines the low-level API contract required by all plotting orchestrators. It encapsulates frame lifecycle management, state selection, and fundamental drawing primitives across two coordinate regimes:

* **Frame Lifecycle & State Operations:**
  * `startDrawing()`: Initializes a rendering frame.
  * `endDrawingAndFlush()`: Finalizes the frame and flushes drawing commands to the target buffer/screen.
  * `clear(Color color)`: Clears the active rendering buffer.
  * `setColor(Color color)`: Sets the active drawing color.

* **Primitives (Viewport-transformed):**
  * `drawPoint(int x, int y)`: Draws a single pixel.
  * `drawLine(int x1, int y1, int x2, int y2)`: Draws a 1D line segment.
  * `drawText(String text, int x, int y)`: Renders textual labels transformed via viewport coordinates.
  * `fillPolygon(int[] xs, int[] ys)`: Fills a closed polygon with the current color (fewer than 3 vertices draws nothing; vertex orientation is irrelevant; out-of-surface portions are clipped).
  * `fillRect(int x, int y, int width, int height)`: Fills a rectangle. A rectangle is a special case of a polygon, so the interface provides a default implementation on top of `fillPolygon`; a driver may override it with a faster native operation.

* **HUD Overlay Primitives (Viewport-independent):**
  * `drawOverlayText(String text, int x, int y, Color color)`: Renders screen-fixed Heads-Up Display (HUD) overlays directly in pixel coordinates $[0, w] \times [0, h]$, bypassing viewport transforms (e.g., displaying viewport bounds, mouse inspection coordinates, or solver runtime metrics).

### One Renderer, Whatever the Dimension
A `Renderer` is a drawing surface, and the surface is always a plane of pixels: points, lines, text and fills are traced the same way whether the plotted object is a 1D curve, a 2D scalar field or (in future) a projected 3D surface. For this reason there are no per-dimension renderer variants. Reducing extra coordinates to the plane (projection, camera) is the job of the layer that uses the renderer, alongside `Viewport`, which hands the renderer pixel coordinates only. Every driver implements all primitives.

* **`drivers.swing` (`SwingRenderer`)**: The desktop implementation. All primitives draw on an offscreen pixel buffer that is blitted to the screen once per frame; `drawPoint` writes the pixel array directly, since it is the primitive used millions of times per frame by field and fractal plots.

### Coordinate Transformation (`Viewport`)
The `Viewport` encapsulates bidirectional transformations between continuous mathematical space $[x_{\min}, x_{\max}] \times [y_{\min}, y_{\max}]$ and discrete screen pixel space $[0, w] \times [0, h]$:

* `convertMathXToPixelX(double mathX)`, `convertMathYToPixelY(double mathY)`
* `convertPixelXToMathX(int pixelX)`, `convertPixelYToMathY(int pixelY)`

### Interactive Control (`ViewportController`)
`ViewportController` manages interactive input (panning, zooming) and notifies registered `ViewportUpdateHandler` listeners upon bound modifications, keeping input event handling isolated from mathematical models.


## Plotting Layer (`graphics.plotting`)
The plotting package coordinates sampling and mapping between algebraic objects and rendering primitives.

### 1D Function Plotting (`FunctionPlotter1D`)
`FunctionPlotter1D` samples a continuous mapping $f(x)$ across the viewport's horizontal domain and renders the resulting curve.

```java
public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
void plotFunction(
        Renderer renderer,                     /* Renderer instance */
        Viewport viewport,                     /* Viewport defining coordinate mapping */
        Mapping<D, C> function,                /* Mathematical function f(D) = C */
        Function<Double, D> domainAdapter,     /* Domain adapter from double to D (e.g., Double -> Real) */
        Function<C, Double> codomainAdapter)   /* Codomain adapter from C to double (e.g., Real -> Double) */
```

### 1D Usage Example: Function Visualization
This example shows how to plot a continuous single-variable function ($P(x) = x^2 - 2$). The polynomial is evaluated using the library's Real type, while the driver handles frame rendering and AWT panel integration. The domain and codomain adapters act as the only bridge between the mathematical model and the rendering pipeline, keeping math logic completely separate from pixel coordinates.

```java
RealField R = RealField.INSTANCE;

// Mathematical model: P(x) = x^2 - 2, wrapped as a Mapping<Real,Real>
Polynomial<Real> p = PolynomialElementFactory.of(R, R.of(-2), R.of(0), R.of(1));
HornerEvaluator<Real, RealField, PolynomialFunction<Real, RealField>> horner = new HornerEvaluator<>(R);
PolynomialFunction<Real, RealField> f = new PolynomialFunction<>(p, R, horner);

// Concrete driver
int width = 600, height = 400;
SwingRenderer renderer = new SwingRenderer(width, height);
SwingWindow.show(renderer, "SMFN: P(x) = x^2 - 2");

// Coordinate mapping
Viewport viewport = new Viewport(-3.0, 3.0, -3.0, 6.0, width, height);

// Adapters: bridge Real <-> double, the only place the mathematical model meets pixels
Function<Double, Real> domainAdapter = Real::new;
Function<Real, Double> codomainAdapter = Real::getValue;

// Plotter orchestrator
renderer.startDrawing();
renderer.clear(Color.WHITE);
CartesianAxisPlotter.plotAxes(renderer, viewport, Color.LIGHT_GRAY, true);
renderer.setColor(Color.BLUE);
FunctionPlotter1D.plotFunction(renderer, viewport, f, domainAdapter, codomainAdapter);
renderer.endDrawingAndFlush();
```


### 2D Function Plotting (`FunctionPlotter2D`)
`FunctionPlotter2D` evaluates a mathematical mapping $f: D \to C$ across pixel grids. It requires two functional adapters to maintain independence from concrete mathematical types:

1. **`domainAdapter` ($(x, y) \mapsto D$)**: Assembles raw Cartesian coordinates into a domain element $D$ (e.g., combining $x, y \in \mathbb{R}$ into $z \in \mathbb{C}$).
2. **`colorMapper` ($C \mapsto \text{Color}$)**: Converts the output algebraic value $C = f(D)$ into an AWT `Color`.

```java
public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
void plotFunction(
        Renderer renderer,                            /* Renderer instance */
        Viewport viewport,                            /* Viewport defining coordinate mapping */
        Mapping<D, C> function,                       /* Mathematical function f(D) = C */
        BiFunction<Double, Double, D> domainAdapter,  /* Domain adapter combining math X and Y into element D */
        ColorMapper<C> colorMapper)                   /* Color mapper transforming output C into AWT Color */
```

This pipeline enables the visualization of scalar fields, complex functions, fractals (Mandelbrot/Julia sets), and heatmaps.

### 2D Usage Example: Complex Dynamics Visualization
This example shows how to visualize a 2D scalar field by rendering a Julia set fractal ($z \mapsto z^2 + c$). The core function calculates escape-time iteration counts (`Natural`) without any graphical code. A domain adapter builds Complex numbers from Cartesian coordinates, while a `ColorMapper` converts iteration counts into colors to render the plot across the `Viewport`.

```java
// Mathematical model: z -> z^2 + c, iterated -- a FixedPointProblem<Complex>,
// exposed here as Mapping<Complex,Natural> (escape iteration count)
int maxIterations = 100;
Complex c = new Complex(0.27334, 0.00742);
JuliaFunction juliaFunction = new JuliaFunction(c, maxIterations);

// Concrete driver
int width = 800, height = 600;
SwingRenderer renderer = new SwingRenderer(width, height);
SwingWindow.show(renderer, "SMFN Julia Set (c = " + c + ") Plot");

// Coordinate mapping
Viewport viewport = new Viewport(-1.5, 1.5, -1.5, 1.5, width, height);

// domainAdapter: combines (x,y) into a single Complex input
BiFunction<Double, Double, Complex> domainAdapter = Complex::new;

// colorMapper: escape iteration count -> color; points that never escape are black
ColorMapper<Natural> colorMapper = new ColorMapper<>() {
    @Override
    public Color map(Natural iterations) {
        double value = iterations.getValue();
        if (value == 0 || value >= maxIterations) return Color.BLACK;
        float hue = (float) Math.sqrt(value / maxIterations);
        return Color.getHSBColor(0.6f, 1.0f, hue);
    }
};

// Plotter orchestrator
renderer.startDrawing();
renderer.clear(Color.WHITE);
FunctionPlotter2D.plotFunction(renderer, viewport, juliaFunction, domainAdapter, colorMapper);
CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);
renderer.endDrawingAndFlush();
```

### Discrete Data & Geometric Rendering (`ScatterPlotter`)
`ScatterPlotter` renders discrete collections of mathematical `Point` objects. It is used for:
* Trajectories and state histories generated by ODE solvers or physical simulators.
* Discrete point clouds and data distributions.
* Geometric constructions and implicit shape intersections.

### Axis & Grid Overlays (`CartesianAxisPlotter`)
`CartesianAxisPlotter` renders mathematical coordinate axes, dynamic tick marks, and domain grid lines over an active plot. Unlike screen-space HUD overlays, these visual structures are bound to the `Viewport` transform and dynamically rescale during pan and zoom operations.

### 3D Surface Plotting (`SurfacePlotter`)
`SurfacePlotter` draws a surface $z = f(x, y)$. It follows the same principle as every other plotter: the renderer stays a flat pixel surface, and the extra coordinate is reduced to the plane by the layer that uses it, here the projection in `Viewport3D`.

* **`Viewport3D`**: the three-dimensional counterpart of `Viewport`. It maps a box $[x_{\min}, x_{\max}] \times [y_{\min}, y_{\max}] \times [z_{\min}, z_{\max}]$ to pixels with an orthographic projection. Each axis is normalized on its own, so a low and wide surface stays readable. `azimuth` (degrees, counter-clockwise seen from above; 0 looks toward $+y$ with $x$ to the right) and `elevation` (degrees in $[-90, 90]$; 0 horizontal, 90 from above) choose the point of view. The box is always scaled to fit the surface, whatever the point of view.
* **Geometry and color are independent channels.** The output $C = f(D)$ gives the height through `heightAdapter`, while `colorMapper` receives the whole $C$: a `Complex` result can give the height with its real part and the hue with its argument.

```java
public static <D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
void plotSurface(
        Renderer renderer,                            /* Renderer instance */
        Viewport3D viewport,                          /* Box and point of view */
        Mapping<D, C> function,                       /* Mathematical function f(D) = C */
        BiFunction<Double, Double, D> domainAdapter,  /* Combines math X and Y into element D */
        Function<C, Double> heightAdapter,            /* Extracts the height z from C */
        ColorMapper<C> colorMapper,                   /* Color of each face, from the value at its center */
        int cellsX, int cellsY)                       /* Grid resolution */
```

`plotWireframe(...)` takes a single `RgbColor` instead of the color mapper and draws the grid as line segments. The wireframe is see-through (no face hides another one), so it is an alternative to `plotSurface`, not an overlay for it: drawn over the faces it would show the hidden lines.

Faces are drawn farthest first (painter's algorithm). For a single-valued surface this is exact and needs no sorting: walking the grid rows and columns from the far side never draws a face before one that hides it. A sample that is not finite (NaN or infinity, e.g. near a pole) has no position on screen, so the faces and segments touching it are skipped. Values outside $[z_{\min}, z_{\max}]$ are still drawn and may leave the surface, where the renderer clips them. Sampling is sequential: a surface grid has a few thousand samples, unlike a per-pixel field.

`SurfaceAxisPlotter.plotAxes(renderer, viewport, color, showLabels)` draws the three edges of the box that meet at the corner nearest to the viewer (at `minZ`), with the axis names and range ends as labels. Call it **after** the surface: it lies on the near boundary of the box, so no part of the surface can hide it, whereas drawn first it would be almost entirely covered. The axes follow the point of view, moving to another edge of the box as it rotates.

See `demo/functions/SurfacePlot` for a complete example ($z = \sin r / r$).
