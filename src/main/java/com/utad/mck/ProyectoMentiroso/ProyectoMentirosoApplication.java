package com.utad.mck.ProyectoMentiroso;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;			
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProyectoMentirosoApplication {
	static Scanner sc = new Scanner(System.in);

	private Map<String, Juego> partidas = new HashMap<>(); // Mapa para guardar los juegos activos

	private static String[] SIMBOLO = { "C", "D", "P", "T" }; // Corazones, diamantes, picas y treboles
	private static String[] NUMERO = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

	@GetMapping("/juego/empezar") // endpoint
	public Map<String, Object> empezarJuego(
			@RequestParam(value = "nombre", defaultValue = "Invitado") String nomJugador) {

		// Dejamos lista la baraja que sera del jugador
		List<String> barajaInicial = repartirCartas();

		// Creamos al jugador
		// !!DE MOMENTO EL ID ES EL NOMBRE DEL JUGADOR HAY QUE CAMBIARLO
		Jugador jugadorNuevo = new Jugador(nomJugador, nomJugador, barajaInicial, false);

		// Creamos la partida
		List<Jugador> jugadores = new ArrayList<>();
		jugadores.add(jugadorNuevo);

		Juego nuevaPartida = new Juego(jugadores, 0); // el jugador actual sera el creador

		partidas.put(nuevaPartida.getIdJuego(), nuevaPartida); // añadimos la partida a la lista de partidas

		Map<String, Object> respuesta = new HashMap<String, Object>(); // como el endpoint devolvera un mapa, la
																		// respuesta sera un mapa
		respuesta.put("idJuego", nuevaPartida.getIdJuego());
		respuesta.put("cartas", jugadorNuevo.getCartas());

		return respuesta;

	}

	@GetMapping("/juego/{idJuego}/jugar") // endpoint
	public String juegarJuego(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/juego/{idJuego}/levantar") // endpoint
	public String levantarJugada(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/juego/{idJuego}/unirse") // endpoint
	public String unirseJuego(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	// metodo para repartir cartas
	private List<String> repartirCartas() {
		List<String> mazo = new ArrayList<>();
		for (String simbolo : SIMBOLO) {
			for (String numero : NUMERO) {
				mazo.add(numero + simbolo); // 3C, 4A, 1P
			}

		}

		Collections.shuffle(mazo); // aleatoriza las cartas, es necesario porque si no el orden de las cartas será
									// el mismo

		// guardara las cartas barajadas que se le daran al jugador
		List<String> cartasRepartidas = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			cartasRepartidas.add(mazo.get(i)); // daremos solo 5 cartas del mazo
		}

		return cartasRepartidas;

	}

	// hay que definir el turno del usuario, en plan que cuando sea tu turno puedas
	// elegir

	public void menuDentroPartida() {
		boolean seguir = true;

		System.out.println("ELIGE TU JUGADA:" + "\n");
		System.out.println("1. Mostrar tus cartas");
		System.out.println("2. Ver estado de la partida");
		System.out.println("3. Realizar una jugada");
		System.out.println("4. Levantar la jugada anterior");
		System.out.println("5. Abandonar partida");

		while (seguir) {
			int opcionJuego = sc.nextInt();
			switch (opcionJuego) {
			case 1: {
				System.out.println("--TUS CARTAS--");

				break;
			}
			case 2: {
				System.out.println("--ESTADO DE LA PARTIDA--");

				break;
			}
			case 3: {
				System.out.println("--JUGADAS--");

				break;
			}
			case 4: {
				System.out.println("--JUGADA ANTERIOR--");

				break;
			}
			case 5: {
				System.out.println("Abandonando partida...");
				seguir = false;

				break;
			}

			}
		}
	}

	public static void main(String[] args) {

		boolean seguir = true;
		SpringApplication.run(ProyectoMentirosoApplication.class, args);

		System.out.println("--BIENVENIDO AL JUEGO DEL MENTIROSO--");
		System.out.println("1. Crear una partida");
		System.out.println("2. Unirse a una partida");
		System.out.println("3. Salir");

		while (seguir) {
			int opcion = sc.nextInt();

			switch (opcion) {
			case 1: {
				System.out.println("--CREACION DE PARTIDA--");

				break;
			}
			case 2: {
				System.out.println("--UNIRSE A PARTIDA--");

				break;
			}
			case 3: {
				System.out.println("Salinedo del programa...");

				seguir = false;
				sc.close();

				break;
			}

			}
		}

	}

}