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
	private List<Jugador> jugadores; // Se inicializará en el constructor
	private int idJugadorActual;
	private boolean partidaTerminada;
	private List<String> mazo; // Se inicializará en el constructor
	private Map<String, Object> ultimaJugada = new java.util.HashMap<>();

	// CONSTRUCTOR ÚNICO: Se encarga de dejar el juego listo
	public Juego() {
		this.idJuego = UUID.randomUUID().toString();
		this.jugadores = new ArrayList<>(); // ¡Crucial para evitar NullPointerException!
		this.mazo = new ArrayList<>(); // ¡Crucial para evitar NullPointerException!
		this.idJugadorActual = 0;
		this.partidaTerminada = false;

		inicializarMazo();
	}

	private void inicializarMazo() {
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
		for (int i = 0; i < cantidad; i++) {
			if (this.mazo != null && !this.mazo.isEmpty()) {
				mano.add(this.mazo.remove(0));
			}
		}
		return mano;
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
		return jugadores.get(idJugadorActual);
	}

	// Pasa el turno al siguiente jugador
	public void pasarTurno() {
		idJugadorActual = (idJugadorActual + 1) % jugadores.size();
	}

}