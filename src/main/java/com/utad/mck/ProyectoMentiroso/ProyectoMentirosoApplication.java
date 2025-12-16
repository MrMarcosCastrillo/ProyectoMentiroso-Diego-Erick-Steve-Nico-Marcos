package com.utad.mck.ProyectoMentiroso;

import java.util.HashMap;
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

	private Map<String, Juego> juegos = new HashMap<>(); // Mapa para guardar los juegos activos

	@GetMapping("/juego/empezar") // endpoint
	public String empezarJuego(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
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