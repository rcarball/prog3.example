package es.deusto.ingenieria.prog3.easybooking.domain;

import java.util.Objects;

public class Plane {
	
	private String code;
	private String name;
	private int seats;
	
	public Plane(String code, String name, int seats) {
		this.code = code;
		this.name = name;
		this.seats = seats;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getSeats() {
		return seats;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			return ((Plane) obj).code.equals(this.code);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("%s: %s (%d seats)", code, name, seats);
	}
	
	/**
	 * Crea un objeto Plane a partir de una cadena de texto separada por comas ",".
	 * @param data String con la cadena de texto separada por comas ",".
	 * @return Plane con el nuevo objeto creado.
	 * @throws Exception Si se produce un error al generar el obejto Plane.
	 */
	public static Plane parseCSV(String data) throws Exception {
		try {
			String[] fields = data.split(",");
			
			return new Plane(fields[0], fields[1], Integer.valueOf(fields[2]));			
		} catch (Exception ex) {
			throw new Exception(String.format("%s from CSV error: %s",
											  Plane.class, data));
		}
	}
}