package com.utad.mck.ProyectoMentiroso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProyectoMentirosoApplication {

	// La url base es http://localhost:8080

	static Scanner sc = new Scanner(System.in);

	private Map<String, Juego> partidas = new HashMap<>(); // Mapa para guardar los juegos activos

	@GetMapping("/juego/empezar")
	public Map<String, Object> empezarJuego(
			@RequestParam(value = "nombre", defaultValue = "Invitado") String nomJugador) {

		// al iniciar el juego las cartas ya estarán barajadas
		Juego nuevaPartida = new Juego();

		// el metodo robarCartas robara del mazo las 5 cartas inciales que necesitamos
		List<String> cartasJugador = nuevaPartida.robarCartas(5);

		Jugador jugadorNuevo = new Jugador(nomJugador, nomJugador, cartasJugador, false);
		nuevaPartida.getJugadores().add(jugadorNuevo);

		// la partida aparecera en la lista de partidas
		partidas.put(nuevaPartida.getIdJuego(), nuevaPartida);

		// Respuesta
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("idJugador", jugadorNuevo.getIdJugador());
		respuesta.put("idJuego", nuevaPartida.getIdJuego());
		respuesta.put("cartas", jugadorNuevo.getCartas());

		return respuesta;
	}

	@GetMapping("/juego/{idJuego}/jugada") // endpoint
	public String jugada(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
	
	
	@GetMapping("/juego/{idJuego}/levantar") // endpoint
	public String levantarJugada(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/juego/{idJuego}/unirse")
	public Map<String, Object> unirseJuego(@PathVariable("idJuego") String idJuego, // path variable saca la id de la
																					// url

			@RequestParam(value = "nombre") String nomJugador) {

		Map<String, Object> respuesta = new HashMap<>();

		// con la variable del pathvariable sacamos el id
		Juego partidaExistente = partidas.get(idJuego);
		if (partidaExistente == null) { // si no existe la respuesta sera un mensaje de error
			respuesta.put("error", "La partida no existe");
			return respuesta;
		}

		// Robara del mazo otras 5 cartas (SERAN DE ESE MISMO MAZO
		List<String> cartasJugador = partidaExistente.robarCartas(5);

		Jugador jugadorNuevo = new Jugador(nomJugador, nomJugador, cartasJugador, false);
		partidaExistente.getJugadores().add(jugadorNuevo);

		// Respuesta
		respuesta.put("idJugador", jugadorNuevo.getIdJugador());
		respuesta.put("idJuego", partidaExistente.getIdJuego());
		respuesta.put("cartas", jugadorNuevo.getCartas());
		respuesta.put("mensaje", "Te has unido a la partida exitosamente");

		return respuesta;
	}

	// metodo para repartir cartas

	public static void main(String[] args) {
		SpringApplication.run(ProyectoMentirosoApplication.class, args);

	}
}