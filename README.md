<div align="center">
  <h1>🏆 Sistema de Administración: Mundial 2026</h1>
  <p>Una aplicación de escritorio profesional para la gestión administrativa de predicciones (quinielas/pollas) de la Copa Mundial de la FIFA 2026.</p>

  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
  ![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
  ![MVC](https://img.shields.io/badge/Architecture-MVC-success?style=for-the-badge)
  ![Ikonli](https://img.shields.io/badge/Icons-Ikonli%2FFontAwesome-orange?style=for-the-badge)
</div>

<br>

## 🚀 Descripción General
Desarrollada en **Java Swing**, esta aplicación centraliza el control de un juego de predicciones deportivas. Implementa el patrón arquitectónico **MVC (Modelo-Vista-Controlador)** y se conecta a una base de datos MySQL local mediante DAO (Data Access Object) con protección nativa contra inyecciones SQL.

Cuenta con un control de acceso por roles (Administrador y Usuario), asegurando que las operaciones críticas de negocio estén protegidas.

---

## ✨ Características Principales

### 🔒 Sistema de Roles y Seguridad
- **Login Autenticado:** Diferenciación en tiempo real entre Administradores y Usuarios estándar.
- **Validaciones Físicas Estrictas:** Controles en la interfaz (`DocumentFilter` vía `ValidationUtils`) que bloquean en tiempo real caracteres inválidos en todos los formularios (no se permiten letras en campos numéricos, grupos fuera del rango A-L, contraseñas cortas, etc.).
- **Protección SQL:** Uso de `PreparedStatement` y `try-with-resources` para máxima seguridad y eficiencia de memoria.
- **Registro de cuenta:** Cualquier usuario puede crear su propia cuenta desde la pantalla de login.

### 👥 Gestión de Apostadores
- Módulo exclusivo de registro y visualización en tabla viva con ranking de posiciones.
- Barra de búsqueda interactiva (solo letras) en tiempo real.
- Tarjetas de estadísticas: total de apostadores, líder actual y suma de puntos en juego.

### ⚽ Simulador de Partidos
- **Creación Inteligente:** Programe partidos evitando cruces inválidos o conflictos de Fase de Grupos.
- **Simulación en Vivo:** Interfaz de marcador visual con minuto actual y validación de rango (1-120).
- **Banderas reales:** Las banderas de cada selección se cargan automáticamente desde [FlagCDN](https://flagcdn.com) de forma asíncrona.

### 🔮 Predicciones y 🏆 Resultados
- **Automatización de Puntos:** Cuando el Administrador carga el marcador final oficial, el sistema automáticamente:
  - **Otorga 5 puntos:** Si el apostador acertó el marcador exacto.
  - **Otorga 3 puntos:** Si el apostador acertó al equipo ganador o el empate.
- Restricción de permisos: el administrador audita resultados, pero solo el usuario registra predicciones.

---

## 🛠️ Tecnologías y Librerías

| Tecnología | Descripción |
| --- | --- |
| **Java 17+** | Lógica core y programación Orientada a Objetos. |
| **Java Swing / AWT** | Interfaz Gráfica (GUI) estilizada con modo oscuro y diseño premium. |
| **MySQL (XAMPP)** | Persistencia de datos relacionales. |
| **JDBC** | Driver nativo de comunicación a base de datos. |
| **Ikonli + FontAwesome 5** | Librería de íconos vectoriales multiplataforma para Swing. Reemplaza el uso de emojis de texto que no son compatibles en Windows. |
| **FlagCDN** | API de banderas de países cargadas dinámicamente como imágenes de alta calidad. |

---

## 📂 Arquitectura (MVC)

```text
📁 mundial_app/
├── 📁 controladores/   # Lógica de negocio (PartidoController, PrediccionController, etc.)
├── 📁 dao/             # Capa de Acceso a Datos y Conexión JDBC
├── 📁 modelos/         # Entidades del negocio (Apostador, Equipo, Partido...)
├── 📁 utils/           # Utilidades transversales:
│   ├── FlagManager.java       # Carga asíncrona de banderas desde FlagCDN
│   ├── SesionUsuario.java     # Control de sesión activa
│   └── ValidationUtils.java   # Filtros de validación reutilizables
└── 📁 vistas/          # Interfaces gráficas (PanelPartidos, PanelEquipos, etc.)
```

---

## ⚙️ Instalación y Configuración Local

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/chaustrexp/Proyecto-Mundial.git
   cd Proyecto-Mundial
   ```

2. **Configurar la Base de Datos:**
   - Inicia tu servidor local (XAMPP con MySQL).
   - Abre phpMyAdmin y crea una base de datos llamada `mundial`.
   - Importa el script SQL ubicado en `base de datos/mundial.sql`.

3. **Dependencias (carpeta `/lib`):**
   El proyecto ya incluye todas las librerías necesarias en la carpeta `lib/`:
   - `mysql-connector-j.jar` — Conector JDBC para MySQL.
   - `ikonli-core.jar`, `ikonli-swing.jar`, `ikonli-fontawesome5-pack.jar` — Librería de íconos.

4. **Ejecutar:**
   - **Opción rápida (Windows):** Doble clic en `ejecutar_proyecto.bat`.
   - **Manual:** Compila con `javac` incluyendo `lib/*` en el classpath y ejecuta la clase `principal`.

---

## 🔍 Validaciones por Formulario

| Pantalla | Campo | Regla |
|---|---|---|
| Login / Registro | Usuario | Solo letras, números y `_` (máx. 30 caracteres) |
| Registro | Alias / Nombre | Solo letras y espacios (máx. 30 caracteres) |
| Registro | Contraseña | Mínimo 4 caracteres |
| Equipos | Nombre / Confederación | Solo letras y espacios (máx. 40 caracteres) |
| Equipos | Grupo | Una letra entre A y L (se autoconvierte a mayúscula) |
| Partidos | Goles | Solo números, máx. 2 dígitos |
| Partidos | Minuto | Solo números, rango válido: 1–120 |
| Resultados / Predicciones | Goles | Solo números, máx. 2 dígitos |

---

<div align="center">
  <i>Desarrollado con pasión para el Mundial 2026 por <b>chaustrexp</b>.</i>
</div>
