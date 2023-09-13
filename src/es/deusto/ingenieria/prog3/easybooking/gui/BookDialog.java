package es.deusto.ingenieria.prog3.easybooking.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import es.deusto.ingenieria.prog3.easybooking.domain.Flight;

public class BookDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JSpinner jSpinnerSeats = new JSpinner();
	private JComboBox<String> jComboPassengers = new JComboBox<>();
	private JLabel jLabelAmount = new JLabel();
	private JButton jButtonConfirm = new JButton("Confirmar");
	private JButton jButtonCancel = new JButton("Cancelar");
		
	//Número de asientos de la reserva
	private int seats = 1;
	//Nombres de la personas
	private List<String> passengers = null;
	
	public BookDialog(Flight flight) {
		JPanel jPanelFlight = new JPanel();
		jPanelFlight.setBorder(new TitledBorder("Datos del vuelo"));
		jPanelFlight.setLayout(new GridLayout(5, 1));

		JLabel jLabelFlight = new JLabel(String.format("- %s", flight.getCode()));
		jLabelFlight.setIcon(new ImageIcon(String.format("resources/images/%s.png", flight.getAirline().getCode())));
		
		jPanelFlight.add(jLabelFlight);
		jPanelFlight.add(new JLabel(String.format("Origen: %s - %s", flight.getOrigin().getCode(), flight.getOrigin().getName())));
		jPanelFlight.add(new JLabel(String.format("Destino: %s - %s", flight.getDestination().getCode(), flight.getDestination().getName())));
		jPanelFlight.add(new JLabel(String.format("Duración: %d m.", flight.getDuration())));
		jPanelFlight.add(new JLabel(String.format("Precio: %.2f €", flight.getPrice())));
		
		JPanel jPanelPassengers = new JPanel();
		jPanelPassengers.setBorder(new TitledBorder("Datos personales"));
		jPanelPassengers.setLayout(new GridLayout(3, 1));
		
		int remainSeats = flight.getSeats()-flight.getReservations().size(); 	
		jSpinnerSeats = new JSpinner(new SpinnerNumberModel(1, 1, remainSeats, 1));				
		
		//Evento de cambio del valor del Spinner de número de pasajeros
		jSpinnerSeats.addChangeListener((e) -> {
			int newSeats = Integer.valueOf(((SpinnerNumberModel) jSpinnerSeats.getModel()).getValue().toString());
			
			//Se se ha cambiado el número de asientos, se modifica el combo de personas
			if (newSeats != seats) {
				if (newSeats > seats) {
					for (int i=seats+1; i<=newSeats; i++) {
						jComboPassengers.addItem(String.format("%d - ¿?", i));					
					}
				} else {
					for (int i=seats; i>newSeats; i--) {
						jComboPassengers.removeItemAt(i);
					}
				}
				
				seats = jComboPassengers.getItemCount()-1;
				
				//Se actualiza el label del importe total teniendo en cuenta el número de asientos
				jLabelAmount.setText(String.format("Importe total (%d x %.2f €): %.2f €",
												   seats,
												   flight.getPrice(),
												   (flight.getPrice()*seats)));
			}
		});
		
		jComboPassengers.addItem("- Indique los datos de la personas -");
		//Por defecto se inserta siempre una persona
		jComboPassengers.addItem("1 - ¿?");
		
		//Evento de selección de una persona
		jComboPassengers.addActionListener((e) -> {
			int position = ((JComboBox<?>) e.getSource()).getSelectedIndex();
			String passenger = ((JComboBox<?>) e.getSource()).getSelectedItem().toString();
			
			//Si se ha seleccionado una persona que no tiene datos personales
			if (passenger.contains("¿?")) {
				JTextField firstName = new JTextField();
				firstName.setColumns(30);
				JTextField lastName = new JTextField();
				lastName.setColumns(30);
				
				JComponent[] inputs = new JComponent[] {
					new JLabel("Nombre: "),
					firstName,
					new JLabel("Apellidos: "),
					lastName,
				};
				
				//Se muestra un cuadro de diálogo para introducir nombre y apellidos
				int result = JOptionPane.showConfirmDialog(this, inputs, 
									String.format("Datos de la persona - %s", passenger.substring(0, passenger.indexOf(" "))), 
									JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.PLAIN_MESSAGE, new ImageIcon("resources/images/passenger.png"));
				//Si se pulsa confirmar y los campos no están vacíos se actualiza el combo de personas
				if (result == JOptionPane.OK_OPTION && 
						(!firstName.getText().trim().isEmpty() || !lastName.getText().trim().isEmpty())) {
					String name;
				
					if (firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty()) {
						name = String.format("%s%s", lastName.getText().trim(), firstName.getText()).trim();
					} else {
						name = String.format("%s, %s", lastName.getText().trim(), firstName.getText()).trim();
					}
					
					//El nombre no puede contener el carácter "#"
					if (!name.contains("#")) {						
						name = String.format("%d - %s", position, name.toUpperCase());
						jComboPassengers.removeItemAt(position);
						jComboPassengers.insertItemAt(name, position);
						jComboPassengers.setSelectedIndex(position);
					}
				}
			}
		});	
		
		//Se actualiza el label del importe total teniendo en cuenta el número de asientos
		jLabelAmount.setText(String.format("Importe total (%d x %.2f €): %.2f €",
				seats,
				flight.getPrice(),
				(flight.getPrice()*seats)));
		
		JPanel jPanelSeats = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jPanelSeats.add(new JLabel(String.format("N.º billetes (1 - %d): ", remainSeats)));
		jPanelSeats.add(jSpinnerSeats);
		
		JPanel jPanelNames = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jPanelNames.add(new JLabel("Personas: "));
		jPanelNames.add(jComboPassengers);
		
		JPanel jPanelAmount = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jPanelAmount.add(jLabelAmount);
		
		jPanelPassengers.add(jPanelSeats);
		jPanelPassengers.add(jPanelNames);
		jPanelPassengers.add(jPanelAmount);
		
		//Eventos de los botones
		jButtonCancel.addActionListener((e) -> setVisible(false));
		jButtonConfirm.addActionListener((e) -> {
			updatePassengers();
			setVisible(false);
		});
		
		JPanel jPanelButtons = new JPanel();
		jPanelButtons.add(jButtonCancel);
		jPanelButtons.add(jButtonConfirm);
		
		JPanel jPanelCenter = new JPanel();
		jPanelCenter.setLayout(new GridLayout(2, 1));
		jPanelCenter.add(jPanelFlight);
		jPanelCenter.add(jPanelPassengers);
		
		//this.setLayout(new BorderLayout(10, 10));
		add(new JPanel(), BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.EAST);
		add(new JPanel(), BorderLayout.WEST);
		add(jPanelCenter, BorderLayout.CENTER);
		add(jPanelButtons, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle(String.format("Reserva del vuelo '%s'", flight.getCode()));		
		setIconImage(new ImageIcon("resources/images/tickets.png").getImage());		
		setSize(500, 350);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Se actualiza la lista de nombres de personas a partir del combo de personas.
	 */
	private void updatePassengers() {
		String item;
		
		passengers = new ArrayList<>();
		
		for (int i = 1; i <= seats; i++) {
			item = jComboPassengers.getItemAt(i);
			
			if (!item.contains("¿?")) {
				passengers.add(item.substring(item.indexOf("-")+2, item.length()));
			}
		}		
	}
	
	public List<String> getPassengers() {
		return passengers;
	}
}