package proyexpertos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Conexion {

    public static Connection conexion;
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String JDBC_URL = "jdbc:derby:baseTipos;create=true";
    // "jdbc:derby:dbtest2;create=true";

    public void conectar() throws Exception {
        try {
            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(JDBC_URL);
        } catch (Exception exc) {
            System.out.println("Error al conectar");
            throw exc;
        }
    }

    /**
     * Cierre de la conexión con la BD.
     *
     * @throws SQLException
     */
    public void cerrar() throws SQLException {
        if (conexion != null) {
            if (!conexion.isClosed()) {
                System.out.println("Cerrado..."); // Mensaje de prueba para comprobar que entra aqui
                conexion.close();
            }
        }
    }

    /**
     * Crea admindba para permitir la creación del resto de objetos y esquemas.
     * Debe iniciarse con superadmin, un usuario con ALL PRIVILEGES.
     */
    public void iniciarTodo() {
        try {
            System.out.println("Entrando a iniciar todo");

            conexion.createStatement().execute("CREATE TABLE productos("
                    + " producto VARCHAR(30), cantidad INTEGER,"
                    + " vendidos INTEGER"
                    + ")");
            System.out.println("Tabla creada");

            conexion.createStatement().execute("INSERT INTO productos VALUES('Jarabe',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Omeprazol',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Naproxeno',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Shampoo',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Jabón de baño',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Toalla femenina',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Labial',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Maquillaje',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Gasas',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Jeringas',5,0)");
            conexion.createStatement().execute("INSERT INTO productos VALUES('Cubrebocas',5,0)");
            System.out.println("Registros insertados");

            // SELECT
            try {
                PreparedStatement st;
                st = conexion.prepareStatement("SELECT * from productos");
                ResultSet rs = st.executeQuery();
                String resultado = "";
                while (rs.next()) {
                    resultado = "Producto: " + rs.getString(1) + ", Cant: " + rs.getString(2)
                            + ", Vendidos: " + rs.getString(3);
                    System.out.println(resultado);
                }
                System.out.println("Fin");
            } catch (Exception exc) {
                System.out.println("Error al leer");
            }
        } catch (SQLException e) {
            System.err.println("Error al iniciar todo");
        }
    }

    /**
     * Reinicia todo al borrar e ingresar de nuevo.
     */
    public void reiniciarTodo() {
        try {
            System.out.println("Entrando a reiniciar todo");

            conexion.createStatement().execute("DROP TABLE productos");
            System.out.println("Tabla borrada");

            iniciarTodo();
        } catch (Exception e) {
            System.err.println("Error al reiniciar todo");
        }
    }

    public void insertar(String producto, int cant, int vend) {
        try {
            conectar();
            conexion.createStatement().execute("INSERT INTO productos VALUES('" + producto + "'," + cant + "," + vend + ")");
            cerrar();
        } catch (Exception e) {
            System.err.println("Error al registrar");
        }
    }

    public void select() {
        // SELECT
        try {
            conectar();
            PreparedStatement st;
            st = conexion.prepareStatement("SELECT * from productos");
            ResultSet rs = st.executeQuery();
            String resultado = "";
            while (rs.next()) {
                resultado = "Producto: " + rs.getString(1) + ", Cant: " + rs.getString(2)
                        + ", Vendidos: " + rs.getString(3);
                System.out.println(resultado);
            }
            cerrar();
            System.out.println("Fin");
        } catch (Exception exc) {
            System.out.println("Error al leer");
        }
    }
    
    public String selectString() {
        String resultado = "";
        // SELECT
        try {
            conectar();
            PreparedStatement st;
            st = conexion.prepareStatement("SELECT * from productos");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                resultado = resultado + rs.getString(1) + "," + rs.getString(2)
                        + "," + rs.getString(3) + "@";
                System.out.println(resultado);
            }
            cerrar();
            System.out.println("Fin");
        } catch (Exception exc) {
            System.out.println("Error al leer");
        }
        
        return resultado;
    }

    /**
     * Trae el nombre de un producto.
     *
     * @param buscado
     * @return
     */
    public String getProducto(String buscado) {
        String producto = null;
        PreparedStatement st;
        try {
            conectar();
            st = conexion.prepareStatement("SELECT * from productos where producto = ?");
            st.setString(1, buscado);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                producto = rs.getString("producto");
                System.out.println("PRODUCTO: " + producto);
            }
            cerrar();
        } catch (Exception e) {
            System.err.println("Error al consultar producto");
        }
        return producto;
    }

    /**
     * Trae la cantidad de un producto.
     *
     * @param buscado
     * @return
     */
    public int getCantidad(String buscado) {
        int producto = -1;
        PreparedStatement st;
        try {
            conectar();
            st = conexion.prepareStatement("SELECT cantidad FROM productos WHERE producto = ?");
            st.setString(1, buscado);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                producto = Integer.parseInt(rs.getString("cantidad"));
                System.out.println("CANTIDAD DE " + buscado + ": " + producto);
            }
            cerrar();
        } catch (Exception e) {
            System.err.println("Error al consultar cantidad");
        }
        return producto;
    }

    /**
     * Trae la cantidad de un producto.
     *
     * @param buscado
     * @return
     */
    public int getVendidos(String buscado) {
        int producto = -1;
        PreparedStatement st;
        try {
            conectar();
            st = conexion.prepareStatement("SELECT * from productos where producto = ?");
            st.setString(1, buscado);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                producto = rs.getInt("vendidos");
                System.out.println("VENDIDOS DE " + buscado + ": " + producto);
            }
            cerrar();
        } catch (Exception e) {
            System.err.println("Error al consultar vendidos");
        }
        return producto;
    }

    public void setCantidad(String buscado, int ingresar) {
        PreparedStatement st;
        try {
            conectar();
            conexion.createStatement().execute("UPDATE productos SET cantidad=" + ingresar + " WHERE producto='" + buscado + "'");
            cerrar();
        } catch (Exception e) {
            System.err.println("Error al actualizar cantidad");
        }
    }

    public void setVendidos(String buscado, int ingresar) {
        PreparedStatement st;
        try {
            conectar();
            conexion.createStatement().execute("UPDATE productos SET vendidos=" + ingresar + " WHERE producto='" + buscado + "'");
            cerrar();
        } catch (Exception e) {
            System.err.println("Error al actualizar vendidos");
        }
    }
}
