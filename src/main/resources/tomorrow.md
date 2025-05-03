# Navegación tipo carrusel moderna para paneles/secciones en la web

A continuación se detalla cómo implementar una barra de navegación interactiva tipo carrusel para moverse entre diferentes paneles o secciones de tu app web, con flechas laterales, título dinámico y puntos indicadores. El diseño será moderno, limpio y adaptado a la estructura y estilos actuales de tu proyecto DiscordBot_app.

---

## 1. Concepto general

- La navegación sustituirá (o complementará) al menú lateral para moverse entre las secciones principales: Dashboard, Configuración, Módulos, Logs, Base de datos, Servidores, etc.
- Mostrará solo una sección/pantalla a la vez.
- Incluye:
  - Flechas izquierda/derecha para navegar secuencialmente.
  - Título de la sección actual, centrado.
  - Puntos debajo del título, uno por cada sección, con el activo resaltado.
  - Opcional: los puntos pueden ser clicables para saltar a una sección concreta.
- Totalmente responsive y visualmente integrado con tu tema actual (colores, bordes, sombras, etc.).

---

## 2. Estructura HTML

### 2.1. Barra de navegación (añadir en tu layout principal, por ejemplo, sobre `.content-area`):

```html
<div class="carousel-nav">
    <button class="carousel-arrow left" aria-label="Anterior">
        <i class="fas fa-chevron-left"></i>
    </button>
    <div class="carousel-title" id="carousel-title">Dashboard</div>
    <button class="carousel-arrow right" aria-label="Siguiente">
        <i class="fas fa-chevron-right"></i>
    </button>
</div>
<div class="carousel-indicators" id="carousel-indicators">
    <!-- Los puntos se generan dinámicamente -->
</div>
<div class="carousel-content" id="carousel-content">
    <!-- Aquí se cargan dinámicamente las pantallas/secciones -->
</div>
```

- Elimina o esconde el menú lateral si quieres que la navegación sea solo por carrusel.
- Si quieres mantener ambos, puedes dejar el menú lateral para desktop y el carrusel para móvil/tablet.

### 2.2. Paneles/secciones

- Cada sección (dashboard, config, etc.) debe ser un panel independiente, idealmente en archivos HTML separados (como ya tienes en `/static/html/`).
- El carrusel cargará el contenido de cada sección en el contenedor `#carousel-content`.

---

## 3. CSS (añadir a tu main.css o un archivo nuevo)

```css
.carousel-nav {
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-surface);
    border-radius: var(--border-radius-lg);
    box-shadow: var(--shadow-small);
    padding: 12px 24px;
    margin: 24px auto 8px auto;
    max-width: 600px;
    position: relative;
    z-index: 10;
}
.carousel-arrow {
    background: transparent;
    border: none;
    color: var(--color-primary);
    font-size: 22px;
    padding: 8px 12px;
    border-radius: 50%;
    transition: background 0.2s;
    cursor: pointer;
}
.carousel-arrow:disabled {
    color: var(--color-secondary);
    cursor: not-allowed;
}
.carousel-title {
    flex: 1;
    text-align: center;
    font-size: 20px;
    font-weight: 600;
    color: var(--color-text-primary);
    letter-spacing: 0.5px;
}
.carousel-indicators {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    margin-bottom: 18px;
}
.carousel-indicator {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: var(--color-border);
    transition: background 0.2s, transform 0.2s;
    cursor: pointer;
}
.carousel-indicator.active {
    background: var(--color-primary);
    transform: scale(1.2);
}
.carousel-content {
    min-height: 400px;
    background: var(--color-surface);
    border-radius: var(--border-radius-lg);
    box-shadow: var(--shadow-medium);
    padding: 24px;
    max-width: 900px;
    margin: 0 auto 32px auto;
    position: relative;
    overflow: hidden;
    transition: min-height 0.2s;
}
@media (max-width: 700px) {
    .carousel-nav, .carousel-content {
        max-width: 98vw;
        padding: 10px;
    }
    .carousel-title {
        font-size: 17px;
    }
}
```

- Puedes ajustar los colores y tamaños según tu paleta.
- El diseño es limpio, moderno y se adapta a tu tema.

---

## 4. JavaScript (añadir a main.js o un archivo nuevo, por ejemplo, carousel.js)

```js
// Definición de secciones
const sections = [
    { id: 'dashboard', title: 'Dashboard', file: '../html/dashboard.html' },
    { id: 'config', title: 'Configuración', file: '../html/config.html' },
    { id: 'modules', title: 'Módulos', file: '../html/modules.html' },
    { id: 'logs', title: 'Logs', file: '../html/logs.html' },
    { id: 'database', title: 'Base de datos', file: '../html/database.html' },
    { id: 'servers', title: 'Servidores', file: '../html/servers.html' }
];
let currentSection = 0;

function renderCarousel() {
    // Título
    document.getElementById('carousel-title').textContent = sections[currentSection].title;
    // Flechas
    document.querySelector('.carousel-arrow.left').disabled = currentSection === 0;
    document.querySelector('.carousel-arrow.right').disabled = currentSection === sections.length - 1;
    // Puntos
    const indicators = document.getElementById('carousel-indicators');
    indicators.innerHTML = '';
    sections.forEach((_, idx) => {
        const dot = document.createElement('div');
        dot.className = 'carousel-indicator' + (idx === currentSection ? ' active' : '');
        dot.onclick = () => goToSection(idx);
        indicators.appendChild(dot);
    });
    // Cargar contenido
    fetch(sections[currentSection].file)
        .then(res => res.text())
        .then(html => {
            document.getElementById('carousel-content').innerHTML = html;
            // Si tienes lógica JS específica por sección, puedes cargarla aquí
            // loadScreenScript(sections[currentSection].id);
        });
}
function goToSection(idx) {
    if (idx < 0 || idx >= sections.length) return;
    currentSection = idx;
    renderCarousel();
}
document.querySelector('.carousel-arrow.left').onclick = () => goToSection(currentSection - 1);
document.querySelector('.carousel-arrow.right').onclick = () => goToSection(currentSection + 1);
// Inicializar
renderCarousel();
```

- Este código gestiona la navegación, el título, los puntos y la carga dinámica de cada sección.
- Si tienes scripts específicos para cada sección, puedes cargarlos tras el fetch (como ya haces en tu main.js).
- Puedes sincronizar el estado del carrusel con la URL (hash) si quieres permitir enlaces directos a secciones.

---

## 5. Adaptaciones sobre tu web actual

- **HTML:** Añade la estructura de la barra de navegación y el contenedor de contenido en tu layout principal (por ejemplo, en `index.html` o la plantilla base).
- **CSS:** Añade los estilos propuestos a tu main.css o styles/dashboard.css según convenga.
- **JS:** Añade el código JS en un archivo nuevo (carousel.js) y enlázalo en tu HTML, o intégralo en main.js si prefieres.
- **Carga de secciones:** Puedes reutilizar tus archivos HTML de secciones actuales (`dashboard.html`, `config.html`, etc.).
- **Menú lateral:** Si quieres que el carrusel sea la única navegación, esconde el menú lateral. Si quieres ambos, puedes mostrar el menú lateral solo en desktop y el carrusel en móvil/tablet (usando media queries y/o JS para alternar).
- **Accesibilidad:** Los botones tienen `aria-label` y los puntos son clicables.
- **Responsive:** El diseño se adapta a cualquier pantalla.
- **Integración con scripts de sección:** Si cada sección tiene lógica JS propia, asegúrate de cargarla tras el fetch del HTML (como ya haces con `loadScreenScript`).

---

## 6. Mejoras opcionales

- **Animaciones de transición** entre secciones (deslizamiento, fade, etc.).
- **Soporte para swipe** en dispositivos táctiles (añadir listeners de touchstart/touchend).
- **Sincronización con la URL** (hash o query) para permitir enlaces directos a una sección concreta.
- **Mostrar/ocultar el menú lateral automáticamente** según el tamaño de pantalla.
- **Soporte para atajos de teclado** (izquierda/derecha para navegar).

---

## 7. Ejemplo visual

```
+---------------------------------------------------+
|   <  [Título de la sección]  >                    |
+---------------------------------------------------+
|                                                   |
|           [Contenido de la sección]               |
|                                                   |
+---------------------------------------------------+
|   ●   ○   ○   ○   ○   ○                           |
+---------------------------------------------------+
```

- El punto sólido indica la sección activa.
- Las flechas permiten navegar secuencialmente.
- El título cambia dinámicamente.

---

## 8. Resumen de pasos

1. Añade la estructura HTML de la barra de navegación y el contenedor de contenido.
2. Añade los estilos CSS para la barra, flechas, título, puntos y contenedor.
3. Añade el JS para gestionar la navegación, el título, los puntos y la carga dinámica de secciones.
4. Integra la lógica de scripts específicos de cada sección si es necesario.
5. Ajusta el menú lateral según tu preferencia (solo carrusel, ambos, o responsive).
6. Prueba en desktop y móvil para asegurar la experiencia de usuario.

---

Esta solución es moderna, limpia, accesible y se adapta perfectamente a la arquitectura y estilos de tu app actual. Si necesitas el código exacto para algún archivo concreto o integración con tus scripts actuales, indícalo y te lo proporciono.
