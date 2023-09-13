package es.deusto.ingenieria.prog3.easybooking.domain;

import java.util.Objects;

public class Airport implements Comparable<Airport> {
	
	private String code;
	private String name;
	private String city;
	private Country country;
	
	public Airport(String code, String name, String city, Country country) {
		this.code = code;
		this.name = name;
		this.city = city;
		this.country = country;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getCity() {
		return city;
	}

	public Country getCountry() {
		return country;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			return ((Airport) obj).code.equals(this.code);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("%s: %s, %s (%s)", code, name, city, country);
	}
	
	/**
	 * Crea un objeto Airport a partir de una cadena de texto separada por comas ",".
	 * @param data String con la cadena de texto separada por comas ",".
	 * @return Airport con el nuevo objeto creado.
	 * @throws Exception Si se produce un error al generar el obejto Airport.
	 */
	public static Airport parseCSV(String data) throws Exception {
		try {
			String[] fields = data.split(",");
			
			return new Airport(fields[0],
							   fields[1],
							   fields[2],
							   Country.valueOf(fields[3]));			
		} catch (Exception ex) {
			throw new Exception(String.format("%s from CSV error: %s",
											Airport.class, data));
		}
	}

	@Override
	public int compareTo(Airport o) {
		return this.getCode().compareTo(o.getCode());
	}
}