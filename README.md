<div align="center">
  <h1>🏆 Sistema de Administración: Mundial 2026</h1>
  <p>Una aplicación de escritorio profesional para la gestión administrativa de predicciones (quinielas/pollas) de la Copa Mundial de la FIFA 2026.</p>

  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
  ![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
  ![MVC](https://img.shields.io/badge/Architecture-MVC-success?style=for-the-badge)
</div>

<br>

## 🚀 Descripción General
Desarrollada en **Java Swing**, esta aplicación centraliza el control de un juego de predicciones deportivas. Implementa el patrón arquitectónico **MVC (Modelo-Vista-Controlador)** y se conecta a una base de datos MySQL local mediante DAO (Data Access Object) con protección nativa contra inyecciones SQL. 

Cuenta con un control de acceso por roles (Administrador y Usuario), asegurando que las operaciones críticas de negocio estén protegidas.

---

## ✨ Características Principales

### 🔒 Sistema de Roles y Seguridad
- **Login Autenticado:** Diferenciación en tiempo real entre Administradores y Usuarios estándar.
- **Validaciones Físicas Estrictas:** Controles en la interfaz (DocumentFilters) y lógica de controladores bloqueando caracteres no válidos o absurdos (ej: no se permiten números negativos ni goles imposibles, nombres sin números).
- **Protección SQL:** Uso de `PreparedStatement` y `try-with-resources` para máxima seguridad y eficiencia de memoria.

### 👥 Gestión de Apostadores
- Módulo exclusivo de registro y visualización en tabla viva.
- Barra de búsqueda interactiva (solo letras) y tabla de posiciones general (Ranking) en tiempo real.

### ⚽ Simulador de Partidos
- **Creación Inteligente:** Programe partidos evitando cruces inválidos (mismo equipo) o conflictos de Fase de Grupos.
- **Simulación en Vivo:** Interfaz de marcador visual simulado con dropdowns de estadios oficiales del Mundial 2026.

### 🔮 Predicciones y 🏆 Resultados
- **Automatización de Puntos:** Cuando el Administrador carga el marcador final oficial, el sistema automáticamente:
  - **Otorga 5 puntos:** Si el apostador acertó el marcador exacto.
  - **Otorga 3 puntos:** Si el apostador acertó al equipo ganador o el empate.
- Restricción de permisos donde el administrador audita resultados, pero solo el usuario registra predicciones.

---

## 🛠️ Tecnologías

| Tecnología | Descripción |
| --- | --- |
| **Java 26+** | Lógica core y programación Orientada a Objetos. |
| **Java Swing / AWT** | Interfaz Gráfica (GUI) estilizada manualmente (modo oscuro, flat design). |
| **MySQL (XAMPP)** | Persistencia de datos relacionales. |
| **JDBC** | Driver nativo de comunicación a base de datos. |

---

## 📂 Arquitectura (MVC)

El proyecto está separado estructuradamente por responsabilidades:

```text
📁 mundial_app/
├── 📁 controladores/  # Lógica de negocio y validación profunda (PartidoController, etc.)
├── 📁 dao/            # Capa de Acceso a Datos y Conexión JDBC (ConexionBD, etc.)
├── 📁 modelos/        # Entidades del negocio (Apostador, Equipo, Partido...)
├── 📁 vistas/         # Interfaces Gráficas y validación de input de usuario
└── 📄 Main.java       # Punto de entrada de la aplicación
```

---

## ⚙️ Instalación y Configuración Local

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/chaustrexp/Proyecto-Mundial.git
   cd Proyecto-Mundial/mundial_app
   ```

2. **Configurar la Base de Datos:**
   - Inicia tu servidor local (ej. XAMPP con Apache y MySQL).
   - Abre phpMyAdmin (o tu gestor SQL preferido).
   - Crea una base de datos llamada `mundial` e importa tu script SQL para crear las tablas necesarias (`usuarios`, `equipos`, `partidos`, `apostadores`, `predicciones`, `resultados`).

3. **Añadir el Conector MySQL:**
   - Asegúrate de tener el **MySQL Connector/J** configurado en el *classpath* o IDE, o dentro de la carpeta `/lib` del proyecto si compilas por terminal.

4. **Ejecutar:**
   Puedes abrir la carpeta `mundial_app` en **VS Code**, **IntelliJ IDEA** o **Eclipse**. Simplemente corre la clase principal: `Main.java`.

---

<div align="center">
  <i>Desarrollado con pasión para el Mundial 2026 por <b>chaustrexp</b>.</i>
</div>
