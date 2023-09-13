package es.deusto.ingenieria.prog3.easybooking.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.deusto.ingenieria.prog3.easybooking.domain.AirAlliance;
import es.deusto.ingenieria.prog3.easybooking.domain.Airline;
import es.deusto.ingenieria.prog3.easybooking.domain.Airport;
import es.deusto.ingenieria.prog3.easybooking.domain.Country;
import es.deusto.ingenieria.prog3.easybooking.domain.Flight;
import es.deusto.ingenieria.prog3.easybooking.domain.Plane;
import es.deusto.ingenieria.prog3.easybooking.domain.Reservation;

public class StarAllianceService extends AirAllianceService {

	//Driver de la BBDD
	private static final String DRIVER = "org.sqlite.JDBC";
	//Cadena de conexión a la BBDD
	private static final String CONNECTION_STRING = String.format("jdbc:sqlite:resources/db/%s.db", AirAlliance.STAR_ALLIANCE);
	//Conexión a la BBDD
	private Connection conn;

	public StarAllianceService() {
		super(AirAlliance.STAR_ALLIANCE);
	}
	
	/**
	 * Establece la conexión con la BBDD.
	 */
	protected void connectDB() {
		try {			
			Class.forName(DRIVER);			
			conn = DriverManager.getConnection(CONNECTION_STRING);			
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error conectando con la BBDD: %s", getAirAlliance(), ex.getMessage()));
		}
	}
	/**
	 * Cierra la conexión con la BBDD.
	 */		
	public void disconnectDB() {
		try {			
			conn.close();			
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error cerrando conexión con la BBDD: %s", getAirAlliance(), ex.getMessage()));
		}
	}
	
	//TAREA 2.A - BASE DE DATOS: Carga los vuelos desde la BBDD.
	//Introduce aquí el código para inicializar el mapa de vuelos llamado "flights".
	//Para implementar este método debes utilizar los métodos loadXXXBD() que recuperan de
	//la BBDD la información de los aviones, las aerolíneas, los aeropuertos y las reservas.
	@Override
	public Map<String, Flight> loadFlights() {		
		//Se establece la conexión con la BBDD
		connectDB();
		//Se inicializa el mapa de vuelos
		flights = new HashMap<>();
				
		return flights;
	}
	
	//TAREA 2.B - BASE DE DATOS: Guardar una reserva en la BBDD.
	//Introduce aquí el código almcenarla una reserva en la BBDD. Los datos de las personas
	//se alacemanan todos juntos como un único String separado por ";".
	@Override
	public void storeReservation(Reservation reservation) {

	}
		
	/**
	 * Devuelve un mapa que indexa las aerolíneas por código de aerolínea.
	 * @return Map<String, Airline> con las aerolíneas.
	 */
	protected Map<String, Airline> loadAirlinesDB() {
		Map<String, Airline> airline = new HashMap<>();	
		
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT * FROM Airline;")) {
			
			while (rs.next()) {
				airline.put(rs.getString("CODE"), 
						new Airline(rs.getString("CODE"), 
								    rs.getString("NAME"),
								    Country.valueOf(rs.getString("COUNTRY")),
								    AirAlliance.valueOf(rs.getString("ALLIANCE")))
				);
			}			
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error recuperando aerolíneas: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return airline;
	}
	
	/**
	 * Devuelve un mapa que indexa los aeropuertos por código de aeropuerto.
	 * @return Map<String, Airport> con los aeropuertos.
	 */
	protected Map<String, Airport> loadAirportsDB() {
		Map<String, Airport> airports = new HashMap<>();	
		
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT * FROM Airport;")) {
			
			while (rs.next()) {
				airports.put(rs.getString("CODE"), 
						new Airport(rs.getString("CODE"), 
								    rs.getString("NAME"),
								    rs.getString("CITY"),
								    Country.valueOf(rs.getString("COUNTRY")))
				);
			}			
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error recuperando aeropuertos: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return airports;
	}
	
	/**
	 * Devuelve un mapa que indexa los aviones por código de avión.
	 * @return Map<String, Plane> con los vuelos.
	 */
	protected Map<String, Plane> loadPlanesDB() {
		Map<String, Plane> planes = new HashMap<>();	
		
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT * FROM Plane;")) {
			
			while (rs.next()) {
				planes.put(rs.getString("CODE"), 
						new Plane(rs.getString("CODE"), 
								  rs.getString("NAME"),
								  rs.getInt("SEATS"))
				);
			}			
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error recuperando aviones: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return planes;
	}
	
	/**
	 * Devuelve un mapa que indexa la lista de reservas de un vuelo por el código de vuelo.
	 * @return Map<String, List<Reservation>> con las reservas.
	 */
	protected Map<String, List<Reservation>> loadReservationsDB() {
		Map<String, List<Reservation>> reservations = new HashMap<>();	
		
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT * FROM Reservation;")) {
			
			while (rs.next()) {
				reservations.putIfAbsent(rs.getString("FLIGHT"), new ArrayList<>());
				reservations.get(rs.getString("FLIGHT")).add(new Reservation(rs.getString("LOCATOR"), 
																			 flights.get(rs.getString("FLIGHT")),
																			 Long.valueOf(rs.getString("DATE")),
																			 Arrays.asList(rs.getString("PASSENGERS").split(";")))
				);
			}			
		} catch (Exception ex) {
			logger.warning(String.format("%s - Error recuperando reservas: %s", getAirAlliance(), ex.getMessage()));
		}
		
		return reservations;
	}
}