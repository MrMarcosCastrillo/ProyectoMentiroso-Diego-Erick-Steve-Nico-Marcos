package com.utad.mck.ProyectoMentiroso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        boolean seguir = true;

        while (seguir) {
            System.out.println("\n--- MENÚ DE LA PARTIDA ---");
            System.out.println("1. Mostrar tus cartas");
            System.out.println("2. Ver estado de la partida");
            System.out.println("3. Realizar una jugada");
            System.out.println("4. Levantar la jugada anterior");
            System.out.println("5. Abandonar partida");

            int opcionJuego = sc.nextInt(); //Hay que cambiarlo tambien por un sc.nextLine en un trycatch con while para que no de errores
            sc.nextLine();

            switch (opcionJuego) {
                case 1:
                    System.out.println("--TUS CARTAS--");
                    // Un endpoint para mostrar cartas
                    break;
                case 2:
                    System.out.println("--ESTADO DE LA PARTIDA--");
                    // Hacer GET /juego/{idJuego}/estado ?
                    break;
                case 3:
                    jugar();
                    break;
                case 4:
                    levantar();
                    break;
                case 5:
                    salirDePartida();
                    seguir = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

	// Metodos para aprovechar los endpoint

	public static void crearPartida() {
		System.out.print("Nombre del jugador: ");
		nombreJugador = sc.nextLine();

		String url = servidor + "/juego/empezar?nombre=" + nombreJugador;
		String respuesta = llamarEndpointString(url);
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

        System.out.print("Valores de las cartas: ");
        String valores = sc.nextLine();

        String url = servidor + "/juego/" + idJuegoActual + "/jugada?nombre=" + nombreJugador
                + "&tipo=" + tipo + "&valores=" + valores;
        llamarEndpoint(url);
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
	        
	        System.out.println("\nRespuesta del servidor:");
	        System.out.println(resultado.toString());

	    } catch (Exception e) {
	        System.out.println("Error al conectar con el servidor");
	    }
	    return resultado.toString();
	}
}
