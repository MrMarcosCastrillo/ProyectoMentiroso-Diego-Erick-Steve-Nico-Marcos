package com.utad.mck.ProyectoMentiroso;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProyectoMentirosoApplication {

	private Map<String, Juego> juegos = new HashMap<>(); // Mapa para guardar los juegos activos

	public static void main(String[] args) {
		SpringApplication.run(ProyectoMentirosoApplication.class, args);
	}

	@GetMapping("/juego/empezar") // endpoint
	public String empezarJuego(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/juego/{idJuego}/jugar") // endpoint
	public String juegarJuego(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/juego/{idJuego}/levantar") // endpoint
	public String levantarJugada(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/juego/{idJuego}/unirse") // endpoint
	public String unirseJuego(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

}