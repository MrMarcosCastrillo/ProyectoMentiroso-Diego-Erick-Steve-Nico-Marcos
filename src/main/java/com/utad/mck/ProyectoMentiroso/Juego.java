package com.utad.mck.ProyectoMentiroso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Juego {
	private static final String[] SIMBOLO = { "C", "D", "P", "T" };
	private static final String[] NUMERO = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "1" };

	private String idJuego;
	private List<Jugador> jugadores;
	private int idJugadorActual;
	private boolean partidaTerminada;
	private List<String> mazo;
	private Map<String, Object> ultimaJugada = new java.util.HashMap<>();
	private List<String> cartasMesa = new ArrayList<>();
	private List<String> ultimasCartasTiradasFisicas = new ArrayList<>(); // para hacer el endpoint levantar
	private String ultimaDeclaracion = "";
	private int turnosCreador = 0;

	// CONSTRUCTOR
	public Juego() {
		this.idJuego = UUID.randomUUID().toString();
		this.jugadores = new ArrayList<>();
		this.mazo = new ArrayList<>();
		this.cartasMesa = new ArrayList<>();
		this.idJugadorActual = 0;
		this.partidaTerminada = false;

		inicializarMazo();
	}

	private void inicializarMazo() { // para crear las cmbinaciones del mazo
		this.mazo.clear();
		for (String simbolo : SIMBOLO) {
			for (String numero : NUMERO) {
				this.mazo.add(numero + simbolo);
			}
		}
		Collections.shuffle(this.mazo);
	}

	public List<String> robarCartas(int cantidad) {
		List<String> mano = new ArrayList<>();
		for (int i = 0; i < cantidad; i++) { // reparte 5 al crear partida o unirse
			if (this.mazo != null && !this.mazo.isEmpty()) {
				mano.add(this.mazo.remove(0)); // para que se quite la carta del mazo y no vuelva a salir
			}
		}
		return mano;
	}

	// metodo que registra la jugada real contra la que se ha declarado
	public void registrarJugada(List<String> cartasFisicas, String declaracion) { // las cartas que ha tirado, y lo que
																					// dice que ha tirado
		this.ultimasCartasTiradasFisicas = new ArrayList<>(cartasFisicas);
		this.ultimaDeclaracion = declaracion;
		this.cartasMesa.addAll(cartasFisicas);
	}

	// GETTERS Y SETTERS
	public String getIdJuego() {
		return idJuego;
	}

	public void setIdJuego(String idJuego) {
		this.idJuego = idJuego;
	}

	public List<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(List<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public int getIdJugadorActual() {
		return idJugadorActual;
	}

	public void setIdJugadorActual(int idJugadorActual) {
		this.idJugadorActual = idJugadorActual;
	}

	public boolean isPartidaTerminada() {
		return partidaTerminada;
	}

	public void setPartidaTerminada(boolean partidaTerminada) {
		this.partidaTerminada = partidaTerminada;
	}

	public List<String> getMazo() {
		return mazo;
	}

	public void setMazo(List<String> mazo) {
		this.mazo = mazo;
	}

	public Map<String, Object> getUltimaJugada() {
		return ultimaJugada;
	}

	public void setUltimaJugada(Map<String, Object> ultimaJugada) {
		this.ultimaJugada = ultimaJugada;
	}

	// Devuelve el jugador al que le toca jugar
	public Jugador getJugadorActual() {
		if (jugadores.isEmpty())
			return null;
		return jugadores.get(idJugadorActual);
	}

	// Pasa el turno al siguiente jugador que no esté eliminado
	public void pasarTurno() {
		if (jugadores.isEmpty())
			return; // por si al pasar de turno, pasamos a un jugador que esta eliminado

		int total = jugadores.size();
		int contador = 0;

		do {
			idJugadorActual = (idJugadorActual + 1) % total;
			contador++;
		} while (jugadores.get(idJugadorActual).isEstaEliminado() && contador < total);
	}

	public Jugador getGanadorSiExiste() {
		Jugador ganador = null; // el ultimo ganador sera el que quede el unico en una partida
		int jugadoresActivos = 0; // cuantos no stan eliminados todavia

		for (Jugador j : jugadores) {
			if (!j.isEstaEliminado()) {
				jugadoresActivos++;
				ganador = j;
			}
		}

		if (jugadoresActivos == 1) { // si solo queda 1 se coge el ganador
			return ganador;
		} else {
			return null;
		}
	}

	public List<String> getCartasEnMesa() {
		return cartasMesa;
	}

	public List<String> getUltimasCartasTiradasFisicas() {
		return ultimasCartasTiradasFisicas;
	}

	public String getUltimaDeclaracion() {
		return ultimaDeclaracion;
	}

	public List<String> getCartasMesa() {
		return cartasMesa;
	}

	public void setCartasMesa(List<String> cartasMesa) {
		this.cartasMesa = cartasMesa;
	}

	public static String[] getSimbolo() {
		return SIMBOLO;
	}

	public static String[] getNumero() {
		return NUMERO;
	}

	public void setUltimasCartasTiradasFisicas(List<String> ultimasCartasTiradasFisicas) {
		this.ultimasCartasTiradasFisicas = ultimasCartasTiradasFisicas;
	}

	public void setUltimaDeclaracion(String ultimaDeclaracion) {
		this.ultimaDeclaracion = ultimaDeclaracion;
	}

	public int getTurnosCreador() {
		return turnosCreador;
	}

	public void sumarTurnosCreador() {
		turnosCreador++; // cuando el jugador tenga el id 0 se suma
	}

}