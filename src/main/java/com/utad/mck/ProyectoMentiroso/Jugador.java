package com.utad.mck.ProyectoMentiroso;

import java.util.List;

public class Jugador {

	private String idJugador;
	private String nombre;
	private List<String> cartas; // la lista de las cartas que tiene en las manos

	public Jugador(String idJugador, String nombre, List<String> cartas, boolean estaEliminado) {
		this.idJugador = idJugador;
		this.nombre = nombre;
		this.cartas = cartas;
	}

	public String getIdJugador() {
		return idJugador;
	}

	public void setIdJugador(String idJugador) {
		this.idJugador = idJugador;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<String> getCartas() {
		return cartas;
	}

	public void setCartas(List<String> cartas) {
		this.cartas = cartas;
	}

	@Override
	public String toString() {
		return "Jugador [idJugador=" + idJugador + ", nombre=" + nombre + ", cartas=" + cartas + "]";
	}

}
