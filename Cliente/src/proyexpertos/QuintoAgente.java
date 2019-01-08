package proyexpertos;

import com.itextpdf.text.DocumentException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Agente que comunica todos los agentes entre ellos para dar posibilidad de
 * controlar la intefaz en común a todos.
 */
public class QuintoAgente extends Agent {

    VCliente vCliente = new VCliente();
    VServidor vServidor = new VServidor();
    private String msjString;
    //private final int cantidad = 5; // cantidad de cosas
    private int jarabes = -1; // = cantidad;
    private int omeprazoles = -1; // = cantidad;
    private int naproxenos = -1; // = cantidad;

    private int shampoos = -1; // = cantidad;
    private int jabon = -1; // = cantidad;
    private int toallas = -1; // = cantidad;

    private int labial = -1; // = cantidad;
    private int maquillaje = -1; // = cantidad;

    private int gasas = -1; // = cantidad;
    private int jeringas = -1; // = cantidad;
    private int cubrebocas = -1; // = cantidad;
    private int curitas = -1; // = cantidad;

    private int faltan = 0;
    private String productoFalta = "";

    private int tiempoEspera = 3000;
    private GenerarPDF generarPDF = new GenerarPDF();
    private String ticket = "";
    //private String ruta = "C:\\Users\\Jesus Angeles\\Dropbox\\UAEMex2018B\\Sistemas expertos\\java\\proyecto_final\\Cliente\\ProyExpertosC\\src";
    private String ruta = "C:\\Users\\iron\\Desktop\\codigo\\Cliente\\ProyExpertosC\\src";
    //C:\Users\iron\Desktop\codigo\codigo\Cliente\ProyExpertosC\src
    private int costoTotal;
    private int cambio = 0;
    private int masDeUnProd = 0; // si es mas de 1 se tiene mas de un producto comprado
    private Conexion conexion;

    // Inicialización del agente
    @Override
    protected void setup() {
        conexion = new Conexion();
        /*try {
            conexion.conectar();
            conexion.reiniciarTodo();
            conexion.cerrar();
        } catch (Exception e) {
            System.err.println("Error al conectar BD");
        }*/
        
        jarabes = conexion.getCantidad("Camisas");
        omeprazoles = conexion.getCantidad("pantalones");
        naproxenos = conexion.getCantidad("zapatos");
//        shampoos = conexion.getCantidad("Shampoo");
//        jabon = conexion.getCantidad("Jabón de baño");
//        toallas = conexion.getCantidad("Toalla femenina");
//        labial = conexion.getCantidad("Labial");
//        maquillaje = conexion.getCantidad("Maquillaje");
//        gasas = conexion.getCantidad("Gasas");
//        jeringas = conexion.getCantidad("Jeringas");
//        cubrebocas = conexion.getCantidad("Cubrebocas");
//        curitas = conexion.getCantidad("curitas");

        vCliente.setTitle("Agentes cliente - Ropa");
        vCliente.setSize(850, 550);
        vCliente.setVisible(true);
        vCliente.setLocationRelativeTo(null);
        vCliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vCliente.ejecutarCliente();

        init();

        // Añade un comportamiento
        addBehaviour(new QuintoAgente.ComienzaEscucha());
    }

    // Definición de un comportamiento
    private class ComienzaEscucha extends Behaviour {

        // Función que realiza MiComportamiento
        public void action() {
            // Repite todo el proceso del agente
            do {
                vCliente.vaciarArea();
                ticket = "";
                costoTotal = 0;
                masDeUnProd = 0;
                

                vCliente.setFarmacia();     

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    // Nada
                }
                //vServidor.mostrarMensaje("Mostrando menú_______\n");
                vCliente.setMensaje("Mostrando menú******\n");
                vCliente.setEnviaMsj(1);
                /*
                System.out.println("Seleccione una opción");
                System.out.println("     1.- Venta");
                System.out.println("     2.- Procdutos ");
                System.out.println("Ingrese opción: ");*/

                vCliente.mostrarMensaje("\t\tTIENDA DE ROPA\n\t\tSeleccione una opción");
                vCliente.mostrarMensaje("\t\t1.- Asesoría");
                vCliente.mostrarMensaje("\t\t2.- Productos ");
                vCliente.mostrarMensaje("\t\tIngrese su opción");

                do {
                    esperaRespuesta();
                    vCliente.setDioEnter(0);                                    // Renueva el que no se ha dado enter para una siguiente nueva entrada
                    if (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 2) {
                        JOptionPane.showMessageDialog(null, vCliente.getOpcion() + " no está en el rango permitido, vuelva a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } while (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 2);

                System.out.println("\nSu opción fue: " + vCliente.getOpcion());
                vCliente.mostrarMensaje("\nSu opción fue: " + vCliente.getOpcion());//Muestra la opcion elegida

                if (vCliente.getOpcion() == 1) {
                    AID id = new AID();                                         // No es el ID del emisor, es el ID del agente al que se va a enviar
                    id.setLocalName("medico");                                //Se le pone nombre vendedor                                  
                    //id.setLocalName("vendedor");                                //Se le pone nombre vendedor                                  
                    ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);    // Creación del objeto ACLMessage, es el canal o medio de comunicación para el mensaje

                                                                                //Rellenar los campos Agente emisor
                    mensaje.setSender(getAID());                                // Al agente emisor se le esta asignando su ID
                    mensaje.setLanguage("Español");                             //Se asigna lenguaje
                    mensaje.addReceiver(id);                                    // Al mensaje se le añade el ide del destinatario
                    mensaje.setContent("consulta");                             //se le añade consulta

                    send(mensaje);                                              //Envia el mensaje a los destinatarios

                    mensaje = blockingReceive();
                    if (mensaje != null) {
                        vCliente.setMedico();
                        vCliente.mostrarMensaje("\nEl vendedor le mostrará las tallas disponibles\nSeleccione articulo: ");//
                        vCliente.mostrarMensaje("    1.- camisas");             //
                        vCliente.mostrarMensaje("    2.- pantalones");          //
                        vCliente.mostrarMensaje("    3.- zapatos");             //
                        vCliente.mostrarMensaje("Ingrese su opción");           //

                        do {
                            esperaRespuesta();
                            vCliente.setDioEnter(0);                            // Renueva el que no se ha dado enter para una siguiente nueva entrada
                            if (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 3) {
                                JOptionPane.showMessageDialog(null, vCliente.getOpcion() + " no está en el rango permitido, vuelva a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 3);

                        System.out.println("\nSu opción fue: " + vCliente.getOpcion());
                        vCliente.mostrarMensaje("\nSu opción fue: " + vCliente.getOpcion() + "\n");//Indica opcion seleccionada

                        id = new AID();                                         // No es el ID del emisor, es el ID del agente al que se va a enviar
                        id.setLocalName("medico");
                        //id.setLocalName("vendedor");                            //indica que es vendedor

                        mensaje = new ACLMessage(ACLMessage.REQUEST);           // Creación del objeto ACLMessage, es el canal o medio de comunicación para el mensaje

                                                                                //Rellenar los campos necesarios del mensaje
                        mensaje.setSender(getAID());                            // Al agente emisor se le esta asignando su ID
                        mensaje.setLanguage("Español");
                        mensaje.addReceiver(id);                                // Al mensaje se le añade el ide del destinatario
                        mensaje.setContent(vCliente.getOpcion() + "");
                        send(mensaje);                                          //Envia el mensaje a los destinatarios
                        
                    }

                    mensaje = blockingReceive();
                    if (mensaje != null) {
                        msjString = mensaje.getContent();
                        vCliente.mostrarMensaje("El vendedor recomienda: ");
                        vCliente.mostrarMensaje("\n     " + msjString);
                        System.out.println("ayuda "+msjString);
                    }
                    vCliente.mostrarMensaje("\nPor favor, vaya con el vendedor.");
                    vCliente.mostrarMensaje("Cliente va con el vendedor.\n");
                    try {
                        Thread.sleep(tiempoEspera);
                    } catch (InterruptedException ex) {
                        // Nada
                    }

                    vCliente.setVendedor();
                    vCliente.mostrarMensaje("\nEl cliente llegó con el vendedor.");
                    vCliente.mostrarMensaje("\nHola! Le atiendo, por favor su ticket.");
                    vCliente.mostrarMensaje("Cliente entrega ticket al vendedor. ");
                    vCliente.mostrarMensaje("Vendedor recibe la ticket del cliente.");

                    vCliente.mostrarMensaje("\n¿Desea medicina genérica(1) o de patente(2)?");

                    do {
                        esperaRespuesta();
                        vCliente.setDioEnter(0); // Renueva el que no se ha dado enter para una siguiente nueva entrada
                        if (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 2) {
                            JOptionPane.showMessageDialog(null, vCliente.getOpcion() + " no está en el rango permitido, vuelva a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } while (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 2);

                    System.out.println("\nSu opción fue: " + vCliente.getOpcion());
                    vCliente.mostrarMensaje("\nSu opción fue: " + vCliente.getOpcion());//imprime la opcion seleccionada

                    vCliente.mostrarMensaje("\nPor favor pase con el cajero para que le cobren.");
                    vCliente.mostrarMensaje("Termina interacción con el vendedor.\n");
                    vCliente.mostrarMensaje("Cliente va con el cajero.\n");
                    vServidor.mostrarMensaje("El cliente llegó con el cajero.\n");
                    vCliente.setMensaje("El cliente llegó con el cajero.\n");
                    vCliente.setEnviaMsj(1);
                    vCliente.setMensaje("El cliente llegó con el cajero.\n");
                    vCliente.setEnviaMsj(1);

                    try {
                        Thread.sleep(tiempoEspera);
                    } catch (InterruptedException ex) {
                        // Nada
                    }

                    vCliente.setCajero();
                    id = new AID(); // No es el ID del emisor, es el ID del agente al que se va a enviar
                    id.setLocalName("cajero");

                    // Creación del objeto ACLMessage, es el canal o medio de comunicación para el mensaje
                    mensaje = new ACLMessage(ACLMessage.REQUEST);

                    //Rellenar los campos necesarios del mensaje
                    mensaje.setSender(getAID()); // Al agente emisor se le esta asignando su ID
                    mensaje.setLanguage("Español");
                    mensaje.addReceiver(id); // Al mensaje se le añade el ide del destinatario
                    mensaje.setContent(msjString + vCliente.getOpcion()); // Manda el diagnostico y la receta

                    //Envia el mensaje a los destinatarios
                    send(mensaje); // Le manda la receta siendo generica o de patente

                    mensaje = blockingReceive();
                    if (mensaje != null) {
                        msjString = mensaje.getContent(); // Regresa costo
                        vCliente.mostrarMensaje("Cajero dice que el costo es de $" + msjString + " más $100 de consulta");
                        cambio = Integer.parseInt(msjString) + 100;
                        vCliente.mostrarMensaje("Total a pagar $" + cambio);

                        vCliente.mostrarMensaje("Por favor ingrese la cantidad para pagar (20, 50, 100, 200, etc.).");

                        do {
                            esperaRespuesta();
                            vCliente.setDioEnter(0); // Renueva el que no se ha dado enter para una siguiente nueva entrada
                            if (vCliente.getOpcion() < cambio || vCliente.getOpcion() > 10000) {
                                JOptionPane.showMessageDialog(null, vCliente.getOpcion() + " no está en el rango permitido, vuelva a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (vCliente.getOpcion() < cambio || vCliente.getOpcion() > 10000);

                        System.out.println("\nCajero recibe " + vCliente.getOpcion());
                        vCliente.mostrarMensaje("\nCajero recibe: $" + vCliente.getOpcion());
                        vServidor.mostrarMensaje("\nCajero recibe: $" + vCliente.getOpcion());
                        vCliente.setMensaje("\nCajero recibe: $" + vCliente.getOpcion());
                        vCliente.setEnviaMsj(1);

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {

                        }

                        int cambio2 = cambio;
                        vCliente.mostrarMensaje("Cliente paga $" + cambio + " al cajero.");

                        cambio = vCliente.getOpcion() - Integer.parseInt(msjString) - 400;
                        vCliente.mostrarMensaje("Cliente recibe $" + cambio + " de cambio.");

                        vServidor.mostrarMensaje("\nCajero recibe pago del ciente por $" + cambio2);
                        vCliente.setMensaje("\nCajero recibe pago del ciente por $" + cambio2);
                        vCliente.setEnviaMsj(1);

                        vCliente.mostrarMensaje("\nCajero da medicinas al cliente.");

                        try {
                            Thread.sleep(tiempoEspera);
                        } catch (InterruptedException ex) {
                            // Nada
                        }
                        vCliente.mostrarMensaje("Se le da ticket al cliente:\n\n");

                        ticket = " === Ticket === \n";
                        costoTotal = 400; // De la consulta
                        if (msjString.equals("100")) // Jarabe generico
                        {
                            ticket = ticket + " pantalon \n";
                            ticket = ticket + " Costo: $100\n";
                            ticket = ticket + " Producto \n";
                            jarabes--;
                            conexion.setCantidad("camisa", jarabes);
                            conexion.setVendidos("camisa", conexion.getVendidos("camisa") + 1);
                            costoTotal = costoTotal + 100;
                        } else if (msjString.equals("200")) // Jarabe generico
                        {
                            ticket = ticket + " camisa\n";
                            ticket = ticket + " Costo: $200\n";
                            ticket = ticket + " camisa\n";
                            jarabes--;
                            conexion.setCantidad("camisa", jarabes);
                            conexion.setVendidos("camisa", conexion.getVendidos("camisa") + 1);
                            costoTotal = costoTotal + 200;}
                        } else if (msjString.equals("220")) // Jarabe generico
                        {
                            ticket = ticket + " Medicina: Omeprazol \n";
                            ticket = ticket + " Costo: $220\n";
                            ticket = ticket + " Producto genérico\n";
                            omeprazoles--;
                            conexion.setCantidad("Omeprazol", omeprazoles);
                            conexion.setVendidos("Omeprazol", conexion.getVendidos("Omeprazol") + 1);
                            costoTotal = costoTotal + 220;
                        } else if (msjString.equals("340")) // Jarabe generico
                        {
                            ticket = ticket + " Medicina: Omeprazol \n";
                            ticket = ticket + " Costo: $340\n";
                            ticket = ticket + " Producto patente\n";
                            omeprazoles--;
                            conexion.setCantidad("Omeprazol", omeprazoles);
                            conexion.setVendidos("Omeprazol", conexion.getVendidos("Omeprazol") + 1);
                            costoTotal = costoTotal + 340;
                        } else if (msjString.equals("180")) // Jarabe generico
                        {
                            ticket = ticket + " Medicina: Naproxeno \n";
                            ticket = ticket + " Costo: $180\n";
                            ticket = ticket + " Producto genérico\n";
                            naproxenos--;
                            conexion.setCantidad("Naproxeno", naproxenos);
                            conexion.setVendidos("Naproxeno", conexion.getVendidos("Naproxeno") + 1);
                            costoTotal = costoTotal + 180;
                        } else if (msjString.equals("640")) // Jarabe generico
                        {
                            ticket = ticket + " Medicina: Naproxeno \n";
                            ticket = ticket + " Costo: $640\n";
                            ticket = ticket + " Producto patente\n";
                            naproxenos--;
                            conexion.setCantidad("Naproxeno", naproxenos);
                            conexion.setVendidos("Naproxeno", conexion.getVendidos("Naproxeno") + 1);
                            costoTotal = costoTotal + 640;
                        }
                        ticket = ticket + " Concepto: Consulta médica \n";
                        ticket = ticket + " Costo: $400\n";
                        ticket = ticket + " Costo Total: $" + costoTotal + "\n\n";

                        //vCliente.setEtiqueta( " Total = $ " + costoTotal );
                        vCliente.setTicket();
                        vCliente.mostrarMensaje(ticket);
                    

                    // Termina consulta*****************************************************************************************
                } else {
                    // Opción 2 - Comprar medicinas
                    vCliente.mostrarMensaje("El cliente pasa con el vendedor.");

                    vCliente.setVendedor();
                    AID id = new AID(); // No es el ID del emisor, es el ID del agente al que se va a enviar
                    id.setLocalName("vendedor");

                    // Creación del objeto ACLMessage, es el canal o medio de comunicación para el mensaje
                    ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);

                    //Rellenar los campos necesarios del mensaje
                    mensaje.setSender(getAID()); // Al agente emisor se le esta asignando su ID
                    mensaje.setLanguage("Español");
                    mensaje.addReceiver(id); // Al mensaje se le añade el ide del destinatario
                    mensaje.setContent("compra");

                    //Envia el mensaje a los destinatarios
                    send(mensaje);

                    mensaje = blockingReceive();

                    int opcion;
                    int costo;
                    ticket = " === Ticket === \n";
                    if (mensaje != null) {
                        do {
                            vCliente.mostrarMensaje("\nVendedor muestra catalogo de productos:");
                            vCliente.mostrarMensaje("     1.- Jarabe.");
                            vCliente.mostrarMensaje("     2.- Omeprazol.");
                            vCliente.mostrarMensaje("     3.- Naproxeno.");
                            vCliente.mostrarMensaje("     4.- Shampoo.");
                            vCliente.mostrarMensaje("     5.- Jabón de baño.");
                            vCliente.mostrarMensaje("     6.- Toalla femenia.");
                            vCliente.mostrarMensaje("     7.- Labial.");
                            vCliente.mostrarMensaje("     8.- Maquillaje.");
                            vCliente.mostrarMensaje("     9.- Gasas.");
                            vCliente.mostrarMensaje("     10.- Jeringas.");
                            vCliente.mostrarMensaje("     11.- Cubrebocas.");
                            vCliente.mostrarMensaje("     12.- Curitas.");
                            if (masDeUnProd != 0) {
                                vCliente.mostrarMensaje("     13.- Continuar.");
                            }
                            masDeUnProd++;
                            vCliente.mostrarMensaje("Ingrese su opción: ");

                            do {
                                esperaRespuesta();
                                vCliente.setDioEnter(0); // Renueva el que no se ha dado enter para una siguiente nueva entrada
                                if (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 13) {
                                    JOptionPane.showMessageDialog(null, vCliente.getOpcion() + " no está en el rango permitido, vuelva a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } while (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 13);

                            opcion = vCliente.getOpcion();

                            System.out.println("\nSu opción fue: " + vCliente.getOpcion());
                            vCliente.mostrarMensaje("\nSu opción fue: " + vCliente.getOpcion());

                            // Compra medicina
                            if (vCliente.getOpcion() == 1 || vCliente.getOpcion() == 2 || vCliente.getOpcion() == 3) {
                                vCliente.mostrarMensaje("\n\n ¿Medicina genérica(1) o de patente(2)?");

                                do {
                                    esperaRespuesta();
                                    vCliente.setDioEnter(0); // Renueva el que no se ha dado enter para una siguiente nueva entrada
                                    if (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 2) {
                                        JOptionPane.showMessageDialog(null, vCliente.getOpcion() + " no está en el rango permitido, vuelva a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } while (vCliente.getOpcion() < 1 || vCliente.getOpcion() > 2);

                                System.out.println("\n Su opción fue: " + vCliente.getOpcion());
                                vCliente.mostrarMensaje("\n Su opción fue: " + vCliente.getOpcion() + "\n");

                            }

                            costo = 0;
                            switch (opcion) {
                                case 1:
                                    if (vCliente.getOpcion() == 1) // generica
                                    {
                                        costo = 100;
                                    } else {
                                        costo = 200;
                                    }
                                    break;
                                case 2:
                                    if (vCliente.getOpcion() == 1) // generica
                                    {
                                        costo = 220;
                                    } else {
                                        costo = 340;
                                    }
                                    break;
                                case 3:
                                    if (vCliente.getOpcion() == 1) // generica
                                    {
                                        costo = 180;
                                    } else {
                                        costo = 640;
                                    }
                                    break;
                                case 4:
                                    costo = 55;
                                    break;
                                case 5:
                                    costo = 20;
                                    break;
                                case 6:
                                    costo = 60;
                                    break;
                                case 7:
                                    costo = 90;
                                    break;
                                case 8:
                                    costo = 85;
                                    break;
                                case 9:
                                    costo = 25;
                                    break;
                                case 10:
                                    costo = 15;
                                    break;
                                case 11:
                                    costo = 10;
                                    break;
                                case 12:
                                    costo = 3;
                                    break;
                                default:
                                    break;
                            }

                            costoTotal = costoTotal + costo;

                            if (costo == 100) // Jarabe generico
                            {
                                ticket = ticket + " Medicina: Jarabe \n";
                                ticket = ticket + " Costo: $100\n";
                                ticket = ticket + " Producto genérico\n";
                                jarabes--;
                                conexion.setCantidad("Jarabe", jarabes);
                                conexion.setVendidos("Jarabe", conexion.getVendidos("Jarabe") + 1);
                            } else if (costo == 200) // Jarabe generico
                            {
                                ticket = ticket + " Medicina: Jarabe \n";
                                ticket = ticket + " Costo: $200\n";
                                ticket = ticket + " Producto patente\n";
                                jarabes--;
                                conexion.setCantidad("Jarabe", jarabes);
                                conexion.setVendidos("Jarabe", conexion.getVendidos("Jarabe") + 1);
                            } else if (costo == 220) // Jarabe generico
                            {
                                ticket = ticket + " Medicina: Omeprazol \n";
                                ticket = ticket + " Costo: $220\n";
                                ticket = ticket + " Producto genérico\n";
                                omeprazoles--;
                                conexion.setCantidad("Omeprazol", omeprazoles);
                                conexion.setVendidos("Omeprazol", conexion.getVendidos("Omeprazol") + 1);
                            } else if (costo == 340) // Jarabe generico
                            {
                                ticket = ticket + " Medicina: Omeprazol \n";
                                ticket = ticket + " Costo: $340\n";
                                ticket = ticket + " Producto patente\n";
                                omeprazoles--;
                                conexion.setCantidad("Omeprazol", omeprazoles);
                                conexion.setVendidos("Omeprazol", conexion.getVendidos("Omeprazol") + 1);
                            } else if (costo == 180) // Jarabe generico
                            {
                                ticket = ticket + " Medicina: Naproxeno \n";
                                ticket = ticket + " Costo: $180\n";
                                ticket = ticket + " Producto genérico\n";
                                naproxenos--;
                                conexion.setCantidad("Naproxeno", naproxenos);
                                conexion.setVendidos("Naproxeno", conexion.getVendidos("Naproxeno") + 1);
                            } else if (costo == 640) // Jarabe generico
                            {
                                ticket = ticket + " Medicina: Naproxeno \n";
                                ticket = ticket + " Costo: $640\n";
                                ticket = ticket + " Producto patente\n";
                                naproxenos--;
                                conexion.setCantidad("Naproxeno", naproxenos);
                                conexion.setVendidos("Naproxeno", conexion.getVendidos("Naproxeno") + 1);
                            } else if (costo == 55) {
                                ticket = ticket + " Medicina: Shampoo \n";
                                ticket = ticket + " Costo: $55\n";
                                shampoos--;
                                conexion.setCantidad("Shampoo", shampoos);
                                conexion.setVendidos("Shampoo", conexion.getVendidos("Shampoo") + 1);
                            } else if (costo == 20) {
                                ticket = ticket + " Medicina: Jabón de baño \n";
                                ticket = ticket + " Costo: $20\n";
                                jabon--;
                                conexion.setCantidad("Jabón de baño", jabon);
                                conexion.setVendidos("Jabón de baño", conexion.getVendidos("Jabón de baño") + 1);
                            } else if (costo == 60) {
                                ticket = ticket + " Medicina: Toallas femeninas \n";
                                ticket = ticket + " Costo: $60\n";
                                toallas--;
                                conexion.setCantidad("Toalla femenina", toallas);
                                conexion.setVendidos("Toalla femenina", conexion.getVendidos("Toalla femenina") + 1);
                            } else if (costo == 90) {
                                ticket = ticket + " Medicina: Labial \n";
                                ticket = ticket + " Costo: $90\n";
                                labial--;
                                conexion.setCantidad("Labial", labial);
                                conexion.setVendidos("Labial", conexion.getVendidos("Labial") + 1);
                            } else if (costo == 85) {
                                ticket = ticket + " Medicina: Maquillaje \n";
                                ticket = ticket + " Costo: $85\n";
                                maquillaje--;
                                conexion.setCantidad("Maquillaje", maquillaje);
                                conexion.setVendidos("Maquillaje", conexion.getVendidos("Maquillaje") + 1);
                            } else if (costo == 25) {
                                ticket = ticket + " Medicina: Gasas \n";
                                ticket = ticket + " Costo: $25\n";
                                gasas--;
                                conexion.setCantidad("Gasas", gasas);
                                conexion.setVendidos("Gasas", conexion.getVendidos("Gasas") + 1);
                            } else if (costo == 15) {
                                ticket = ticket + " Medicina: Jeringas \n";
                                ticket = ticket + " Costo: $15\n";
                                jeringas--;
                                conexion.setCantidad("Jeringas", jeringas);
                                conexion.setVendidos("Jeringas", conexion.getVendidos("Jeringas") + 1);
                            } else if (costo == 10) {
                                ticket = ticket + " Medicina: Cubrebocas \n";
                                ticket = ticket + " Costo: $10\n";
                                cubrebocas--;
                                conexion.setCantidad("Cubrebocas", cubrebocas);
                                conexion.setVendidos("Cubrebocas", conexion.getVendidos("Cubrebocas") + 1);
                            }else if(costo == 3){
                                ticket = ticket + " Medicina: Curitas \n";
                                ticket = ticket + " Costo: $3\n";
                                curitas--;
                                conexion.setCantidad("Curitas", curitas);
                                conexion.setVendidos("Curitas", conexion.getVendidos("Curitas") + 1);
                            }
                        } while (opcion != 13);

                        ticket = ticket + " Costo Total: " + costoTotal;
                        vCliente.mostrarMensaje("\n\nPor favor pase con el cajero para que le cobren.");
                        vCliente.mostrarMensaje("Termina interaccion con el vendedor.");
                        vCliente.mostrarMensaje("\n\nCliente va con el cajero");

                        try {
                            Thread.sleep(tiempoEspera);
                        } catch (InterruptedException ex) {
                            // Nada
                        }

                        vCliente.setCajero();
                        vServidor.mostrarMensaje("\nEl cliente llegó con el cajero.");
                        vCliente.setMensaje("\nEl cliente llegó con el cajero.");
                        vCliente.setEnviaMsj(1);

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {

                        }

                        vServidor.mostrarMensaje("\nCajero revisa costo y le informa al cliente.");
                        vCliente.setMensaje("\nCajero revisa costo y le informa al cliente.");
                        vCliente.setEnviaMsj(1);
                        vCliente.mostrarMensaje("\nCajero dice que el costo es de " + costoTotal);

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {

                        }

                        vCliente.setMensaje("\nCajero dice que el costo es de " + costoTotal);
                        vCliente.setEnviaMsj(1);

                        vCliente.mostrarMensaje("Por favor ingrese la cantidad para pagar (20 50 100 200 500 1000).");

                        do {
                            esperaRespuesta();
                            vCliente.setDioEnter(0); // Renueva el que no se ha dado enter para una siguiente nueva entrada
                            if (vCliente.getOpcion() < costoTotal || vCliente.getOpcion() > 10000) {
                                JOptionPane.showMessageDialog(null, vCliente.getOpcion() + " no está en el rango permitido, vuelva a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (vCliente.getOpcion() < costoTotal || vCliente.getOpcion() > 10000);

                        System.out.println("\nCajero recibe " + vCliente.getOpcion());
                        vCliente.mostrarMensaje("\nCajero recibe: $" + vCliente.getOpcion());
                        vServidor.mostrarMensaje("\nCajero recibe: $" + vCliente.getOpcion());
                        vCliente.setMensaje("\nCajero recibe: $" + vCliente.getOpcion());
                        vCliente.setEnviaMsj(1);

                        cambio = vCliente.getOpcion() - costoTotal;

                        vCliente.mostrarMensaje("Cliente paga $" + costoTotal + " al cajero.");
                        vCliente.mostrarMensaje("Cliente recibe $" + cambio + " de cambio.");

                        vServidor.mostrarMensaje("\nCajero recibe pago del ciente por " + costoTotal);
                        vCliente.setMensaje("\nCajero recibe pago del ciente por " + costoTotal);
                        vCliente.setEnviaMsj(1);
                        vCliente.mostrarMensaje("\nCajero da productos al cliente.");

                        try {
                            Thread.sleep(tiempoEspera);
                        } catch (InterruptedException ex) {
                            // Nada
                        }

                        vCliente.mostrarMensaje("\nSe le da ticket al cliente:\n\n");
                        vServidor.mostrarMensaje("\nCajero dio productos y ticket al cliente.");
                        vCliente.setMensaje("\nCajero dio productos y ticket al cliente.");
                        vCliente.setEnviaMsj(1);

                        //vCliente.setEtiqueta( " Total = $ " + costoTotal );
                        vCliente.setTicket();
                        vCliente.mostrarMensaje(ticket);
                    }
                }

                try {
                    generarPDF.genera(ticket, "Ticket", ruta);
                } catch (FileNotFoundException | DocumentException ex) {
                    System.out.println("-----Error al generar ticket ----- ");
                }

                /*File file = new File("c:/newfile.txt");

                try {
                    if( file.createNewFile() ){
                        System.out.println("Fichero creado correctamente");
                    } else {
                        System.out.println("El fichero ya existe");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(QuintoAgente.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                vCliente.mostrarMensaje("\nSe ha generado el ticket del cliente en PDF.\n");
                vServidor.mostrarMensaje("\nSe ha generado el ticket del cliente en PDF.");
                vCliente.setMensaje("\nSe ha generado el ticket del cliente en PDF.");
                vCliente.setEnviaMsj(1);

                // Verifica si ya se van a acabar los productos para comprar mas
                if (jarabes <= 1) {
                    productoFalta = "jarabes";
                    faltan = 1;
                }
                if (omeprazoles <= 1) {
                    productoFalta = "omeprazoles";
                    faltan = 1;
                }
                if (naproxenos <= 1) {
                    productoFalta = "naproxenos";
                    faltan = 1;
                }

                // Higiene
                if (shampoos <= 1) {
                    productoFalta = "shampoos";
                    faltan = 1;
                }
                if (jabon <= 1) {
                    productoFalta = "jabones";
                    faltan = 1;
                }
                if (toallas <= 1) {
                    productoFalta = "toallas femenias";
                    faltan = 1;
                }

                // Cosmeticos
                if (labial <= 1) {
                    productoFalta = "labiales";
                    faltan = 1;
                }
                if (maquillaje <= 1) {
                    productoFalta = "maquillajes";
                    faltan = 1;
                }

                // Salud
                if (gasas <= 1) {
                    productoFalta = "gasas";
                    faltan = 1;
                }
                if (jeringas <= 1) {
                    productoFalta = "jeringas";
                    faltan = 1;
                }
                if (cubrebocas <= 1) {
                    productoFalta = "cubrebocas";
                    faltan = 1;
                }
                if (curitas <= 1) {
                    productoFalta = "curitas";
                    faltan = 1;
                }

                if (faltan == 1) {
                    vServidor.mostrarMensaje("Están apunto de terminarse l@s " + productoFalta + ".\n");
                    vServidor.mostrarMensaje("Cajero pide más " + productoFalta + " a proveedor.\n");

                    vCliente.setMensaje("Están apunto de terminarse l@s " + productoFalta + ".\n");
                    vCliente.setEnviaMsj(1);

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {

                    }

                    vCliente.setMensaje("Cajero pide más " + productoFalta + " a proveedor.\n");
                    vCliente.setEnviaMsj(1);

                    AID id = new AID(); // No es el ID del emisor, es el ID del agente al que se va a enviar
                    id.setLocalName("proveedor");

                    // Creación del objeto ACLMessage, es el canal o medio de comunicación para el mensaje
                    ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);

                    //Rellenar los campos necesarios del mensaje
                    mensaje.setSender(getAID()); // Al agente emisor se le esta asignando su ID
                    mensaje.setLanguage("Español");
                    mensaje.addReceiver(id); // Al mensaje se le añade el ide del destinatario
                    mensaje.setContent("productos");

                    //Envia el mensaje a los destinatarios
                    send(mensaje);

                    mensaje = blockingReceive();
                    if (mensaje != null) {
                        // Se reciben productos
                        if (jarabes <= 1) {
                            productoFalta = "jarabes";
                            conexion.setCantidad("Jarabe", 5);
                            jarabes = 5; // = cantidad;
                        }
                        if (omeprazoles <= 1) {
                            productoFalta = "omeprazoles";
                            conexion.setCantidad("Omeprazol", 5);
                            omeprazoles = 5; // = cantidad;
                        }
                        if (naproxenos <= 1) {
                            productoFalta = "naproxenos";
                            conexion.setCantidad("Naproxeno", 5);
                            naproxenos = 5; // = cantidad;
                        }

                        // Higiene
                        if (shampoos <= 1) {
                            productoFalta = "shampoos";
                            conexion.setCantidad("Shampoo", 5);
                            shampoos = 5; // = cantidad;
                        }
                        if (jabon <= 1) {
                            productoFalta = "jabones";
                            conexion.setCantidad("Jabón de baño", 5);
                            jabon = 5; // = cantidad;
                        }
                        if (toallas <= 1) {
                            productoFalta = "toallas femenias";
                            conexion.setCantidad("Toalla femenina", 5);
                            toallas = 5; // = cantidad;
                        }

                        // Cosmeticos
                        if (labial <= 1) {
                            productoFalta = "labiales";
                            conexion.setCantidad("Labial", 5);
                            labial = 5; // = cantidad;
                        }
                        if (maquillaje <= 1) {
                            productoFalta = "maquillajes";
                            conexion.setCantidad("Maquillaje", 5);
                            maquillaje = 5; // = cantidad;
                        }

                        // Salud
                        if (gasas <= 1) {
                            productoFalta = "gasas";
                            conexion.setCantidad("Gasas", 5);
                            gasas = 5; // = cantidad;
                        }
                        if (jeringas <= 1) {
                            productoFalta = "jeringas";
                            conexion.setCantidad("Jeringas", 5);
                            jeringas = 5; // = cantidad;
                        }
                        if (cubrebocas <= 1) {
                            productoFalta = "cubrebocas";
                            conexion.setCantidad("Cubrebocas", 5);
                            cubrebocas = 5; // = cantidad;
                        }
                        if (curitas <= 1) {
                            productoFalta = "curitas";
                            conexion.setCantidad("Curitas", 5);
                            cubrebocas = 5; // = cantidad;
                        }

                        vServidor.mostrarMensaje("Fueron comprados al proveedor " + productoFalta);
                        vServidor.mostrarMensaje("Stock lleno para " + productoFalta + "\n");

                        vCliente.setMensaje("Fueron comprados al proveedor " + productoFalta);
                        vCliente.setEnviaMsj(1);

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {

                        }

                        vCliente.setMensaje("Stock lleno para " + productoFalta + "\n");
                        vCliente.setEnviaMsj(1);
                    }
                }
                faltan = 0;

                vCliente.mostrarMensaje("\nPresione Enter para continuar...");
                vCliente.meterNum(1);
                esperaRespuesta();
                vCliente.setDioEnter(0); // Renueva el que no se ha dado enter para una siguiente nueva entrada

                try {
                    Thread.sleep(50); // Espera 50 milisegundos para no calentar el procesador
                } catch (InterruptedException ex) {
                    // Nada
                }
                
                // carga los registros a la otra maquina
                String registros = conexion.selectString();
                vCliente.setMensaje(registros);
                vCliente.setEnviaMsj(1);
                System.out.println(registros);
                System.out.println("Select:::");
                conexion.select();
            } while (true);
        }

        private void esperaRespuesta() {
            do {
                try {
                    Thread.sleep(50); // Espera 50 milisegundos para no calentar el procesador
                } catch (InterruptedException ex) {
                    // Nada
                }
            } while (vCliente.getDioEnter() == 0);
        }

        @Override
        public boolean done() {
            return true;
        }

        public long leerInt(String dato) {
            int recibeNum = 0;
            try {
                recibeNum = Integer.parseInt(dato);
            } catch (NumberFormatException e) {

            }
            return recibeNum;
        }
    }

    public void init() {
        vServidor.setTitle("Agentes servidor - Farmacia");
        vServidor.setSize(650, 550);
        //vServidor.setVisible(true);
        vServidor.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //vServidor.ejecutarServidor();
    }
}
