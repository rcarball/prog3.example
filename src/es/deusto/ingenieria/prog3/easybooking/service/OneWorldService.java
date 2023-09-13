package es.deusto.ingenieria.prog3.easybooking.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.deusto.ingenieria.prog3.easybooking.domain.AirAlliance;
import es.deusto.ingenieria.prog3.easybooking.domain.Airline;
import es.deusto.ingenieria.prog3.easybooking.domain.Airport;
import es.deusto.ingenieria.prog3.easybooking.domain.Flight;
import es.deusto.ingenieria.prog3.easybooking.domain.Plane;
import es.deusto.ingenieria.prog3.easybooking.domain.Reservation;

public class OneWorldService extends AirAllianceService {

	private static final String AIRLINES_FILE = "resources/data/airlines.csv";
	private static final String AIRPORTS_FILE = "resources/data/airports.csv";
	private static final String FLIGHTS_FILE = "resources/data/flights.csv";
	private static final String PLANES_FILE = "resources/data/planes.csv";
	private static final String RESERVATIONS_FILE = "resources/data/reservations.csv";	
	
	public OneWorldService() {
		super(AirAlliance.ONE_WORLD);		
	}
	
	@Override
	public Map<String, Flight> loadFlights() {
		//Estructuras auxiliares para el proceso de carga de loa vuelos
		Map<String, Airline> airlines = loadAirlinesCSV();
		Map<String, Airport> airports = loadAirportsCSV();
		Map<String, Plane> planes = loadPlanesCSV();
		Map<String, List<Reservation>> reservationsMap = loadReservationsCSV();
		//Se inicializa el mapa de vuelos
		flights = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE))) {
			String line = reader.readLine();
			String[] fields;
			Flight flight;

			while ((line = reader.readLine()) != null) {
				fields = line.split(",");
				
				if (airlines.get(fields[3]).getAlliance().equals(AirAlliance.ONE_WORLD)) {
					flight = new Flight(fields[0],				//Code
									airports.get(fields[1]),	//Departure 
									airports.get(fields[2]), 	//Arrival
									airlines.get(fields[3]),	//Airline
									planes.get(fields[5]), 		//Plane
									Integer.valueOf(fields[4]),	//Seats
						            Float.parseFloat(fields[6]));//Price
					//Se actualizan las reservas del vuelo, si hubiese alguna reserva previa.
					if (reservationsMap.containsKey(flight.getCode())) {				
						flight.setReservations(reservationsMap.get(flight.getCode()));
					}
					
					flights.put(flight.getCode(), flight);
				}
			}
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error al cargando vuelos: %s", getAirAlliance(), ex.getMessage()));
		}
		
		logger.info(String.format("%s - %d vuelos cargados correctamente", getAirAlliance(), flights.values().size()));
		
		return flights;
	}
	
	@Override
	public void storeReservation(Reservation reservation) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATIONS_FILE, true))) {
			String line = String.format("%s#%s#%s#%s", 
									reservation.getLocator(),
									reservation.getFlight().getCode(),
									String.valueOf(reservation.getDate()),
									String.join(";", reservation.getPassengers()));
			writer.write(line);
			writer.newLine();
        } catch (Exception ex) {
        	logger.warning(String.format("%s - Error guardando reserva: %s", getAirAlliance(), ex.getMessage()));
        }		
	}
	
	/**
	 * Devuelve un mapa que indexa las aerolíneas por código de aerolínea.
	 * @return Map<String, Airline> con las aerolíneas.
	 */
	private Map<String, Airline> loadAirlinesCSV() {
		Map<String, Airline> airlines = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(AIRLINES_FILE))) {
			String line = reader.readLine();
			Airline airline;
			while ((line = reader.readLine()) != null) {
				airline = Airline.parseCSV(line);
				airlines.put(airline.getCode(), airline);
			}
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error cargando aerolíneas: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return airlines;
	}

	/**
	 * Devuelve un mapa que indexa los aeropuertos por código de aeropuerto.
	 * @return Map<String, Airport> con los aeropuertos.
	 */
	private Map<String, Airport> loadAirportsCSV() {
		Map<String, Airport> airports = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(AIRPORTS_FILE))) {
			String line = reader.readLine();
			Airport airport;

			while ((line = reader.readLine()) != null) {
				airport = Airport.parseCSV(line);
				airports.put(airport.getCode(), airport);
			}
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error cargando aeropuertos: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return airports;
	}

	/**
	 * Devuelve un mapa que indexa los aviones por código de avión.
	 * @return Map<String, Planes> con los aviones.
	 */
	private Map<String, Plane> loadPlanesCSV() {
		Map<String, Plane> planes = new HashMap<>();
				
		try (BufferedReader reader = new BufferedReader(new FileReader(PLANES_FILE))) {
			String line = reader.readLine();
			Plane plane;

			while ((line = reader.readLine()) != null) {
				plane = Plane.parseCSV(line);
				planes.put(plane.getCode(), plane);
			}
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error cargando aviones: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return planes;
	}
	
	/**
	 * Devuelve un mapa que indexa la lista de reservas de un vuelo por el código de vuelo.
	 * @return Map<String, List<Reservation>> con las reservas.
	 */
	private Map<String, List<Reservation>> loadReservationsCSV() {
		Map<String, List<Reservation>> reservations = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(RESERVATIONS_FILE))) {
			String line = reader.readLine();
			Reservation reservation;

			while ((line = reader.readLine()) != null) {
				reservation = Reservation.parseCSV(line);
				reservations.putIfAbsent(reservation.getFlight().getCode(), new ArrayList<>());
				reservations.get(reservation.getFlight().getCode()).add(reservation);
			}
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error cargando reservas: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return reservations;
	}
}