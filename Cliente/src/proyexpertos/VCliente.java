package proyexpertos;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VCliente extends JFrame
{
    private JTextField campoIntroducir; // introduce la información del usuario
    private JTextArea areaPantalla; // muestra la información al usuario
    private JLabel ticket;
    private int dioEnter = 0; // Bandera para saber si ya se dio enter en el cuadro de entrada
    private int opcion; // Opcion elegida del menú
    private Font fuente = new Font( "Century", Font.BOLD, 14 );
    ImageIcon img;
    private int cerrar = 0;
    private String mensajeEnviar = "NadaSSS";
    private int enviaMsj = 0; // 0 no manda
    
    
    private ObjectOutputStream salida; // flujo de salida hacia el servidor
    private ObjectInputStream entrada; // flujo de entrada del servidor
    private String mensaje = ""; // mensaje del servidor
    private String servidorChat; // aloja al servidor para esta aplicación
    private Socket cliente; // socket para comunicarse con el servidor
    private ArrayList mensajeString = new ArrayList();  // guarda el mensaje a enviar al servidor
    
    public VCliente()
    {
        //servidorChat = "192.168.1.2";148.215.12.222
        //servidorChat = "192.168.100.10";
        servidorChat = "169.254.193.180";
        //servidorChat = "127.0.0.1";
        //servidorChat = "192.168.43.239";
        
        campoIntroducir = new JTextField(); // crea objeto campoIntroducir
        campoIntroducir.setEditable(true);
        campoIntroducir.setFont(fuente);
        campoIntroducir.addActionListener(
                new ActionListener() {
                    // envía el mensaje al servidor
                    public void actionPerformed(ActionEvent evento)
                    {
                        opcion = Integer.parseInt(campoIntroducir.getText());
                        mostrarMensaje( campoIntroducir.getText() );
                        campoIntroducir.setText("");
                        setDioEnter(1);
                    } // fin del método actionPerformed
                } // fin de la clase interna anónima
        ); // fin de la llamada a addActionListener
        add(campoIntroducir, BorderLayout.NORTH);

        areaPantalla = new JTextArea(); // crea objeto areaPantalla
        areaPantalla.setFont(fuente);
        add(new JScrollPane(areaPantalla), BorderLayout.CENTER);
		areaPantalla.setEditable(false);
        
        img = new ImageIcon( this.getClass().getResource("/imagenes/farmacia.jpg") );
		img = new ImageIcon( img.getImage().getScaledInstance( 200, 500, java.awt.Image.SCALE_DEFAULT ));
        
        ticket = new JLabel();// " Total = $000 " );
        //ticket.setIcon(img);
        ticket.setFont(fuente);
        ticket.setSize( 100, 100);
        add(ticket,BorderLayout.WEST);
    }
    
    public class HiloV1 extends Thread
    {
        @Override
        public void run()
        {
            try // se conecta al servidor, obtiene flujos, procesa la conexión
            {
                conectarAlServidor(); // crea un objeto Socket para hacer la conexión
                obtenerFlujos(); // obtiene los flujos de entrada y salida
                procesarConexion(); // procesa la conexión
            } // fin de try
            catch (EOFException excepcionEOF) {
                System.out.println("Cliente terminó la conexion");
            } // fin de catch
            catch (IOException excepcionES) {
                excepcionES.printStackTrace();
            } // fin de catch
            finally {
                cerrarConexion(); // cierra la conexión
            } // fin de finally
        }
    }
    
    
    public void ejecutarCliente() {
        HiloV1 hiloV1 = new HiloV1();
        hiloV1.start();
        
    } // fin del método ejecutarCliente
    
    public void setMensaje( String msj )
    {
        mensajeEnviar = msj;
    }
    
    public void setEnviaMsj( int valor )
    {
        enviaMsj = valor;
    }
    
    
    private void obtenerFlujos() throws IOException {
        // establece flujo de salida para los objetos
        salida = new ObjectOutputStream(cliente.getOutputStream());
        salida.flush(); // vacía el búfer de salida para enviar información de encabezado
        // establece flujo de entrada para los objetos
        entrada = new ObjectInputStream(cliente.getInputStream());
        System.out.println("Se obtuvieron los flujos de E/S\n");
    } // fin del método obtenerFlujos
    
    
    
    private void conectarAlServidor() throws IOException {
        System.out.println("Intentando realizar conexion\n");
        // crea objeto Socket para hacer conexión con el servidor
        cliente = new Socket(InetAddress.getByName(servidorChat), 12345);
        // muestra la información de la conexión
        System.out.println("Conectado a: "
                + cliente.getInetAddress().getHostName());
    } // fin del método conectarAlServidor
    
    
    private void procesarConexion() throws IOException {
        do // procesa los mensajes que se envían desde el servidor
        {
			try
			{
				//mensaje = (String)entrada.readObject(); // lee nuevo mensaje
                if( mensaje.equalsIgnoreCase("cierre") )
                {
                    System.exit(0);
                }
                if( enviaMsj == 1 )
                {
                    enviarDatos( mensajeEnviar );
                    enviaMsj = 0;
                }
                //mostrarMensaje("\n" + mensaje ); // muestra el mensaje
			} catch( Exception exc )
			{
                System.out.println("Se ha recibido un objeto desconocido");
				// System.out.println("Servidor cerrado");
				break;
			}

        } while (true);
    } // fin del método procesarConexion
    
    
    private void cerrarConexion() {
        System.out.println("\nCerrando conexion\n");
		
		try
		{
			try {
				salida.close(); // cierra el flujo de salida
				entrada.close(); // cierra el flujo de entrada 1
				cliente.close(); // cierra el socket
			} // fin de try
			catch (IOException excepcionES) {
				excepcionES.printStackTrace();
			} // fin de catch
		} catch( Exception exc ) {}
        System.out.println("Conexión terminada");
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    } // fin del método cerrarConexion
    
    
    public void enviarDatos(String mensaje) {
        try // envía un objeto al servidor
        {
			salida.writeObject( mensaje );
            salida.flush(); // envía todos los datos a la salida
            System.out.println( "Contenido del mensaje::: " + mensaje );
        } // fin de try
        catch (IOException excepcionES) {
            areaPantalla.append("\nError al escribir objeto");
        } // fin de catch
    } // fin del método enviarDatos
    
    
    
    public void confirmaCierre()
    {
        if( cerrar == 1 )
        {
            cerrarConexion();
        }
    }
    
    
    /*public void setEtiqueta( String txt )
    {
        ticket.setText( txt );
    }*/
    
    public void setFarmacia()
    {
        img = new ImageIcon( this.getClass().getResource("/imagenes/farmacia.jpg") );
		img = new ImageIcon( img.getImage().getScaledInstance( 200, 500, java.awt.Image.SCALE_DEFAULT ));
        ticket.setIcon(img);
    }
    
    public void setMedico()
    {
        img = new ImageIcon( this.getClass().getResource("/imagenes/medico.gif") );
		img = new ImageIcon( img.getImage().getScaledInstance( 240, 350, java.awt.Image.SCALE_DEFAULT ));
        ticket.setIcon(img);
    }
    
    public void setVendedor()
    {
        img = new ImageIcon( this.getClass().getResource("/imagenes/vendedor.gif") );
		img = new ImageIcon( img.getImage().getScaledInstance( 240, 350, java.awt.Image.SCALE_DEFAULT ));
        ticket.setIcon(img);
    }
    
    public void setCajero()
    {
        img = new ImageIcon( this.getClass().getResource("/imagenes/cajero.png") );
		img = new ImageIcon( img.getImage().getScaledInstance( 210, 350, java.awt.Image.SCALE_DEFAULT ));
        ticket.setIcon(img);
    }
    
    public void setTicket()
    {
        img = new ImageIcon( this.getClass().getResource("/imagenes/ticket.jpg") );
		img = new ImageIcon( img.getImage().getScaledInstance( 240, 240, java.awt.Image.SCALE_DEFAULT ));
        ticket.setIcon(img);
    }
    
    
    public void mostrarMensaje(final String mensajeAMostrar) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() // actualiza objeto areaPantalla
                    {
						//Date date = new Date();
						//DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
                        areaPantalla.append( "\n" +  mensajeAMostrar);
                    } // fin del método run
                } // fin de la clase interna anónima
        ); // fin de la llamada a SwingUtilities.invokeLater
    } // fin del método mostrarMensaje
    
    public void vaciarArea()
    {
        areaPantalla.setText("");
    }
    
    public void meterNum( int num )
    {
        campoIntroducir.setText( "" + num );
    }
    
    public int getDioEnter()
    {
        return dioEnter;
    }
    
    public void setDioEnter( int var )
    {
        dioEnter = var;
    }
    
    public int getOpcion()
    {
        return opcion;
    }
}
