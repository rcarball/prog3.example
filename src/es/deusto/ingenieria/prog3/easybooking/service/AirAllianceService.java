package es.deusto.ingenieria.prog3.easybooking.service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import es.deusto.ingenieria.prog3.easybooking.domain.AirAlliance;
import es.deusto.ingenieria.prog3.easybooking.domain.Airport;
import es.deusto.ingenieria.prog3.easybooking.domain.Flight;
import es.deusto.ingenieria.prog3.easybooking.domain.Reservation;

public abstract class AirAllianceService {

	//Atributos utilizadas para generar el localizador de las reservas
	private static final String LOCATOR_ALPHABET = "234679CDFGHJKMNPRTWXYZ";
	private static final Short LOCATOR_LENGTH = 6;
	private static final Random RANDOM = new Random();
	private static List<String> locators = new ArrayList<>();
	
	protected Logger logger = Logger.getLogger(OneWorldService.class.getName());
	
	//Alianza de compañías aéreas del servicio
	private AirAlliance airAlliance;
	//Mapa que indexa los vuelos por su código
	protected Map<String, Flight> flights = new HashMap<>();
	//Mapa que indexa los aeropuertos por su código
	protected Map<String, Airport> airports = new HashMap<>();
	
	/**
	 * Constructor de la clase
	 */
	public AirAllianceService(AirAlliance airAlliance) {
		try (FileInputStream fis = new FileInputStream("conf/logger.properties")) {
			LogManager.getLogManager().readConfiguration(fis);
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error leyendo configuración del Logger: %s", getAirAlliance(), ex.getMessage()));
		}
		
		this.airAlliance = airAlliance;

		//Se cargan los mapas de vuelos y aeropuértos
		flights = loadFlights();
		airports = initAirports();
	}
	
	/**
	 * Crea un mapa que indexa los vuelos por el código de vuelo.
	 * @return Map<String, Flight> con los vuelos.
	 */
	public abstract Map<String, Flight> loadFlights();
	

	/**
	 * Almacena una reserva en el respositorio de información.
	 * @param reservation Reservation con la reserva.
	 */
	public abstract void storeReservation(Reservation reservation);	

	/**
	 * Realiza una reserva de un vuelo para uno o varias personas.
	 * @param flight String con el código del vuelo.
	 * @param passengers List<String> con los nombres de las personas.
	 * @return String con el localizador de la reserva.
	 */
	public String book(String flight, List<String> passengers) {
		Flight flightObject = flights.get(flight);
		String locator = generateLocator();		
		Reservation reservation = new Reservation(locator, flightObject, System.currentTimeMillis(), passengers);			
		flightObject.addReservation(reservation);
		storeReservation(reservation);		
		logger.info(String.format("%s - Nueva reserva: %s", getAirAlliance(), reservation));
		
		return reservation.getLocator();		
	}
		
	/**
	 * Crea un mapa que indexa los aeropuertos por el código de aeropuerto.
	 * @return Map<String, Airport> con los aeropuertos.
	 */
	protected Map<String, Airport> initAirports() {
		Map<String, Airport> airports = new HashMap<>();
		
		for (Flight flight : flights.values()) {
			airports.putIfAbsent(flight.getDestination().getCode(), flight.getDestination());
			airports.putIfAbsent(flight.getOrigin().getCode(), flight.getOrigin());
		}
		
		return airports;
	}
	
	/**
	 * Genera el localizador para una reserva. El localizador es una secuencia
	 * alfanumérica de 6 caracteres generada de forma aleatoria.
	 * @return String con el localizador de la reserva.
	 */
	private static final String generateLocator() {
		StringBuffer buffer;
				
		do {
			buffer = new StringBuffer();
			//Se realizar una selección aleatoria de 6 caracteres	
			for (int i=0; i<LOCATOR_LENGTH; i++) {
				buffer.append(LOCATOR_ALPHABET.charAt(RANDOM.nextInt(LOCATOR_ALPHABET.length())));
			}
		//Si no está repetido el localizador, el proceso se detiene
		} while (locators.contains(buffer.toString()));
		
		return buffer.toString();		
	}
	
	/**
	 * Devuelve los aeropuertos origen de todos los vuelos gestionados por la alianza.
	 * @return List<String> con los aeropuertos origen.
	 */
	public List<Airport> getOrigins() {
		Set<Airport> airports = new HashSet<>();		

		flights.values().forEach(f -> airports.add(f.getOrigin()));
		
		return new ArrayList<>(airports);
	}
	
	/**
	 * Devuelve los aeropuertos destino dado un aeropuerto origen.
	 * @param origin String con el código del aeropuerto origen.
	 * @return List<String> con los aeropuertos destino.
	 */
	public List<Airport> getDestinations(String origin) {
		Airport dAirport = airports.get(origin);
		Set<Airport> airports = new HashSet<>();		

		flights.values().forEach(f -> {
			if (f.getOrigin().equals(dAirport)) {
				airports.add(f.getDestination());
			}
		});
		
		return new ArrayList<>(airports);
	}
	
	/**
	 * Devuelve la lista de vuelos entre aeropuerto origen y destino.
	 * @param origin String con el código del aeropuerto origen.
	 * @param destination String con el código del aeropuerto destino.
	 * @return List<Flight> con la lista de los vuelos obtenidos por la búsqueda.
	 */
	public List<Flight> search(String origin, String destination) {
		List<Flight> result = new ArrayList<>();
		
		Airport dAirport = airports.get(origin);
		Airport aAirport = airports.get(destination);
		
		flights.values().forEach(f -> {
			if (f.getOrigin().equals(dAirport) && f.getDestination().equals(aAirport)) {
				result.add(f);
			}
		});
		
		return result;
	}
		
	/**
	 * Devuelve el AirAlliance del servicio.
	 * @return AirAlliance del servicio.
	 */
	public AirAlliance getAirAlliance() {
		return airAlliance;
	}
	
	/**
	 * Devuelve una lista con todos los vuelos.
	 * @return List<Flight> con la lista vuelos.
	 */
	public List<Flight> getAllFlights() {
		return new ArrayList<>(flights.values());
	}
}