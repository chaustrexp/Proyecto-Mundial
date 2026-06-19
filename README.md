# Sistema de Administración Mundial 2026

Una aplicación de escritorio desarrollada en **Java Swing** para administrar una "polla" (quiniela) del Mundial 2026. Permite gestionar equipos, registrar apostadores, guardar predicciones y calcular puntajes según los resultados reales de los partidos.

## 🚀 Características

- **Tema Oscuro Integrado:** Interfaz gráfica moderna (basada en Nimbus) completamente estilizada con un tema oscuro para mayor comodidad visual.
- **Gestión de Apostadores:** Registro de participantes y visualización de puntos acumulados.
- **Gestión de Equipos y Partidos:** Creación de partidos por fases del mundial.
- **Sistema de Predicciones:** Interfaz para que los apostadores ingresen sus pronósticos.
- **Cálculo Automático de Puntos:** Actualización del ranking general al ingresar los resultados reales de los partidos.
- **Persistencia de Datos:** Conexión a base de datos MySQL de forma eficiente usando el patrón DAO.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java
- **Interfaz Gráfica:** Java Swing
- **Base de Datos:** MySQL / MariaDB (vía XAMPP)
- **Conector JDBC:** MySQL Connector/J

## 📂 Estructura del Proyecto

- `vistas/`: Contiene todos los paneles de la interfaz de usuario (`PanelEquipos`, `PanelPartidos`, etc.) y la ventana principal.
- `modelos/`: Clases que representan la lógica del negocio (`Apostador`, `Equipo`, `Partido`, `Prediccion`).
- `dao/`: Lógica de conexión a base de datos (`ConexionBD`) y ejecución de consultas SQL (`MundialDAO`).
- `utils/`: Utilidades generales como el gestor de temas oscuros (`ThemeManager`).
- `lib/`: Directorio destinado para las librerías externas (como el `.jar` de MySQL).

## ⚙️ Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/chaustrexp/Proyecto-Mundial.git
   cd Proyecto-Mundial/mundial_app
   ```

2. **Configurar la Base de Datos:**
   - Inicia tu servidor local (ej. XAMPP con Apache y MySQL).
   - Abre phpMyAdmin u otro gestor.
   - Importa el archivo `mundial.sql` que se encuentra dentro de la carpeta `dao/` para crear la estructura de tablas.

3. **Añadir el Conector MySQL:**
   - Descarga **MySQL Connector/J** (archivo `.jar`).
   - Copia el archivo `.jar` descargado dentro de la carpeta `lib/` de este proyecto.

4. **Compilar y Ejecutar:**
   Para compilar y correr la aplicación desde consola (estando dentro de la carpeta `mundial_app`):
   
   ```powershell
   # Compilar el proyecto
   javac -d bin -classpath "lib/*" $(Get-ChildItem -Recurse -Filter *.java | % {$_.FullName})
   
   # Ejecutar el programa
   java -cp "bin;lib/*" Main
   ```

## ✒️ Autor
Proyecto desarrollado por **chaustrexp**.
