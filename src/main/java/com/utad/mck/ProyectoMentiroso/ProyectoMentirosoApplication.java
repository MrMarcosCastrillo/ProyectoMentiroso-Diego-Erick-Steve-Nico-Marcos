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
	
	//-----------------------------ENDPOINT EMPEZAR UNA PARTIDA-----------------------------\\

	@GetMapping("/juego/empezar")
	public Map<String, Object> empezarJuego(
			@RequestParam(value = "nombre", defaultValue = "Invitado") String nomJugador) {

		// al iniciar el juego las cartas ya estarán barajadas
		Juego nuevaPartida = new Juego();

		// el metodo robarCartas robara del mazo las 5 cartas inciales que necesitamos
		List<String> cartasJugador = nuevaPartida.robarCartas(5);

		Jugador jugadorNuevo = new Jugador(nomJugador, nomJugador, cartasJugador, false);
		nuevaPartida.getJugadores().add(jugadorNuevo); // añade a la partida

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
	
	
	//-----------------------------ENDPOINT UNIRSE A UNA PARTIDA-----------------------------\\
	
	@GetMapping("/juego/{idJuego}/unirse")
	public Map<String, Object> unirseJuego(@PathVariable("idJuego") String idJuego, 
			@RequestParam(value = "nombre") String nomJugador) {
		// path variable saca la id de la
		// url

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

		// Robara del mazo 5 cartas
		List<String> cartasJugador = partidaExistente.robarCartas(5);

		Jugador jugadorNuevo = new Jugador(nomJugador, nomJugador, cartasJugador, false);
		partidaExistente.getJugadores().add(jugadorNuevo);//se crea el jugador

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

		//Ver si me toca jugar ahora
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
	
	
	
	//-----------------------------ENDPOINT VER PARTIDAS-----------------------------\\

	// endpoint que devolvera las partidas disponibles y sus jugadores dentro//se usa en cliente (lobby)
	@GetMapping("/juego/lista")
	public List<Map<String, String>> listarPartidas() {
		List<Map<String, String>> listaParaCliente = new ArrayList<>();

		for (Juego juego : partidas.values()) {
			Map<String, String> info = new HashMap<>();
			info.put("idJuego", juego.getIdJuego()); //id
			info.put("creador", juego.getJugadores().get(0).getNombre()); //creador del juego
			info.put("jugadores", juego.getJugadores().size() + "/5");  //jugadore que hay como 2/5
			listaParaCliente.add(info);//se añade para mostrarlo en cliente 
		}
		return listaParaCliente;
	}

	
	
	//-----------------------------ENDPOINT PARA HACER LA JUGADA-----------------------------\\

	// Hacer la jugada
	@GetMapping("/juego/{idJuego}/jugada")
	public Map<String, Object> jugada(@PathVariable("idJuego") String idJuego, @RequestParam("nombre") String nombre,
			@RequestParam("cartas") String cartasStr, @RequestParam("tipo") String tipo,
			@RequestParam("cartasSupuestas") String cartasSupuestas) { 
		Map<String, Object> respuesta = new HashMap<>();

		cartasSupuestas = normalizarValores(cartasSupuestas);
		//Si se mete 10D, se utiliza solo el 10 (valorDeCarta metodo debajo auxiliar)

		//ver que exista el juego
		Juego partida = partidas.get(idJuego);
		if (partida == null) {
			respuesta.put("error", "No existe la partida con idJuego=" + idJuego);
			return respuesta;
		}

		// Buscar jugador, al introducir el nombre si no está, se evita que juegue
		Jugador jugador = buscarJugadorPorNombre(partida, nombre);
		if (jugador == null) {
			respuesta.put("error", "No existe el jugador '" + nombre + "' en esta partida");
			return respuesta;
		}

		// Comprobar si es su turno
		Jugador jugadorTurno = partida.getJugadorActual();

		if (jugadorTurno == null || !jugadorTurno.getNombre().equalsIgnoreCase(nombre)) { //comprobacion
			respuesta.put("error",
					"No es tu turno. Le toca a " + (jugadorTurno == null ? "nadie" : jugadorTurno.getNombre()));
			return respuesta;
		}

		// Parsear cartas físicas
		List<String> cartasPedidas = new ArrayList<>();
		for (String c : cartasStr.split(",")) { //cartasStr es "2C,10D,4T" lo separo con comas
			String carta = c.trim();//se quitan los espacios con el trim()
			if (!carta.isEmpty())
				cartasPedidas.add(carta); //["2C","10D","4T"]
		}

		if (cartasPedidas.isEmpty()) { //que tire minimo 1 carta
			respuesta.put("error", "Debes tirar al menos 1 carta");
			return respuesta;
		}

		int n = cartasPedidas.size();
		tipo = tipo.toLowerCase().trim();

		boolean valido =
		        (tipo.equals("carta") && n == 1) ||
		        (tipo.equals("pareja") && n == 2) ||
		        (tipo.equals("trio") && n == 3) ||
		        (tipo.equals("dosparejas") && n == 4) ||
		        (tipo.equals("full") && n == 5) ||
		        (tipo.equals("poker") && n == 5);

		if (!valido) {
		    respuesta.put("error", "El tipo '" + tipo + "' no coincide con el número de cartas (" + n + ")");
		    return respuesta;
		}

		if (!declaracionValidaSimple(tipo, cartasSupuestas, n)) {
		    respuesta.put("error", "La declaración no encaja con el tipo");
		    return respuesta;
		

		} // para ver que coincide con el numero de cartas a la hora de echarlas

		// Validar que el jugador TIENE esas cartas en su mano
		// y evitar duplicados)
		List<String> mano = jugador.getCartas();
		List<String> cartasAEliminar = new ArrayList<>();

		for (String carta : cartasPedidas) {
			if (!mano.contains(carta)) { //ver si tiene las cartas que tiro en su mano
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

		//quitar las cartas echadas
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
		
		Jugador ganador = partida.getGanadorSiExiste();
		if (ganador != null) {
		    partida.setPartidaTerminada(true);
		    respuesta.put("ganador", ganador.getNombre());
		}


		return respuesta;
	}
	
	
	//-----------------------------ENDPOINT PARA LEVANTAR LA JUGADA-----------------------------\\

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

		// valores declarados ("10,10")
		List<String> declaradas = new ArrayList<>();
		for (String x : declaracion.split(",")) declaradas.add(x);

		// valores reales ("10D","2D" -> "10","2")
		List<String> reales = new ArrayList<>();
		for (String c : partida.getUltimasCartasTiradasFisicas())
		    reales.add(valorDeCarta(c));

		// comparar cantidades
		if (reales.size() != declaradas.size()) mintio = true;

		// comparar valores uno a uno (sin importar orden)
		for (int i = 0; i < reales.size() && !mintio; i++) {
		    String r = reales.get(i);
		    boolean encontrada = false;

		    for (int j = 0; j < declaradas.size(); j++) {
		        if (r.equals(declaradas.get(j))) {
		            declaradas.remove(j);
		            encontrada = true;
		            j = declaradas.size();
		        }
		    }

		    if (!encontrada) mintio = true;
		}

		if (!declaradas.isEmpty()) mintio = true;


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

	//-----------------------------ENDPOINT PARA SALIRSE-----------------------------\\
	
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
	
	
	//-----------------------------ENDPOINT PARA VER ULTIMA JUGADA-----------------------------\\

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

	

	// la comparación de jugadas para ver si la qu tiraste es superior a la anterior
	private boolean esSuperior(String tipoNuevo, String valoresNuevo, String tipoAnt, String valoresAnt) { //pareja, 12,1, tipo de la anterior y valor de la jugada anterior
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

	private int rankValor(String v) { //para poder comparar todo bien
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
	

	private String valorDeCarta(String carta) { //quitar el palo de la carta
		// "10D" = "10"
		return carta.replaceAll("[^0-9]", "");
	}
	
	
	private String normalizarValores(String input) {
	    if (input == null) return "";
	    // quita espacios y deja solo digitos y comas
	    input = input.replace(" ", "");
	    input = input.replaceAll("[^0-9,]", "");
	    // quita comas repetidas o al principio o al final
	    input = input.replaceAll(",+", ",");
	    input = input.replaceAll("^,|,$", "");
	    return input;
	}
	
	
	
	
	private boolean declaracionValidaSimple(String tipo, String cartasSupuestas, int n) {
	    if (cartasSupuestas == null) return false;

	    String[] v = cartasSupuestas.split(",");
	    if (v.length != n) return false; //por si metes en lo que dices que es pareja, y poner 3 cartas
	    int distintos = 0;

	    for (int i = 0; i < v.length; i++) {
	        boolean repetido = false;
	        for (int j = 0; j < i; j++) {
	            repetido = repetido || v[i].equals(v[j]);
	        }
	        if (!repetido) distintos++;
	    }

	    if (tipo.equals("carta"))      return n == 1 && distintos == 1;
	    if (tipo.equals("pareja"))     return n == 2 && distintos == 1;
	    if (tipo.equals("trio"))       return n == 3 && distintos == 1;
	    if (tipo.equals("dosparejas")) return n == 4 && distintos == 2;
	    if (tipo.equals("full"))  return n == 5 && distintos == 2;
	    if (tipo.equals("poker")) return n == 5 && distintos == 2;

	    return false;
	}

	

	

	public static void main(String[] args) {
		SpringApplication.run(ProyectoMentirosoApplication.class, args);

	}
}