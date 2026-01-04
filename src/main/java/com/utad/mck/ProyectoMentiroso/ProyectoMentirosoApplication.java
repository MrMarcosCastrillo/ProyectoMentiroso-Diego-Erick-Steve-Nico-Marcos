package com.utad.mck.ProyectoMentiroso;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

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

	@GetMapping("/juego/{idJuego}/levantar")
	public Map<String, Object> levantar(@PathVariable String idJuego, @RequestParam String nombre) {
	    Map<String, Object> respuesta = new HashMap<>();
	    Juego partida = partidas.get(idJuego);

	    if (partida == null) {
	        respuesta.put("error", "Partida no encontrada");
	        return respuesta;
	    }

	    String nombreUltimoJugador = partida.getJugadorUltimaJugada();

	    boolean mintio = false;
	    for (String carta : partida.getUltimasCartasTiradasFisicas()) {
	        if (!carta.startsWith(partida.getUltimaDeclaracion())) {
	            mintio = true;
	            break;
	        }
	    }

	    Jugador eliminado;
	    String mensaje;
	    if (mintio) {
	        // Último jugador mintió -> se elimina
	        eliminado = buscarJugadorPorNombre(partida, nombreUltimoJugador);
	        mensaje = "¡Cazado! " + nombreUltimoJugador + " mentía y ha sido eliminado.";
	    } else {
	        // Acusador mintió -> se elimina
	        eliminado = buscarJugadorPorNombre(partida, nombre);
	        mensaje = "Era verdad... " + nombre + " mintió y ha sido eliminado.";
	    }

	    if (eliminado != null) {
	        partida.getJugadores().remove(eliminado);
	    }

	    // Pasar turno solo si queda más de un jugador
	    if (partida.getJugadores().size() > 0) {
	        partida.pasarTurno();
	    }

	    // Respuesta con estado actualizado
	    respuesta.put("resultado", mensaje);
	    respuesta.put("jugadoresRestantes", partida.getJugadores().stream().map(Jugador::getNombre).toList());
	    respuesta.put("turnoSiguiente", partida.getJugadorActual() != null ? partida.getJugadorActual().getNombre() : null);

	    return respuesta;
	}

	// Hacer la jugada
	@GetMapping("/juego/{idJuego}/jugada")
	public Map<String, Object> jugada(
	        @PathVariable String idJuego,
	        @RequestParam String nombre,
	        @RequestParam String tipo,
	        @RequestParam String valores,
	        @RequestParam String declaracion) { // NUEVO: lo que anuncia

	    Map<String, Object> respuesta = new HashMap<>();
	    Juego partida = partidas.get(idJuego);

	    if (partida == null) {
	        respuesta.put("error", "No existe la partida");
	        return respuesta;
	    }

	    Jugador jugador = buscarJugadorPorNombre(partida, nombre);
	    if (jugador == null) {
	        respuesta.put("error", "Jugador no encontrado");
	        return respuesta;
	    }

	    // Turno
	    Jugador turno = partida.getJugadorActual();
	    if (!turno.getNombre().equalsIgnoreCase(nombre)) {
	        respuesta.put("error", "No es tu turno. Le toca a " + turno.getNombre());
	        return respuesta;
	    }

	    // Bloqueo si está solo
	    if (!hayMasDeUnJugador(partida)) {
	        respuesta.put("error", "No puedes jugar hasta que haya al menos 2 jugadores.");
	        return respuesta;
	    }

	    // Cartas que dará físicamente
	    List<String> cartasJugadas = List.of(valores.split(" "));

	    // Comprobar que las tiene
	    if (!jugador.getCartas().containsAll(cartasJugadas)) {
	        respuesta.put("error", "No tienes esas cartas");
	        return respuesta;
	    }

	    // Quitar las cartas de la mano
	    jugador.getCartas().removeAll(cartasJugadas);

	    // Registrar la jugada en Juego
	    partida.registrarJugada(nombre, cartasJugadas, declaracion);

	    // Jugada VISIBLE para los demás
	    Map<String, Object> ultima = new HashMap<>();
	    ultima.put("jugador", nombre);
	    ultima.put("tipo", tipo);
	    ultima.put("declaracion", declaracion);
	    ultima.put("cantidad", cartasJugadas.size());

	    partida.setUltimaJugada(ultima);

	    // Pasar turno
	    partida.pasarTurno();

	    respuesta.put("ok", true);
	    respuesta.put("jugadaRegistrada", ultima);
	    respuesta.put("turnoSiguiente", partida.getJugadorActual().getNombre());

	    return respuesta;
	}
	
	@GetMapping("/juego/{idJuego}/unirse")
	public Map<String, Object> unirseJuego(@PathVariable("idJuego") String idJuego, // path variable saca la id de la
																					// url

			@RequestParam(value = "nombre") String nomJugador) {

		Map<String, Object> respuesta = new HashMap<>();

		// con la variable del pathvariable sacamos el id
		Juego partidaExistente = partidas.get(idJuego);

		if (partidaExistente.getJugadores().size() >= 5) {
			respuesta.put("error", "La partida está llena. Máximo 5 jugadores.");
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

	// endpoint que devolvera las partidas disponibles y sus jugadores dentro
	@GetMapping("/juego/lista")
	public List<Map<String, String>> listarPartidas() {
		List<Map<String, String>> listaParaCliente = new ArrayList<>();

		for (Juego juego : partidas.values()) {
			Map<String, String> info = new HashMap<>();
			info.put("idJuego", juego.getIdJuego());
			info.put("creador", juego.getJugadores().get(0).getNombre());
			info.put("jugadores", juego.getJugadores().size() + "/5");
			listaParaCliente.add(info);
		}
		return listaParaCliente;
	}
	
	@GetMapping("/juego/{idJuego}/salir")
	public Map<String, String> salirDePartida(@PathVariable String idJuego, @RequestParam String nombre) {
	    Map<String, String> respuesta = new HashMap<>();

	    Juego partida = partidas.get(idJuego);
	    if (partida == null) {
	        respuesta.put("error", "No existe la partida con idJuego=" + idJuego);
	        return respuesta;
	    }

	    // Buscar jugador y eliminarlo
	    Jugador jugador = buscarJugadorPorNombre(partida, nombre);
	    if (jugador != null) {
	        partida.getJugadores().remove(jugador);
	        respuesta.put("mensaje", nombre + " ha salido de la partida.");

	        // Si no quedan jugadores, eliminar la partida
	        if (partida.getJugadores().isEmpty()) {
	            partidas.remove(idJuego);
	            respuesta.put("mensaje", "La partida se ha eliminado porque no quedan jugadores.");
	        }
	    } else {
	        respuesta.put("error", "El jugador no estaba en la partida.");
	    }

	    return respuesta;
	}
	
	//endpoint para saber el estado de la partida
	
	@GetMapping("/juego/{idJuego}/estado")
	public Map<String, Object> estadoPartida(
	        @PathVariable String idJuego,
	        @RequestParam String nombre) {

	    Map<String, Object> respuesta = new HashMap<>();
	    Juego partida = partidas.get(idJuego);

	    if (partida == null) {
	        respuesta.put("error", "La partida no existe");
	        return respuesta;
	    }

	    Jugador jugador = buscarJugadorPorNombre(partida, nombre);
	    if (jugador == null) {
	        respuesta.put("error", "Jugador no encontrado");
	        return respuesta;
	    }

	    Jugador turnoActual = partida.getJugadorActual();

	    respuesta.put("tuTurno", turnoActual != null && turnoActual.getNombre().equalsIgnoreCase(nombre));
	    respuesta.put("turnoDe", turnoActual != null ? turnoActual.getNombre() : null);
	    respuesta.put("ultimaJugada", partida.getUltimaJugada());
	    respuesta.put("jugadoresActivos", partida.getJugadores().stream().map(Jugador::getNombre).toList());

	    return respuesta;
	}

	// metodos auxuliares

//metodo para encontrar a jugador por nombre
	private Jugador buscarJugadorPorNombre(Juego partida, String nombre) {
		for (Jugador j : partida.getJugadores()) {
			if (j.getNombre().equalsIgnoreCase(nombre)) {
				return j;
			}
		}
		return null;
	}
	
	private boolean hayMasDeUnJugador(Juego partida) {
	    return partida.getJugadores().size() > 1;
	}

	// metodo para repartir cartas

	public static void main(String[] args) {
		SpringApplication.run(ProyectoMentirosoApplication.class, args);

	}
}