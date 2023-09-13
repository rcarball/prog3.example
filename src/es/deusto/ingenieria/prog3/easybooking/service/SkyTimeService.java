package es.deusto.ingenieria.prog3.easybooking.service;

import java.util.HashMap;
import java.util.Map;

import es.deusto.ingenieria.prog3.easybooking.domain.AirAlliance;
import es.deusto.ingenieria.prog3.easybooking.domain.Flight;
import es.deusto.ingenieria.prog3.easybooking.domain.Reservation;

public class SkyTimeService extends AirAllianceService {
	
	public SkyTimeService() {
		super(AirAlliance.SKY_TEAM);
	}
	
	//TAREA 1.A: FICHEROS: Carga los vuelos desde un archivo usando serialización nativa.
	//Introduce el código para leer el mapa de vuelos desde el fichero "resources/data/SKY_TEAM.dat".
	public Map<String, Flight> loadFlights() {
		Map<String, Flight> flights = new HashMap<>();
				
		return flights;
	}	
		
	//TAREA 1.B: FICHEROS: Almacena una reserva en un archivo usando serialización nativa.
	//Introduce el código para guardar el mapa de vuelos en el fichero "resources/data/SKY_TEAM.dat".
	@Override
	public void storeReservation(Reservation reservation) {		

	}
}