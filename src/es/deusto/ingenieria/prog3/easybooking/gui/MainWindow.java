package es.deusto.ingenieria.prog3.easybooking.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import es.deusto.ingenieria.prog3.easybooking.domain.Airport;
import es.deusto.ingenieria.prog3.easybooking.domain.Flight;
import es.deusto.ingenieria.prog3.easybooking.service.AirAllianceService;
import es.deusto.ingenieria.prog3.easybooking.service.OneWorldService;
import es.deusto.ingenieria.prog3.easybooking.service.SkyTimeService;
import es.deusto.ingenieria.prog3.easybooking.service.StarAllianceService;


public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//Servicios de aerolíneas
	private List<AirAllianceService> airAllianceServices = new ArrayList<>();
	
	//Lista de vuelos que se está visualizando en la ventana
	private List<Flight> flights = new ArrayList<>();
	
	//JTable de vuelos
	private JTable jTableFlights = new JTable();	
	//JLabel para mensajes de información
	private JLabel jLabelInfo = new JLabel("Selecciona un aeropuerto origen");
	//JCombos de Origen y Destino
	private JComboBox<String> jComboOrigin = new JComboBox<>();
	private JComboBox<String> jComboDestination = new JComboBox<>();
	private JButton jBtnRecursiveSearch = new JButton("Búsqueda Recursiva (max. 2 escalas)");
	
	public MainWindow() {
		//Se inicializan los servicios de las aerolíneas
		airAllianceServices.add(new OneWorldService());
		airAllianceServices.add(new SkyTimeService());
		airAllianceServices.add(new StarAllianceService());
		
		//Ajuste el JComboBox de aeropuertos origen
		jComboOrigin.setPrototypeDisplayValue("Seleccione el nombre del aeropuerto origen");		
		jComboOrigin.addActionListener((e) -> {
			Object fromItem = ((JComboBox<?>) e.getSource()).getSelectedItem();
			flights = new ArrayList<>();
			
			if (fromItem != null && !fromItem.toString().isEmpty()) {
				final String origin = fromItem.toString().substring(0, fromItem.toString().indexOf(" - "));
				
				if (!origin.isEmpty()) {
					Set<Airport> destinations = new HashSet<>();					
					airAllianceServices.forEach(a -> destinations.addAll(a.getDestinations(origin)));					
					updateDestinations(new ArrayList<Airport>(destinations));
				} else {
					jComboDestination.removeAllItems();
				}								
			}
			
			updateFlights();
			jLabelInfo.setText("Selecciona un aeropuerto origen");
		});
		
		//Recuperación de todos los aeropuertos origen de las alianzas de aerolíneas
		Set<Airport> origins = new HashSet<>();
		airAllianceServices.forEach(a -> origins.addAll(a.getOrigins()));
		updateOrigins(new ArrayList<Airport>(origins));

		//Inicialización el JComboBox de aeropuertos destino
		jComboDestination.setPrototypeDisplayValue("Seleccione el nombre del aeropuerto destino");
		jComboDestination.addActionListener((e) -> {
			Object toItem = ((JComboBox<?>) e.getSource()).getSelectedItem();
			flights = new ArrayList<>();
			
			if (toItem != null && !toItem.toString().isEmpty()) {
				final String destination = toItem.toString().substring(0, toItem.toString().indexOf(" - "));
				
				if (!destination.isEmpty() && jComboOrigin.getSelectedIndex() > 0) {
					Object fromItem = jComboOrigin.getSelectedItem();
					final String origin = fromItem.toString().substring(0, fromItem.toString().indexOf(" - "));
					airAllianceServices.forEach(a -> flights.addAll(a.search(origin, destination)));				
				}
			}
			
			updateFlights();
		});		

		//Inicialización del botón de búsqueda recursiva
		jBtnRecursiveSearch.setEnabled(false);
		jBtnRecursiveSearch.addActionListener((e) -> {
			Object item = jComboOrigin.getSelectedItem();
			String origin = item.toString().substring(0, item.toString().indexOf(" - "));
			item = jComboDestination.getSelectedItem();
			String destination = item.toString().substring(0, item.toString().indexOf(" - "));

			List<List<Flight>> flightsList = new ArrayList<>();
			
			//Se realiza la búsqueda recursiva de vuelos en un hilo independiente
			new Thread(() -> {
				//Se invoca al método que realiza la búsqueda recursiva
				flightsList.addAll(recursiveSearch(origin, destination));
				//Los itinerarios se muestran en un JTree dento de un cuadro de diálogo
				DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(String.format("%02d Itinerarios", flightsList.size()));
				DefaultMutableTreeNode itineraryNode;
				
				float price = 0;
				int duration = 0;
				
				for (int i=0; i<flightsList.size(); i++) {
					itineraryNode = new DefaultMutableTreeNode(); 
					rootNode.add(itineraryNode);
					
					for (Flight f : flightsList.get(i)) {
						itineraryNode.add(new DefaultMutableTreeNode(f));
						duration += f.getDuration();
						price += f.getPrice();						
					}					
					
					itineraryNode.setUserObject(String.format("%2d vuelos, %2d min., %.2f €", 
							flightsList.get(i).size(),
							duration,
							price));
				}
				
				JScrollPane scrollPane = new JScrollPane(new JTree(rootNode));
				scrollPane.setPreferredSize(new Dimension(600, 300));
				
				JOptionPane.showMessageDialog(this, 
						scrollPane, 
						String.format("Itinerarios entre %s y %s", origin, destination), 
						JOptionPane.PLAIN_MESSAGE, 
						new ImageIcon("resources/images/confirm.png"));		    
			}).start();	//Se activa el hilo que realiza la búsqueda recursiva						
		});
		
		//Inicialización de la tabla de vuelos
		jTableFlights.setRowHeight(30);
		jTableFlights.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		((DefaultTableCellRenderer) jTableFlights.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		//Inicialización del label de información
		jLabelInfo.setHorizontalAlignment(JLabel.RIGHT);
		
		//Distribución de los elementos en el JFrame
		JPanel pOrigin = new JPanel();		
		pOrigin.add(new JLabel("Origen: "));		
		pOrigin.add(this.jComboOrigin);

		JPanel pDestination = new JPanel();
		pDestination.add(new JLabel("Destino: "));
		pDestination.add(jComboDestination);
		
		JPanel pRecursion = new JPanel();
		pRecursion.add(jBtnRecursiveSearch);
		
		JPanel pSearch = new JPanel();
		pSearch.setBorder(new TitledBorder("Búsqueda de vuelos"));
		pSearch.setLayout(new GridLayout(3, 1));
		pSearch.add(pOrigin);
		pSearch.add(pDestination);		
		pSearch.add(pRecursion);
						
		add(pSearch, BorderLayout.NORTH);
		add(new JScrollPane(jTableFlights), BorderLayout.CENTER);
		add(jLabelInfo, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Easy Booking");
		setIconImage(new ImageIcon("resources/images/logo.png").getImage());		
		setSize(1200, 600);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Devuelve una lista de itinerario (lista de vuelos) entre un aeropuerto origen y
	 * un aeropuerto destino con un máximo de 2 escalas.
	 * @param origin String con el código del aeropuerto origen.
	 * @param destination String con el código de aeropuerto destino.
	 * @return List<List<Flight>> lista con los itinearios entre origen y destino.
	 */
	private List<List<Flight>> recursiveSearch(String origin, String destination) {
		//Se recuperan todos los vuelos 
		List<Flight> allFlights = new ArrayList<>();
		airAllianceServices.forEach(s -> allFlights.addAll(s.getAllFlights()));
		//Se invoca al método que realiza la búsqueda recursiva
		return recursiveSearch(origin, destination, allFlights, 2);
	}
		
	//TAREA 5: Crea un método recursivo que dados los códigos de aeropuerto 
	//origen y destino busque todos los itinerarios (secuencias de uno o varios 
	//vuelos) con un máximo de 2 escalas.
	
	//El método debe ir generando combinaciones de secuencias de vuelos 
	//encadenados con un máximo de 3 vuelos en los que el aeropuerto origen del 
	//primer vuelo y el aeropuerto destino del último vuelo coinciden con la 
	//selección realizada en las listas desplegables de aeropuerto origen y 
	//aeropuerto destino de la ventana principal. Comprueba que un mismo vuelo
	//no aparezca más de una vez en cada itinerario y que no se repitan los itinerarios.
	
	/**
	 * Devuelve una lista de itinerarios entre un origen y un destino. La generación
	 * de la lista de itinerarios se realiza con un proceso recursivo.
	 * @param origin String con el código del aeropuerto origen.
	 * @param destination String con el código del aeropuerto destino.
	 * @param flights List<Flight> con la lista de vuelos usados como referencia.
	 * @param max int con el número máximo de escalas.
	 * @return List<List<Flight>> con la lista de itinerarios de vuelos.
	 */
	private List<List<Flight>> recursiveSearch(String origin, String destination,
											   List<Flight> flights, int max) {                
        List<List<Flight>> result = new ArrayList<>();

        //Puedes implementar aquí directamente el proceso recursivo o utilizar algún método adicional
        
        return result;
    }
		
	/**
	 * Actualiza el JComboBos de aeropuertos origen.
	 * @param airports List<Airport> con los aeropuertos.
	 */
	private void updateOrigins(List<Airport> airports) {		
		this.jComboOrigin.removeAllItems();
		this.jComboOrigin.addItem("");		
		Collections.sort(airports);
		airports.forEach(a -> jComboOrigin.addItem(String.format("%s - %s (%s)", 
				a.getCode(), a.getName(), a.getCountry().getName())));		
	}

	/**
	 * Actualiza el JComboBos de aeropuertos destino.
	 * @param airports List<Airport> con los aeropuertos.
	 */
	private void updateDestinations(List<Airport> airports) {		
		this.jComboDestination.removeAllItems();
		this.jComboDestination.addItem("");
		Collections.sort(airports);
		airports.forEach(a -> jComboDestination.addItem(String.format("%s - %s (%s)", 
				a.getCode(), a.getName(), a.getCountry().getName())));		
	}
		
	/**
	 * Actualiza la JTable de vuelos.
	 */
	public void updateFlights() {
		jTableFlights.setModel(new FlightsTableModel(flights));	
		
		//Se define el render para todas las columnas de la tabla excepto la última
		FlightRenderer defaultRenderer = new FlightRenderer();
		
		for (int i=0; i<jTableFlights.getColumnModel().getColumnCount()-1; i++) {
			jTableFlights.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
		}

		//Se define el render y editor para la última columna de la tabla
		int lastColumn = jTableFlights.getColumnModel().getColumnCount()-1;
		
		jTableFlights.getColumnModel().getColumn(lastColumn).setCellRenderer(new BookRendererEditor(this));
		jTableFlights.getColumnModel().getColumn(lastColumn).setCellEditor(new BookRendererEditor(this));		
		
		jLabelInfo.setText(String.format("%d vuelos", flights.size()));
		
		if (flights.isEmpty()) {
			jBtnRecursiveSearch.setEnabled(false);
		} else {
			jBtnRecursiveSearch.setEnabled(true);
		}
	}
	
	/**
	 * Devuelve el servicio de la alianaza de aerolíneas que gestiona el vuelo.
	 * @param flight Flight con el vuelo.
	 * @return AirAllianceService con el servicio de la alianza de aerolíneas.
	 */
	protected AirAllianceService getService(Flight flight) {
		for (AirAllianceService service : airAllianceServices) {
			if (service.getAirAlliance().equals(flight.getAirline().getAlliance())) {
				return service;
			}
		}
		
		return null;
	}
	
	/**
	 * Método main
	 * @param args String[] con los argumentos.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MainWindow());
	}
}