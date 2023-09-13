package es.deusto.ingenieria.prog3.easybooking.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Airline {
	
	private String code;
	private String name;
	private Country country;
	private AirAlliance alliance;
	private List<Flight> flights;
	
	public Airline(String code, String name, Country country, AirAlliance alliance) {
		this.code = code;
		this.name = name;
		this.country = country;
		this.alliance = alliance;
		this.flights = new ArrayList<>();
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public Country getCountry() {
		return country;
	}
	
	public AirAlliance getAlliance() {
		return alliance;
	}
	
	public List<Flight> getFlights() {
		return flights;
	}

	public void addFlight(Flight flight) {
		if (flight !=null && !flights.contains(flight)) {
			flights.add(flight);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			return ((Airline) obj).code.equals(code);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("%s: %s [%s] (%s)", code, name, alliance, country);
	}
	
	/**
	 * Crea un objeto Airline a partir de una cadena de texto separada por comas ",".
	 * @param data String con la cadena de texto separada por comas ",".
	 * @return Airline con el nuevo objeto creado.
	 * @throws Exception Si se produce un error al generar el obejto Airline.
	 */
	public static Airline parseCSV(String data) throws Exception {
		try {
			String[] fields = data.split(",");
			
			return new Airline(fields[0], 
							   fields[1],
							   Country.valueOf(fields[2]),
							   AirAlliance.valueOf(fields[3]));			
		} catch (Exception ex) {
			throw new Exception(String.format("%s from CSV error: %s",
											Airline.class, data));
		}
	}
}