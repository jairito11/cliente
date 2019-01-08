package proyexpertos;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VServidor extends JFrame implements ActionListener {

    private JTextArea areaPantalla; // muestra la información al usuario
    private Font fuente = new Font("Arial", Font.BOLD, 18);
    private JLabel ticket;
    private JButton btnSalir;

    private ObjectOutputStream salida; // flujo de salida hacia el cliente
    private ObjectInputStream entrada; // flujo de entrada del cliente
    private ServerSocket servidor; // socket servidor
    private Socket conexion; // conexión al cliente
    private String datos;  // recibe y guarda los datos del sistema cliente
    private int contador = 1; // contador del número de conexiones

    public VServidor() {
        btnSalir = new JButton("Salir");
        add(btnSalir, BorderLayout.SOUTH);
        btnSalir.addActionListener(this);

        areaPantalla = new JTextArea(); // crea objeto areaPantalla
        add(new JScrollPane(areaPantalla), BorderLayout.CENTER);
        areaPantalla.setEditable(false);
        areaPantalla.setFont(fuente);

        ImageIcon img = new ImageIcon(this.getClass().getResource("/imagenes/farmacia.jpg"));
        img = new ImageIcon(img.getImage().getScaledInstance(200, 500, java.awt.Image.SCALE_DEFAULT));

        ticket = new JLabel(img, SwingConstants.CENTER);
        ticket.setFont(fuente);
        //ticket.setSize( 100, 100);
        //add(ticket, BorderLayout.EAST);
    }

    public class HiloServidor extends Thread {

        @Override
        public void run() {
            try // establece el servidor para que reciba conexiones; procesa las conexiones
            {
                servidor = new ServerSocket(12345, 100); // crea objeto ServerSocket

                while (true) {
                    try {
                        esperarConexion(); // espera una conexión
                        obtenerFlujos(); // obtiene los flujos de entrada y salida
                        procesarConexion(); // procesa la conexión
                    } // fin de try
                    catch (EOFException excepcionEOF) {
                        System.out.println("Servidor termino la conexion");
                    } // fin de catch
                    finally {
                        cerrarConexion(); // cierra la conexión
                        contador++;
                    } // fin de finally
                } // fin de while
            } // fin de try
            catch (IOException exepcionES) {
                exepcionES.printStackTrace();
            } // fin de catch
        }
    }

    // establece y ejecuta el servidor
    public void ejecutarServidor() {
        HiloServidor hiloServidor = new HiloServidor();
        hiloServidor.start();
    } // fin del método ejecutarServidor

    // espera a que llegue una conexión, después muestra información sobre ésta
    private void esperarConexion() throws IOException {
        System.out.println("Esperando una conexion\n");
        conexion = servidor.accept(); // permite al servidor aceptar la conexión
        System.out.println("Conexion recibida de: "
                + conexion.getInetAddress().getHostName());
    } // fin del método esperarConexion

    private void obtenerFlujos() throws IOException {
        // establece el flujo de salida para los objetos
        salida = new ObjectOutputStream(conexion.getOutputStream());
        salida.flush(); // vacía el búfer de salida para enviar información del encabezado

        // establece el flujo de entrada para los objetos
        entrada = new ObjectInputStream(conexion.getInputStream());

        System.out.println("Se obtuvieron los flujos de red de E/S\n");
    } // fin del método obtenerFlujos

    private void procesarConexion() throws IOException {
        String mensaje = "Conexion exitosa";
        enviarDatos(mensaje); // envía mensaje de conexión exitosa

        do // procesa los mensajes enviados desde el cliente
        {
            try {
                datos = (String) entrada.readObject(); // lee el nuevo mensaje
                mostrarMensaje(datos);
                System.out.println("Datos recibidos del cliente.");
            } catch (Exception excep) {
            }

        } while (!mensaje.equalsIgnoreCase("Cliente::: TERMINAR"));
    } // fin del método procesarConexion

    private void cerrarConexion() {
        System.out.println("Terminando conexion\n");

        try {
            try {
                salida.close(); // cierra flujo de salida
                entrada.close(); // cierra flujo de entrada
                conexion.close(); // cierra el socket
            } // fin de try
            catch (IOException exepcionES) {
                exepcionES.printStackTrace();
            } // fin de catch
        } catch (Exception e) {
        }
        System.out.println("Conexión terminada");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } // fin del método cerrarConexion

    public void enviarDatos(String mensaje) {
        try // envía objeto al cliente
        {
            salida.writeObject("Servidor::: " + mensaje);
            salida.flush(); // envía toda la salida al cliente
            System.out.println("Servidor::: " + mensaje);
        } // fin de try
        catch (IOException exepcionES) {
            // areaPantalla.append("\nError al escribir objeto");
            try {
                cerrarConexion();
            } catch (Exception e) {
            }
        } // fin de catch
    } // fin del método enviarDatos

    public void mostrarMensaje(final String mensajeAMostrar) {
        SwingUtilities.invokeLater(
                new Runnable() {
            public void run() // actualiza objeto areaPantalla
            {
                //Date date = new Date();
                //DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
                areaPantalla.append("\n" + mensajeAMostrar);
            } // fin del método run
        } // fin de la clase interna anónima
        ); // fin de la llamada a SwingUtilities.invokeLater
    } // fin del método mostrarMensaje

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == btnSalir) {
            mostrarMensaje("\nCerrando...");
            System.out.println("Cerrando");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                // Nada
            }
            System.exit(0);
            //enviarDatos("TERMINAR");
        }
    }
}
