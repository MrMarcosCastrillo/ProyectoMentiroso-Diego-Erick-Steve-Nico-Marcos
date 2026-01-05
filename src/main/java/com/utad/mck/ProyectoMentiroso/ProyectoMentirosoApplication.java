package com.utad.mck.ProyectoMentiroso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		respuesta.put("misCartas", jugadorNuevo.getCartas());

		return respuesta;

	}

	@GetMapping("/juego/{idJuego}/levantar")
	public Map<String, Object> levantar(@PathVariable String idJuego, @RequestParam String nombre) {
		Map<String, Object> respuesta = new HashMap<>();
		Juego partida = partidas.get(idJuego);
		if (partida == null) {
			respuesta.put("error", "No existe la partida");
			return respuesta;
		}
		if (partida.getUltimaJugada() == null || partida.getUltimaJugada().isEmpty()) {
			respuesta.put("error", "No hay jugada anterior que levantar");
			return respuesta;
		}

		// nombre ultimo jugador
		String nombreSospechoso = (String) partida.getUltimaJugada().get("jugador");

		// comprobar si miente o no
		boolean mintio = false;
		String declaracion = partida.getUltimaDeclaracion();

		for (String carta : partida.getUltimasCartasTiradasFisicas()) {
			String valorReal = valorDeCarta(carta);

			if (!valorReal.equals(declaracion)) {
				mintio = true;
			}
		}

		// si miente se aplica el castigo
		if (mintio) {
			// si el sospechoso miente se lleva las cartas
			Jugador sospechoso = buscarJugadorPorNombre(partida, nombreSospechoso);
			sospechoso.setEstaEliminado(true);
			respuesta.put("resultado", "¡Cazado! " + nombreSospechoso + " mentía. Es eliminado");
		} else {
			// si no el acusador se las lleva
			Jugador acusador = buscarJugadorPorNombre(partida, nombre);
			acusador.setEstaEliminado(true);
			respuesta.put("resultado", "Era verdad... " + nombre + "Eres eliminado");
		}

		Jugador ganador = partida.getGanadorSiExiste();
		if (ganador != null) {
			partida.setPartidaTerminada(true);
			respuesta.put("ganador", ganador.getNombre());
		}

		Jugador yo = buscarJugadorPorNombre(partida, nombre);
		if (yo != null) {
			respuesta.put("misCartas", yo.getCartas());
			respuesta.put("estoyEliminado", yo.isEstaEliminado());
		} // para poder ver siempre mis cartas

		return respuesta;

	}

	// Hacer la jugada
	@GetMapping("/juego/{idJuego}/jugada")
	public Map<String, Object> jugada(@PathVariable("idJuego") String idJuego, @RequestParam("nombre") String nombre,
			@RequestParam("cartas") String cartasStr, @RequestParam("tipo") String tipo,
			@RequestParam("cartasSupuestas") String cartasSupuestas) {
		Map<String, Object> respuesta = new HashMap<>();

		cartasSupuestas = valorDeCarta(cartasSupuestas);

		Juego partida = partidas.get(idJuego);
		if (partida == null) {
			respuesta.put("error", "No existe la partida con idJuego=" + idJuego);
			return respuesta;
		}

		// Buscar jugador
		Jugador jugador = buscarJugadorPorNombre(partida, nombre);
		if (jugador == null) {
			respuesta.put("error", "No existe el jugador '" + nombre + "' en esta partida");
			return respuesta;
		}

		// Comprobar si es su turno
		Jugador jugadorTurno = partida.getJugadorActual();

		if (jugadorTurno == null || !jugadorTurno.getNombre().equalsIgnoreCase(nombre)) {
			respuesta.put("error",
					"No es tu turno. Le toca a " + (jugadorTurno == null ? "nadie" : jugadorTurno.getNombre()));
			return respuesta;
		}

		// Parsear cartas físicas
		List<String> cartasPedidas = new ArrayList<>();
		for (String c : cartasStr.split(",")) {
			String carta = c.trim();
			if (!carta.isEmpty())
				cartasPedidas.add(carta);
		}

		if (cartasPedidas.isEmpty()) {
			respuesta.put("error", "Debes tirar al menos 1 carta");
			return respuesta;
		}

		int n = cartasPedidas.size();
		tipo = tipo.toLowerCase().trim();

		boolean valido = (tipo.equals("carta") && n == 1) || (tipo.equals("pareja") && n == 2)
				|| (tipo.equals("dosparejas") && n == 4) || (tipo.equals("trio") && n == 3)
				|| (tipo.equals("full") && n == 5) || (tipo.equals("poker") && n == 4);

		if (!valido) {
			respuesta.put("error", "El tipo '" + tipo + "' no coincide con el número de cartas (" + n + ")");
			return respuesta;
		} // para ver que coincide con el numero de cartas a la hora de echarlas

		// Validar que el jugador TIENE esas cartas en su mano
		// y evitar duplicados)
		List<String> mano = jugador.getCartas();
		List<String> cartasAEliminar = new ArrayList<>();

		for (String carta : cartasPedidas) {
			if (!mano.contains(carta)) {
				respuesta.put("error", "No tienes la carta " + carta + " en tu mano. Mano actual: " + mano);
				return respuesta;
			}
			cartasAEliminar.add(carta);
		}

		// Si hay jugada anterior, comprobar que esta declaración la supera
		if (partida.getUltimaJugada() != null && !partida.getUltimaJugada().isEmpty()) {
			String tipoAnterior = (String) partida.getUltimaJugada().get("tipo");
			String cartaSupAnterior = (String) partida.getUltimaJugada().get("cartasSupuestas");

			if (!esSuperior(tipo, cartasSupuestas, tipoAnterior, cartaSupAnterior)) {
				respuesta.put("error",
						"No se admite: no supera la jugada anterior (" + tipoAnterior + " " + cartaSupAnterior + ")");
				respuesta.put("turnoActual", partida.getJugadorActual().getNombre());
				return respuesta;
			}
		}

		// 3) Mover mano -> mesa (quitar de la mano)
		for (String carta : cartasAEliminar) {
			mano.remove(carta); // quita 1 ocurrencia
		}

		// 4) Registrar en la partida (mesa + última jugada real + declaración)
		partida.registrarJugada(cartasPedidas, cartasSupuestas);

		Map<String, Object> ultima = new HashMap<>();
		ultima.put("jugador", nombre);
		ultima.put("tipo", tipo);
		ultima.put("cartasSupuestas", cartasSupuestas);
		ultima.put("cantidad", cartasPedidas.size());
		partida.setUltimaJugada(ultima);

		// Si juega el creador (primer jugador), contamos su turno
		Jugador creador = partida.getJugadores().get(0);
		if (jugador.getNombre().equalsIgnoreCase(creador.getNombre())) {
			partida.sumarTurnosCreador();
		}

		// Pasar turno al siguiente jugador
		partida.pasarTurno();

		respuesta.put("ok", true);
		respuesta.put("idJuego", idJuego);
		respuesta.put("jugador", nombre);
		respuesta.put("tiradas", cartasPedidas);
		respuesta.put("manoRestante", jugador.getCartas());
		respuesta.put("misCartas", jugador.getCartas());
		respuesta.put("mesa", partida.getCartasMesa());
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

		if (partidaExistente == null) {
			respuesta.put("error", "No existe la partida con idJuego=" + idJuego);
			return respuesta;
		} // por si mete mal el id q no pete

		// Comprobar si el jugador ya está en la partida
		for (Jugador j : partidaExistente.getJugadores()) {
			if (j.getNombre().equalsIgnoreCase(nomJugador)) {
				respuesta.put("error", "El jugador ya está en la partida");
				return respuesta;
			}
		}

		if (partidaExistente.getJugadores().size() >= 5) {
			respuesta.put("error", "La partida está llena. Máximo 5 jugadores.");
			return respuesta;
		}

		// Comprobar si es tarde para unirse
		if (partidaExistente.getTurnosCreador() >= 2) {
			respuesta.put("error", "Es tarde para unirse a la partida");
			return respuesta;
		}

		// Robara del mazo otras 5 cartas
		List<String> cartasJugador = partidaExistente.robarCartas(5);

		Jugador jugadorNuevo = new Jugador(nomJugador, nomJugador, cartasJugador, false);
		partidaExistente.getJugadores().add(jugadorNuevo);

		// Respuesta
		respuesta.put("idJugador", jugadorNuevo.getIdJugador());
		respuesta.put("idJuego", partidaExistente.getIdJuego());
		respuesta.put("cartas", jugadorNuevo.getCartas());
		respuesta.put("mensaje", "Te has unido a la partida exitosamente");

		// Nombres de los jugadores actuales
		List<String> nombres = new ArrayList<>();
		for (Jugador j : partidaExistente.getJugadores()) {
			nombres.add(j.getNombre());
		}
		respuesta.put("jugadoresActuales", nombres);

		// ¿Me toca jugar ahora?
		Jugador actual = partidaExistente.getJugadorActual();
		boolean meToca = false;
		if (actual != null) {
			meToca = actual.getNombre().equalsIgnoreCase(nomJugador);
		}
		respuesta.put("meToca", meToca);

		// Si me toca y hay jugada anterior, la mostramos
		if (meToca) {
			if (!partidaExistente.getUltimaJugada().isEmpty()) {
				respuesta.put("jugadaAnterior", partidaExistente.getUltimaJugada());
			} else {
				respuesta.put("jugadaAnterior", null);
				respuesta.put("mensajeJugadaAnterior", "Eres el primero en jugar, no hay jugada anterior.");
			}
		}
		respuesta.put("misCartas", jugadorNuevo.getCartas());

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

	// Salirse de la partida y que se elimine si hay 0 personas
	@GetMapping("/juego/{idJuego}/salir")
	public Map<String, Object> salir(@PathVariable String idJuego, @RequestParam String nombre) {

		Map<String, Object> resp = new HashMap<>();

		Juego partida = partidas.get(idJuego);
		if (partida == null) {
			resp.put("error", "No existe la partida");
			return resp;
		}

		Jugador jugador = buscarJugadorPorNombre(partida, nombre);
		if (jugador == null) {
			resp.put("error", "El jugador no está en la partida");
			return resp;
		}

		// quitar jugador
		partida.getJugadores().remove(jugador);

		// si no queda nadie borrar partida
		if (partida.getJugadores().isEmpty()) {
			partidas.remove(idJuego);
		}

		resp.put("ok", true);
		return resp;
	}

	@GetMapping("/juego/{idJuego}/estado")
	public Map<String, Object> estado(@PathVariable String idJuego, @RequestParam String nombre) {

		Map<String, Object> resp = new HashMap<>();

		Juego partida = partidas.get(idJuego);
		if (partida == null) {
			resp.put("error", "No existe la partida");
			return resp;
		}

		Jugador yo = buscarJugadorPorNombre(partida, nombre);
		if (yo == null) {
			resp.put("error", "No estás en la partida");
			return resp;
		}

		Jugador turno = partida.getJugadorActual();

		if (turno != null) {
			resp.put("turnoDe", turno.getNombre());

			if (turno.getNombre().equalsIgnoreCase(nombre)) {
				resp.put("tuTurno", true);
			} else {
				resp.put("tuTurno", false);
			}

		} else {
			resp.put("turnoDe", null);
			resp.put("tuTurno", false);
		}

		resp.put("ultimaJugada", partida.getUltimaJugada());
		resp.put("misCartas", yo.getCartas());

		return resp;
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

	private String valorPrincipalString(String valores) {
		if (valores == null)
			return "";
		String[] partes = valores.split(",");
		return partes[0].trim(); // ej "10,8" -> "10"
	}

	// la comparación de jugadas
	private boolean esSuperior(String tipoNuevo, String valoresNuevo, String tipoAnt, String valoresAnt) {
		int rNuevo = rankTipo(tipoNuevo);
		int rAnt = rankTipo(tipoAnt);

		if (rNuevo > rAnt)
			return true;
		if (rNuevo < rAnt)
			return false;

		int vNuevo = valorPrincipal(valoresNuevo);
		int vAnt = valorPrincipal(valoresAnt);

		return vNuevo > vAnt;
	}

	private int rankTipo(String tipo) {
		if (tipo == null)
			return 0;
		tipo = tipo.toLowerCase().trim();

		if (tipo.equals("carta"))
			return 1;
		if (tipo.equals("pareja"))
			return 2;
		if (tipo.equals("dosparejas"))
			return 3;
		if (tipo.equals("trio"))
			return 4;
		if (tipo.equals("full"))
			return 5;
		if (tipo.equals("poker"))
			return 6;

		return 0;
	}

	private int valorPrincipal(String valores) {
		if (valores == null || valores.trim().isEmpty())
			return -1;
		String[] partes = valores.split(",");
		String v = partes[0].trim();
		return rankValor(v);
	}

	private int rankValor(String v) {
		if (v == null)
			return -1;
		v = v.trim();

		if (v.equals("1"))
			return 14; // As
		if (v.equals("12"))
			return 13; // Reina
		if (v.equals("11"))
			return 12; // Jota

		try {
			return Integer.parseInt(v); // 2..10
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private String valorDeCarta(String carta) {
		// "10D" -> "10"
		return carta.replaceAll("[^0-9]", "");
	}

	public static void main(String[] args) {
		SpringApplication.run(ProyectoMentirosoApplication.class, args);

	}
}