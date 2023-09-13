package es.deusto.ingenieria.prog3.easybooking.gui;

import java.util.Arrays;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import es.deusto.ingenieria.prog3.easybooking.domain.Flight;

//TAREA 4.B: Modifica el renderer de la tabla de vuelos
//El valor de la nueva columna DISPONIBILIDAD debe renderizarse como una barra 
//de progreso (JProgressBar) de rango 0 a 100. El texto visualizado sobre la 
//barra de progreso será el valor de disponibilidad con un decimal y el símbolo 
//de “%” porcentaje (por ejemplo, para el vuelo IB4353 - BIO -> BCN la 
//disponibilidad es de 44,9 %).
public class FlightsTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	
	private List<Flight> flights;
	private final List<String> headers = Arrays.asList(
			"AEROLÍNEA", 
			"VUELO", 
			"ORIGEN", 
			"DESTINO", 
			"DURACIÓN", 
			"PRECIO", 
			"RESERVAS",
			"ASIENTOS LIBRES",
			"RESERVAR");

	public FlightsTableModel(List<Flight> flights) {
		this.flights = flights;
	}
	
	@Override
	public String getColumnName(int column) {
		return headers.get(column);
	}

	@Override
	public int getRowCount() {
		if (flights != null) {
			return flights.size();
		} else { 
			return 0;
		}
	}

	@Override
	public int getColumnCount() {
		return headers.size(); 
	}
	
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 8);
    }
    
    @Override
    public void setValueAt(Object aValue, int row, int column) {    	
    }
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Flight flight = flights.get(rowIndex);
		
		switch (columnIndex) {
			case 0: return flight.getAirline();
			case 1: return flight.getCode();
			case 2: return flight.getOrigin();
			case 3: return flight.getDestination();
			case 4: return Integer.valueOf(flight.getDuration());
			case 5: return Float.valueOf(flight.getPrice());
			case 6: return Integer.valueOf(flight.getReservations().size());
			case 7: return Integer.valueOf(flight.getRemainingSeats());
			case 8: return flight;
			default: return null;
		}
	}
}