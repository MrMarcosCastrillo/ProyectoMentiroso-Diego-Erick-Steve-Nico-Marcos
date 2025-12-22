package com.utad.mck.ProyectoMentiroso;

import java.util.Scanner;


public class cliente {
	static Scanner sc = new Scanner(System.in);

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
