package com.utad.mck.ProyectoMentiroso;

import java.util.List;
import java.util.UUID;

public class Juego {

	private String idJuego;
	private List<Jugador> jugadores;
	private int idJugadorActual;
	private boolean partidaTerminada;
	private List<String> mazoRestante;

	public Juego(List<Jugador> jugadores, int idJugadorActual) {
		this.idJuego = UUID.randomUUID().toString(); // generar id random
		this.jugadores = jugadores;
		this.idJugadorActual = idJugadorActual;
		this.partidaTerminada = false; // al iniciar la partida esta sin terminar
		this.mazoRestante = mazoRestante;
	}

	public String getIdJuego() {
		return idJuego;
	}

	public void setIdJuego(String idJuego) {
		this.idJuego = idJuego;
	}

	public List<Jugador> getJugador() {
		return jugadores;
	}

	public void setJugador(List<Jugador> jugador) {
		this.jugadores = jugador;
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

	@Override
	public String toString() {
		return "Juego [idJuego=" + idJuego + ", jugador=" + jugadores + ", idJugadorActual=" + idJugadorActual
				+ ", partidaTerminada=" + partidaTerminada + "]";
	}

}
