package proyexpertos;

import jade.content.onto.*;
import jade.content.schema.*;


public class Ontologia extends Ontology
{
    public static final String ONTOLOGY_NAME = "Farmacia";
    public static final String PRODUCTOS = "Productos";
    public static final String PRODUCTOS_NOMBRE = "nombre";
    public static final String PRODUCTOS_PRECIO = "precio";
    
    public static final String ORDER = "Receta";
    public static final String ORDER_ITEM = "Consulta";
    
    public static final String SELL = "Oferta";
    public static final String SELL_ITEM = "Productos";
    
    private static Ontology theInstance = new Ontologia();

    public static Ontology getInstance() {
        return theInstance;
    }
    
    private Ontologia() {
        super(ONTOLOGY_NAME, BasicOntology.getInstance(),new CFReflectiveIntrospector());
        try {
            add(new ConceptSchema(PRODUCTOS), Medicina.class); //Definicion del concepto a vender
            add(new PredicateSchema(ORDER), Medicina.class); // Definicion del predicado
            add(new AgentActionSchema(SELL), Medicina.class); // Definicion del accion
            //Estructura del esquema para el concepto
            ConceptSchema cs = (ConceptSchema) getSchema(PRODUCTOS);
            cs.add(PRODUCTOS_NOMBRE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(PRODUCTOS_PRECIO, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            // Estrucutra del esquema para el concepto
            PredicateSchema ps = (PredicateSchema) getSchema(ORDER);
            ps.add(ORDER_ITEM, (ConceptSchema) getSchema(PRODUCTOS));
            // Estructura del esquema para la accion del agente
            AgentActionSchema as = (AgentActionSchema) getSchema(SELL);
            as.add(SELL_ITEM, (ConceptSchema) getSchema(PRODUCTOS));
        } catch (OntologyException oe) {
            oe.printStackTrace();
        }
    }
}
