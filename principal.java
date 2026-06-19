package Mundial;

import java.util.Scanner;

public class mundial5 {
    static Scanner scanner = new Scanner(System.in);

    static String[] equipos = {
            "México", "Sudáfrica", "República de Corea", "Chequia",
            "Canadá", "Bosnia y Herzegovina", "Catar", "Suiza",
            "Brasil", "Marruecos", "Haití", "Escocia",
            "Estados Unidos", "Paraguay", "Australia", "Turquía",
            "Alemania", "Curazao", "Costa de Marfil", "Ecuador",
            "Países Bajos", "Japón", "Suecia", "Túnez",
            "Bélgica", "Egipto", "RI de Irán", "Nueva Zelanda",
            "España", "Cabo Verde", "Arabia Saudí", "Uruguay",
            "Francia", "Senegal", "Irak", "Noruega",
            "Argentina", "Argelia", "Austria", "Jordania",
            "Portugal", "RD Congo", "Uzbekistán", "Colombia",
            "Inglaterra", "Croacia", "Ghana", "Panamá"
    };

    // Datos de la Polla
    static int cantidadApostadores = 0;
    static String[] nombresApostadores;
    static int[] puntosApostadores;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("          SISTEMA DE POLLA - MUNDIAL 2026         ");
        System.out.println("==================================================\n");

        // Inicio del torneo y registro de participantes
        System.out.println("[CONFIGURACIÓN INICIAL]");
        System.out.print("¿Cuántas personas van a participar en la polla?: ");
        cantidadApostadores = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer del teclado

        nombresApostadores = new String[cantidadApostadores];
        puntosApostadores = new int[cantidadApostadores];

        for (int i = 0; i < cantidadApostadores; i++) {
            System.out.print("Registre el nombre del apostador #" + (i + 1) + ": ");
            nombresApostadores[i] = scanner.nextLine();
        }

        System.out.println("\n--> ¡Participantes registrados con éxito! Redirigiendo al menú de control...");

        String botonPresionado = "";
        do {
            System.out.println("\n==================================================");
            System.out.println("          VISTA DE BOTONERA INTERACTIVA           ");
            System.out.println("==================================================");
            System.out.println("  [A] al [L] -> Gestionar Grupo Específico");
            System.out.println("  [1]        -> Panel: Dieciseisavos de final");
            System.out.println("  [2]        -> Panel: Octavos de final");
            System.out.println("  [3]        -> Panel: Cuartos de final");
            System.out.println("  [4]        -> Panel: Semifinales");
            System.out.println("  [5]        -> Panel: FINAL del torneo");
            System.out.println("  [EXTRA]    -> Mostrar Tabla General de Posiciones");
            System.out.println("  [0] o [S]  -> Cerrar Sistema de Registro");
            System.out.println("==================================================");
            System.out.print("Elija qué botón de la interfaz operar: ");
            botonPresionado = scanner.next().toUpperCase();

            switch (botonPresionado) {
                case "A":
                case "B":
                case "C":
                case "D":
                case "E":
                case "F":
                case "G":
                case "H":
                case "I":
                case "J":
                case "K":
                case "L":
                    int numGrupo = botonPresionado.charAt(0) - 'A' + 1;
                    registrarDatosGrupo(numGrupo, botonPresionado.charAt(0));
                    break;
                case "1":
                    fasesEliminatorias("DIECISEISAVOS DE FINAL");
                    break;
                case "2":
                    fasesEliminatorias("OCTAVOS DE FINAL");
                    break;
                case "3":
                    fasesEliminatorias("CUARTOS DE FINAL");
                    break;
                case "4":
                    fasesEliminatorias("SEMIFINALES");
                    break;
                case "5":
                    fasesEliminatorias("FINAL DEL TORNEO");
                    break;
                case "EXTRA":
                    publicarResultadosPolla();
                    break;
                case "0":
                case "O":
                case "S":
                    System.out.println("\nSaliendo del sistema de forma segura...");
                    break;
                default:
                    System.out.println("\nAcción no válida. Por favor, presione un botón correcto.");
            }

        } while (!botonPresionado.equals("0") && !botonPresionado.equals("O") && !botonPresionado.equals("S"));

        System.out.println("Programa finalizado.");
        scanner.close();
    }

    // --- PROCESO COMPLETO DE REGISTRO ---
    public static void registrarDatosGrupo(int grupoSeleccionado, char letraGrupo) {
        int inicio = (grupoSeleccionado - 1) * 4;
        System.out.println("\n--------------------------------------------------");
        System.out.println(" MODULO DE REGISTRO: GRUPO " + letraGrupo);
        System.out.println("--------------------------------------------------");

        int[][] enfrentamientos = { { 0, 1 }, { 2, 3 }, { 0, 2 }, { 1, 3 }, { 0, 3 }, { 1, 2 } };

        int[][] predGoles1 = new int[cantidadApostadores][6];
        int[][] predGoles2 = new int[cantidadApostadores][6];

        // PASO 1: Se digita lo que cada jugador predijo
        System.out.println("\n--- PASO 1: REGISTRAR PREDICCIONES DE LOS JUGADORES ---");
        for (int p = 0; p < cantidadApostadores; p++) {
            System.out.println("\nDigitando plantilla de: " + nombresApostadores[p].toUpperCase());

            for (int i = 0; i < enfrentamientos.length; i++) {
                int eq1 = inicio + enfrentamientos[i][0];
                int eq2 = inicio + enfrentamientos[i][1];

                System.out.println("Partido " + (i + 1) + ": " + equipos[eq1] + " vs " + equipos[eq2]);
                System.out.print("   Goles pronosticados para " + equipos[eq1] + ": ");
                predGoles1[p][i] = scanner.nextInt();
                System.out.print("   Goles pronosticados para " + equipos[eq2] + ": ");
                predGoles2[p][i] = scanner.nextInt();
            }
        }

        // PASO 2: Se digitan los resultados oficiales tras jugarse los
        // partidos reales
        System.out.println("\n--- PASO 2: REGISTRAR RESULTADOS OFICIALES DE LA FIFA ---");
        int[] realesGoles1 = new int[6];
        int[] realesGoles2 = new int[6];

        for (int i = 0; i < enfrentamientos.length; i++) {
            int eq1 = inicio + enfrentamientos[i][0];
            int eq2 = inicio + enfrentamientos[i][1];

            System.out.println("Marcador REAL del Partido " + (i + 1) + ": " + equipos[eq1] + " vs " + equipos[eq2]);
            System.out.print("   Goles reales anotados por " + equipos[eq1] + ": ");
            realesGoles1[i] = scanner.nextInt();
            System.out.print("   Goles reales anotados por " + equipos[eq2] + ": ");
            realesGoles2[i] = scanner.nextInt();
        }

        // PASO 3: El sistema calcula todo internamente
        //
        System.out.println("\n--- PASO 3: AUDITORÍA Y ACTUALIZACIÓN DE PUNTOS ---");
        for (int p = 0; p < cantidadApostadores; p++) {
            int puntosGanadosEnEsteGrupo = 0;
            for (int i = 0; i < 6; i++) {
                int pg1 = predGoles1[p][i];
                int pg2 = predGoles2[p][i];
                int rg1 = realesGoles1[i];
                int rg2 = realesGoles2[i];

                // Acierto Exacto -> 5 puntos
                if (pg1 == rg1 && pg2 == rg2) {
                    puntosApostadores[p] += 5;
                    puntosGanadosEnEsteGrupo += 5;
                }
                // Acierto de Tendencia (Ganador/Empate) -> 3 puntos
                else {
                    boolean predijoGanaL = pg1 > pg2;
                    boolean realGanoL = rg1 > rg2;

                    boolean predijoGanaV = pg2 > pg1;
                    boolean realGanoV = rg2 > rg1;

                    boolean predijoEmpate = pg1 == pg2;
                    boolean realEmpate = rg1 == rg2;

                    if ((predijoGanaL && realGanoL) || (predijoGanaV && realGanoV) || (predijoEmpate && realEmpate)) {
                        puntosApostadores[p] += 3;
                        puntosGanadosEnEsteGrupo += 3;
                    }
                }
            }
            System.out.println(
                    "> " + nombresApostadores[p] + " sumó +" + puntosGanadosEnEsteGrupo + " pts en esta sección.");
        }
        System.out.println("\n[SISTEMA]: Datos guardados correctamente. Volviendo al panel principal.");
    }

    public static void fasesEliminatorias(String nombreFase) {
        System.out.println("\n==================================================");
        System.out.println("   REGISTRO EN PANEL FASE: " + nombreFase);
        System.out.println("==================================================");

        System.out.print("¿Cuántos partidos se disputarán en esta fase a registrar?: ");
        int numPartidos = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        if (numPartidos <= 0) {
            System.out.println("Operación cancelada. Regresando al menú...");
            return;
        }

        String[] equipo1 = new String[numPartidos];
        String[] equipo2 = new String[numPartidos];

        System.out.println("\n--- CONFIGURACIÓN DE PARTIDOS ---");
        for (int i = 0; i < numPartidos; i++) {
            System.out.println("Definiendo Partido " + (i + 1) + ":");
            System.out.print("   Nombre del Equipo 1: ");
            equipo1[i] = scanner.nextLine();
            System.out.print("   Nombre del Equipo 2: ");
            equipo2[i] = scanner.nextLine();
        }

        int[][] predGoles1 = new int[cantidadApostadores][numPartidos];
        int[][] predGoles2 = new int[cantidadApostadores][numPartidos];

        System.out.println("\n--- PASO 1: PREDICCIONES DE LOS JUGADORES ---");
        for (int p = 0; p < cantidadApostadores; p++) {
            System.out.println("\nDigitando plantilla de: " + nombresApostadores[p].toUpperCase());
            for (int i = 0; i < numPartidos; i++) {
                System.out.println("Partido: " + equipo1[i] + " vs " + equipo2[i]);
                System.out.print("   Goles pronosticados para " + equipo1[i] + ": ");
                predGoles1[p][i] = scanner.nextInt();
                System.out.print("   Goles pronosticados para " + equipo2[i] + ": ");
                predGoles2[p][i] = scanner.nextInt();
            }
        }

        System.out.println("\n--- PASO 2: RESULTADOS OFICIALES ---");
        int[] realesGoles1 = new int[numPartidos];
        int[] realesGoles2 = new int[numPartidos];

        for (int i = 0; i < numPartidos; i++) {
            System.out.println("Marcador REAL del Partido: " + equipo1[i] + " vs " + equipo2[i]);
            System.out.print("   Goles reales anotados por " + equipo1[i] + ": ");
            realesGoles1[i] = scanner.nextInt();
            System.out.print("   Goles reales anotados por " + equipo2[i] + ": ");
            realesGoles2[i] = scanner.nextInt();
        }

        System.out.println("\n--- PASO 3: ACTUALIZACIÓN DE PUNTOS ---");
        for (int p = 0; p < cantidadApostadores; p++) {
            int puntosGanados = 0;
            for (int i = 0; i < numPartidos; i++) {
                int pg1 = predGoles1[p][i];
                int pg2 = predGoles2[p][i];
                int rg1 = realesGoles1[i];
                int rg2 = realesGoles2[i];

                if (pg1 == rg1 && pg2 == rg2) {
                    puntosApostadores[p] += 5;
                    puntosGanados += 5;
                } else {
                    boolean predGana1 = pg1 > pg2;
                    boolean realGana1 = rg1 > rg2;
                    boolean predGana2 = pg2 > pg1;
                    boolean realGana2 = rg2 > rg1;
                    boolean predEmpate = pg1 == pg2;
                    boolean realEmpate = rg1 == rg2;

                    if ((predGana1 && realGana1) || (predGana2 && realGana2) || (predEmpate && realEmpate)) {
                        puntosApostadores[p] += 3;
                        puntosGanados += 3;
                    }
                }
            }
            System.out
                    .println("> " + nombresApostadores[p] + " sumó +" + puntosGanados + " pts en " + nombreFase + ".");
        }
        System.out.println("\n[SISTEMA]: Datos de la fase eliminatoria guardados correctamente.");
    }

    public static void publicarResultadosPolla() {
        System.out.println("\n==================================================");
        System.out.println("       TABLA DE POSICIONES OFICIAL DE LA POLLA     ");
        System.out.println("==================================================");
        System.out.printf("%-20s %s\n", "Participante", "Puntaje Total");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < cantidadApostadores; i++) {
            System.out.printf("%-20s %d PTS\n", nombresApostadores[i].toUpperCase(), puntosApostadores[i]);
        }
        System.out.println("==================================================");
    }
}