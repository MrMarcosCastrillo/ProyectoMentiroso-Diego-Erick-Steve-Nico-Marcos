package com.utad.mck.ProyectoMentiroso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ClienteMentiroso {

	static Scanner sc = new Scanner(System.in);
	static String nombreJugador = null;
	static String idJuegoActual = null;
	static String cartasActuales = "-";
	static String servidor = "http://localhost:8080";

	public static void main(String[] args) {
		boolean seguir = true;
		System.out.println("-- CLIENTE JUEGO DEL MENTIROSO --");

		while (seguir) {
			System.out.println();
			System.out.println("====================================");
			System.out.println("      CLIENTE JUEGO DEL MENTIROSO    ");
			System.out.println("====================================");

			System.out.println("Jugador : " + (nombreJugador == null ? "-" : nombreJugador));
			System.out.println("Info jugador : " + cartasActuales);
			System.out.println("Partida : " + (idJuegoActual == null ? "-" : idJuegoActual));
			System.out.println("------------------------------------");

			System.out.println(" 1) Crear partida");
			System.out.println(" 2) Unirse a partida (Lobby)");
			System.out.println(" 3) Jugar");
			System.out.println(" 4) Levantar jugada");
			System.out.println(" 5) Ver última jugada");
			System.out.println(" 6) Salir");

			System.out.println("------------------------------------");
			System.out.print("Selecciona una opción: ");

			int opcion = sc.nextInt();
			sc.nextLine();

			switch (opcion) {
			case 1:
				crearPartida();
				break;
			case 2:
				lobby();
				break;
			case 3:
				jugar();
				break;
			case 4:
				levantar();
				break;
			case 5:
				verUltimaJugada();
				break;

			case 6:
				// Si estoy dentro de una partida, aviso al servidor antes de cerrar
				if (idJuegoActual != null && nombreJugador != null) {
					String url = servidor + "/juego/" + idJuegoActual + "/salir?nombre=" + nombreJugador;
					llamarEndpoint(url);
				}

				seguir = false;
				System.out.println("Saliendo...");
				break;
			}
		}
		sc.close();
	}

	public static void crearPartida() {
		System.out.print("Nombre del jugador: ");
		nombreJugador = sc.nextLine();

		String url = servidor + "/juego/empezar?nombre=" + nombreJugador;
		String json = llamarEndpoint(url);

		if (!json.isEmpty() && json.contains("\"idJuego\":\"")) {
			int i = json.indexOf("\"idJuego\":\"");
			int start = i + "\"idJuego\":\"".length();
			int end = json.indexOf("\"", start);
			idJuegoActual = json.substring(start, end);
			System.out.println("ID guardado: " + idJuegoActual);
		}

	}

	public static void lobby() {
		System.out.println("--LISTADO DE PARTIDAS--");
		llamarEndpoint(servidor + "/juego/lista");
		unirsePartida();
	}

	public static void unirsePartida() {
		System.out.print("ID del juego: ");
		idJuegoActual = sc.nextLine();
		System.out.print("Nombre del jugador: ");
		nombreJugador = sc.nextLine();

		String url = servidor + "/juego/" + idJuegoActual + "/unirse?nombre=" + nombreJugador;
		llamarEndpoint(url);
	}

	public static void jugar() {
		if (idJuegoActual == null) {
			System.out.println("Error: No estas en ninguna partida");
			return;
		}
		System.out.print("Cartas (ej: 2C,10D,4T): ");
		String cartas = sc.nextLine();
		System.out.print("Tipo (carta/pareja/dosparejas/trio/full/poker): ");
		String tipo = sc.nextLine();
		System.out.print("Que has tirado: ");
		String cartasSupuestas = sc.nextLine();

		String url = servidor + "/juego/" + idJuegoActual + "/jugada?nombre=" + nombreJugador + "&cartas=" + cartas
				+ "&tipo=" + tipo + "&cartasSupuestas=" + cartasSupuestas;
		llamarEndpoint(url);
	}

	public static void levantar() {
		if (idJuegoActual == null)
			return;
		String url = servidor + "/juego/" + idJuegoActual + "/levantar?nombre=" + nombreJugador;
		llamarEndpoint(url);
	}

	public static String llamarEndpoint(String urlStr) {
		StringBuilder respuesta = new StringBuilder();
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String linea;
			System.out.println("\nRespuesta del servidor:");
			while ((linea = br.readLine()) != null) {
				System.out.println(linea);
				respuesta.append(linea);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error al conectar con el servidor.");
		}

		String json = respuesta.toString();

		if (json.contains("misCartas")) {
			cartasActuales = json;
		}

		return respuesta.toString();
	}

	public static void verUltimaJugada() {
		if (idJuegoActual == null) {
			System.out.println("No estás en ninguna partida");
			return;
		}
		if (nombreJugador == null) {
			System.out.println("No tienes nombre de jugador");
			return;
		}

		String url = servidor + "/juego/" + idJuegoActual + "/estado?nombre=" + nombreJugador;
		llamarEndpoint(url);
	}

}