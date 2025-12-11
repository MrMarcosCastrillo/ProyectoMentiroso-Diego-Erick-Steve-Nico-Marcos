package com.utad.mck.ProyectoMentiroso;

import java.util.List;

public class Jugada {

	private String idJugador;
	private String tipoMovimiento; // Carta, Pareja, Trio, etc.
	private List<String> valoresCartas; // Los valores de las cartas sin el palo

	public Jugada(String idJugador, String tipoMovimiento, List<String> valoresCartas) {
		this.idJugador = idJugador;
		this.tipoMovimiento = tipoMovimiento;
		this.valoresCartas = valoresCartas;
	}

	public String getIdJugador() {
		return idJugador;
	}

	public void setIdJugador(String idJugador) {
		this.idJugador = idJugador;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public List<String> getValoresCartas() {
		return valoresCartas;
	}

	public void setValoresCartas(List<String> valoresCartas) {
		this.valoresCartas = valoresCartas;
	}

	@Override
	public String toString() {
		return "Jugada [idJugador=" + idJugador + ", tipoMovimiento=" + tipoMovimiento + ", valoresCartas="
				+ valoresCartas + "]";
	}

}
