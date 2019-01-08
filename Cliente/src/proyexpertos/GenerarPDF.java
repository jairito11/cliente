package proyexpertos;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GenerarPDF
{
	/**
	 * Genera un documento .pdf para usarlo con el contenido de las fases
	 * @param fase Recibe el contenido de la fase x
	 * @param noFase Recibe el nombre de la fase x
	 * @param direccion Recibe la dirección para guardar los documentos PDF
	 * @throws FileNotFoundException
	 * @throws DocumentException 
	 */
	public void genera( String fase, String noFase, String direccion ) throws FileNotFoundException, DocumentException
	{
		if( direccion.length() != 0 )
		{
			FileOutputStream archivo = new FileOutputStream( direccion + "/" + noFase + ".pdf" );
			Document documento = new Document();
			PdfWriter.getInstance( documento, archivo );
			documento.open();
			documento.add( new Paragraph( fase ) );
			documento.close();
		}
		else
		{
			JOptionPane.showMessageDialog( null, "Ocurrió un error y no generó el PDF.", "ERROR", JOptionPane.ERROR_MESSAGE );
		}
	}
	
	/**
	 * permite al usuario especificar el directorio para guardar
	 * @return Regresa la refencia al archivo
	 */
	private File obtenerDirectorio()
	{
		FileNameExtensionFilter filter = new FileNameExtensionFilter ( "Documento PDF", "pdf"); // creación de un filtro
		// muestra el cuadro de diálogo de archivos, para que el usuario pueda elegir el directorio donde guardar
		JFileChooser selectorDirectorio = new JFileChooser();
		selectorDirectorio.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		selectorDirectorio.setFileFilter( filter );  // se establece el filtro creado para la busqueda de archivos asm
		selectorDirectorio.setDialogTitle( "Guardar archivos..." ); // título de la ventana Abrir
		
		selectorDirectorio.setDialogType( JFileChooser.SAVE_DIALOG );

		int resultado = selectorDirectorio.showOpenDialog( null );

		// si el usuario hizo clic en el botón Cancelar en el cuadro de diálogo, regresa
		if ( resultado == JFileChooser.CANCEL_OPTION )
			System.exit( 1 );

		File nombreDirectorio = selectorDirectorio.getSelectedFile(); // obtiene el directorio seleccionado

		// muestra error si es inválido
		if ( ( nombreDirectorio == null ) || ( nombreDirectorio.getName().equals( "" ) ) )
		{
			JOptionPane.showMessageDialog( null, "Nombre inválido", "Nombre inválido", JOptionPane.ERROR_MESSAGE );
			System.exit( 1 );
		} // fin de if

		return nombreDirectorio;
	} // fin del método obtenerArchivo
	
	/**
	 * Devuelve la ruta del directorio
	 * @return Devuelve la ruta del directorio en un String
	 */
	public String analizaRuta()
	{
		// crea un objeto File basado en la entrada del usuario
		File nombre = obtenerDirectorio();

		if ( nombre.exists() ) // si el nombre existe, muestra información sobre él
		{
			return nombre.getAbsolutePath();
		} // fin de if exterior
		else // no es un directorio, imprime mensaje de error
		{
			JOptionPane.showMessageDialog( null, nombre + " no existe.", "ERROR", JOptionPane.ERROR_MESSAGE );
			return "";
		} // fin de else
	} // fin del método analizarRuta

}