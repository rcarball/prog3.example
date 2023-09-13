package es.deusto.ingenieria.prog3.easybooking.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import es.deusto.ingenieria.prog3.easybooking.domain.Airline;
import es.deusto.ingenieria.prog3.easybooking.domain.Airport;

//TAREA 4.B: Modifica el renderer de la tabla de vuelos
//El valor de la nueva columna DISPONIBILIDAD debe renderizarse como una barra 
//de progreso (JProgressBar) de rango 0 a 100. El texto visualizado sobre la 
//barra de progreso será el valor de disponibilidad con un decimal y el símbolo 
//de “%” porcentaje (por ejemplo, para el vuelo IB4353 - BIO -> BCN la 
//disponibilidad es de 44,9 %).
public class FlightRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {		
		JLabel label = new JLabel();
		//El color de fondo es el color por defecto de la tabla
		label.setBackground(table.getBackground());
		//Por defecto el label se centra
		label.setHorizontalAlignment(JLabel.CENTER);
		
		//AEROLÍNEA se renderiza como un label con el logo de la aerolínea
		if (value.getClass().equals(Airline.class)) {
			label.setIcon(new ImageIcon(String.format("resources/images/%s.png", ((Airline)value).getCode())));
			label.setToolTipText(((Airline)value).getName());
		}

		//El VUELO, RESERVAS y ASIENTOS LIBRES se renderizan como texto ce trado
		if (column == 1 || column == 6 || column == 7) {
			label.setText(value.toString());
		}
		
		//ORIGEN y DESTINO se redenrizan con el código de aeropuerto como texto centrado
		if (value.getClass().equals(Airport.class)) {
			label.setText(((Airport)value).getCode());
			label.setToolTipText(((Airport)value).getName());
		}
					
		//DURACIÓN se alinea a la derecha y se añade "m."
		if (column == 4) {
			label.setText(String.format("%s m.", value.toString()));
			label.setHorizontalAlignment(JLabel.RIGHT);
		}
		
		//PRECIO se alinea a la derecha, redondea con 2 decimales, se añade "€"
		if (column == 5) {
			label.setText(String.format("%.2f €", value));
			label.setHorizontalAlignment(JLabel.RIGHT);
		}
						
		//Si la celda está seleccionada se usa el color por defecto de selección de la tabla
		if (isSelected) {
			label.setBackground(table.getSelectionBackground());
			label.setForeground(table.getSelectionForeground());
		}
		
		label.setOpaque(true);
		
		return label;
	}
}