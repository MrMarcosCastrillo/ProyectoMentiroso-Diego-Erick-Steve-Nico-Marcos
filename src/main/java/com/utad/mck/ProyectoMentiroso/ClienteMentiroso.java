package com.utad.mck.ProyectoMentiroso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClienteMentiroso {

	static Scanner sc = new Scanner(System.in);

	// Dirección del servidor, localhost porque el servidor corre en el mismo
	// ordenador
	static String servidor = "http://localhost:8080";

	static boolean enPartida = false;
    static String idJuegoActual = "";
    static String nombreJugador = "";
	
	public static void main(String[] args) {

		System.out.println("-- CLIENTE JUEGO DEL MENTIROSO --");

		while (true) {

            if (!enPartida) {
                // MENÚ PRINCIPAL
                System.out.println("\n--- MENÚ PRINCIPAL ---");
                System.out.println("1. Crear partida");
                System.out.println("2. Unirse a partida");
                System.out.println("3. Listar partidas");
                System.out.println("4. Salir");

                int opcion = sc.nextInt(); //Hay que cambiarlo por un sc.nextLine en un trycatch con while para que no de errores
                sc.nextLine();

                switch (opcion) {
                    case 1:
                        crearPartida();
                        break;
                    case 2:
                        unirsePartida();
                        break;
                    case 3:
                        lobby();
                        break;
                    case 4:
                        System.out.println("Saliendo...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }

            } else {
                menuDentroPartida();
            }
        }
	}
	
	public static void menuDentroPartida() {

	    System.out.println("Esperando...");

	    while (!esMiTurno()) {
	        // Revisar si la partida ya terminó
	        if (revisarFinPartida()) {
	            return; // Volvemos al menú principal
	        }
	        dormir(2000); // 2 segundos
	    }

	    mostrarUltimaJugada();

	    boolean seguir = true;
	    while (seguir) {
	        System.out.println("\n--- TU TURNO ---");
	        System.out.println("1. Realizar una jugada");
	        System.out.println("2. Levantar la jugada anterior");
	        System.out.println("3. Abandonar partida");

	        int opcion = sc.nextInt();
	        sc.nextLine();

	        switch (opcion) {
	            case 1:
	                jugar();
	                seguir = false;
	                break;
	            case 2:
	                levantar();
	                seguir = false;
	                break;
	            case 3:
	                salirDePartida();
	                seguir = false;
	                break;
	            default:
	                System.out.println("Opción inválida");
	        }

	        // Revisar fin de partida después de la acción
	        if (revisarFinPartida()) {
	            return; // Volvemos al menú principal
	        }
	    }
	}

	public static void dormir(int ms) {
	    try {
	        Thread.sleep(ms);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}
	
	// Metodos para aprovechar los endpoint

	public static void crearPartida() {
	    System.out.print("Nombre del jugador: ");
	    nombreJugador = sc.nextLine();

	    String url = servidor + "/juego/empezar?nombre=" + nombreJugador;
	    String respuesta = llamarEndpointString(url);

	    System.out.println("\nRespuesta del servidor:");
	    System.out.println(respuesta);

	    idJuegoActual = respuesta.split("\"idJuego\":\"")[1].split("\"")[0];
	    enPartida = true;
	}
	
	public static void lobby() {
		System.out.println("--LISTADO DE PARTIDAS--");
		String url = servidor + "/juego/lista";
		llamarEndpoint(url);	
	}

	public static void unirsePartida() {
		System.out.print("ID del juego: ");
		String idJuego = sc.nextLine();

		System.out.print("Nombre del jugador: ");
		nombreJugador = sc.nextLine();

		String url = servidor + "/juego/" + idJuego + "/unirse?nombre=" + nombreJugador;
		llamarEndpoint(url);
		idJuegoActual = idJuego;
		enPartida = true;
	}
	
	public static void jugar() {
	    System.out.print("Tipo de jugada: ");
	    String tipo = sc.nextLine();

	    System.out.print("Valores de las cartas (separadas por espacio): ");
	    String valores = sc.nextLine();

	    System.out.print("Declaración (lo que anunciarás): ");
	    String declaracion = sc.nextLine();

	    try {
	        String valoresEncoded = URLEncoder.encode(valores, "UTF-8");
	        String declaracionEncoded = URLEncoder.encode(declaracion, "UTF-8");
	        String url = servidor + "/juego/" + idJuegoActual + "/jugada"
	                + "?nombre=" + URLEncoder.encode(nombreJugador, "UTF-8")
	                + "&tipo=" + URLEncoder.encode(tipo, "UTF-8")
	                + "&valores=" + URLEncoder.encode(valores, "UTF-8")
	                + "&declaracion=" + URLEncoder.encode(declaracion, "UTF-8");
	        llamarEndpoint(url);
	    } catch (Exception e) {
	        System.out.println("Error al codificar los valores de la jugada");
	    }
	}
	
	public static void levantar() {
        String url = servidor + "/juego/" + idJuegoActual + "/levantar?nombre=" + nombreJugador;
        llamarEndpoint(url);
    }
	
	public static void salirDePartida() {
        String url = servidor + "/juego/" + idJuegoActual + "/salir?nombre=" + nombreJugador;
        llamarEndpoint(url);
        enPartida = false;
        idJuegoActual = "";
    }
	
	public static boolean esMiTurno() {
	    try {
	        String url = servidor + "/juego/" + idJuegoActual + "/estado?nombre=" + nombreJugador;
	        String respuesta = llamarEndpointString(url);
	        return respuesta.contains("\"tuTurno\":true");
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static void mostrarUltimaJugada() {
	    String url = servidor + "/juego/" + idJuegoActual + "/estado?nombre=" + nombreJugador;
	    String respuesta = llamarEndpointString(url);

	    // Si no ha habido una jugada anterior
	    if (respuesta.contains("\"ultimaJugada\":{}")) {
	        System.out.println("Empiezas tú la partida.");
	        return;
	    }

	    // Si la ha habido
	    System.out.println("Última jugada:");
	    System.out.println(respuesta);
	}
	
	public static boolean revisarFinPartida() {
	    String url = servidor + "/juego/" + idJuegoActual + "/estado?nombre=" + nombreJugador;
	    String respuesta = llamarEndpointString(url);

	    // Comprobar cuántos jugadores quedan
	    if (respuesta.contains("\"jugadoresActivos\"")) {
	        int jugadoresRestantes = respuesta.split("\"jugadoresActivos\":\\[")[1].split("\\]")[0].split(",").length;
	        if (jugadoresRestantes <= 1) {
	            System.out.println("\n¡La partida ha terminado!");
	            System.out.println("Ganador: " + nombreJugador); // Si solo queda el cliente actual
	            enPartida = false;
	            idJuegoActual = "";
	            return true;
	        }
	    }
	    return false;
	}

	// Clase para llamar Endpoint

	@SuppressWarnings("deprecation")
	public static void llamarEndpoint(String urlStr) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String linea;
			System.out.println("\nRespuesta del servidor:");

			while ((linea = br.readLine()) != null) {
				System.out.println(linea);
			}

			br.close();

		} catch (Exception e) {
			System.out.println("Error al conectar con el servidor");
		}
	}
	
	// Lo mismo pero devuelve un string
	
	@SuppressWarnings("deprecation")
	public static String llamarEndpointString(String urlStr) {
	    StringBuilder resultado = new StringBuilder();
	    try {
	        URL url = new URL(urlStr);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("GET");

	        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        
	        String linea;

	        while ((linea = br.readLine()) != null) {
	            resultado.append(linea);
	        }

	        br.close();
	        
	    } catch (Exception e) {
	        System.out.println("Error al conectar con el servidor");
	    }
	    return resultado.toString();
	}
}
