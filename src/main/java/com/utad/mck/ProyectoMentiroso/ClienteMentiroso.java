package com.utad.mck.ProyectoMentiroso;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ClienteMentiroso {

    static Scanner sc = new Scanner(System.in);
    
    // Dirección del servidor, localhost porque el servidor corre en el mismo ordenador
    static String servidor = "http://localhost:8080";

    public static void main(String[] args) {

        boolean seguir = true;

        System.out.println("-- CLIENTE JUEGO DEL MENTIROSO --");

        while (seguir) {

            System.out.println("\nMENU:");
            System.out.println("1. Crear partida");
            System.out.println("2. Unirse a partida");
            System.out.println("3. Jugar (prueba)");
            System.out.println("4. Levantar jugada (prueba)");
            System.out.println("5. Salir");

            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    crearPartida();
                    break;

                case 2:
                    unirsePartida();
                    break;

                case 3:
                    jugar();
                    break;

                case 4:
                    levantar();
                    break;

                case 5:
                    seguir = false;
                    System.out.println("Saliendo del cliente...");
                    break;
            }
        }

        sc.close();
    }

    // Metodos para aprovechar los endpoint

    public static void crearPartida() {
        System.out.print("Nombre del jugador: ");
        String nombre = sc.nextLine();

        String url = servidor + "/juego/empezar?nombre=" + nombre;
        llamarEndpoint(url);
    }

    public static void unirsePartida() {
        System.out.print("ID del juego: ");
        String idJuego = sc.nextLine();

        System.out.print("Nombre del jugador: ");
        String nombre = sc.nextLine();

        String url = servidor + "/juego/" + idJuego + "/unirse?nombre=" + nombre;
        llamarEndpoint(url);
    }

    public static void jugar() {
        System.out.print("ID del juego: ");
        String idJuego = sc.nextLine();

        System.out.print("Nombre del jugador: ");
        String nombre = sc.nextLine();

        String url = servidor + "/juego/" + idJuego + "/jugar?nombre=" + nombre;
        llamarEndpoint(url);
    }

    public static void levantar() {
        System.out.print("ID del juego: ");
        String idJuego = sc.nextLine();

        System.out.print("Nombre del jugador: ");
        String nombre = sc.nextLine();

        String url = servidor + "/juego/" + idJuego + "/levantar?nombre=" + nombre;
        llamarEndpoint(url);
    }

    //Clase para llamar Endpoint
    
    @SuppressWarnings("deprecation")
	public static void llamarEndpoint(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String linea;
            System.out.println("\nRespuesta del servidor:");

            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error al conectar con el servidor");
        }
    }
}
