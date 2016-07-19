package br.com.techne.gluonsoft.pdfgenerator.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

import java.io.ByteArrayOutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Iterator;
import java.util.ArrayList;

import br.com.techne.gluonsoft.pdfgenerator.PDFGenerator;


/**
 * Classe que representa ...
 * 
 * @author Rudiney Patrick
 * @version 1.0
 * @since 2016-06-24
 *
 */
@Path("/generator")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON})
public class PDFGeneratorREST {

    private JSONParser jsonParser = new JSONParser();
    
    /**
     *  Return the Service Status
     */
	  @GET
	  @Path("/")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response getServiceStatus(){
	      return Response.ok("{\"PDFGeneratorRESTServiceSTATUS\":\"ok\"}").build();  
	  }
	  
	  
	  @POST
	  @Path("/create")
	  @Produces(MediaType.APPLICATION_OCTET_STREAM)
	  public Response generatePDF(String jsonPDFData){
	      JSONObject jsonObject;
	      
	      try {
	          jsonObject = (JSONObject)jsonParser.parse(jsonPDFData);
	      } catch(ParseException pe){
	          throw new RuntimeException(pe);
	      }
	      
	      verifyParamsOfJSONData(jsonObject);
	      
	      String fileName = (String)jsonObject.get("PDFFileName");
	      
	      JSONObject jsonTable = (JSONObject)jsonObject.get("Table");
	      String tableTitle = (String)jsonTable.get("tableTitle");
        JSONArray tableColumns = (JSONArray)jsonTable.get("columnsNames");
        JSONArray tableData = (JSONArray)jsonTable.get("tableContent");
	     
	      ArrayList<String> tableColumnsName = new ArrayList<>();
	      ArrayList<String> strTableContent = new ArrayList<>();
	      
	      Iterator<String> strColumns = tableColumns.iterator();
	      while(strColumns.hasNext()){
	          String col = strColumns.next();
	          //System.out.println(col);
	          tableColumnsName.add(col);
	      }
	      
	      Iterator<String> strData = tableData.iterator();
	      while(strData.hasNext()){
	          //String data = strData.next();
	          Object objData = strData.next();
	          String data = objData != null ? objData.toString() : "";
	          
	          strTableContent.add(data);
	      }
	      
	      ByteArrayOutputStream out = null;
	      
	      try {
    	      PDFGenerator pdfGen = new PDFGenerator();
    	      pdfGen.createDocument(fileName);
    	      pdfGen.addNewTable(tableTitle, tableColumnsName, strTableContent);
    	      
    	      out = (ByteArrayOutputStream) pdfGen.saveToOutputStream();
    	      
    	       //System.out.print(out.toByteArray());
	      } catch(Exception e){
	          throw new WebApplicationException("File Not Found !!");
	      }
	      
	      return Response.ok(out.toByteArray())
                       .header("content-disposition","attachment; filename = " + fileName)
                       .build();
	  }
	  
	  /**
     * @brief verifica parametro em comum nos serviços acima
     * data deve conter atributos verificados abaixo 
     * @param data
     */
    @SuppressWarnings("all")
    private void verifyParamsOfJSONData(JSONObject data){
        JSONObject jsonTable = (JSONObject)data.get("Table");
	      String tableTitle = (String)jsonTable.get("tableTitle");
        JSONArray tableColumns = (JSONArray)jsonTable.get("columnsNames");
        JSONArray tableData = (JSONArray)jsonTable.get("tableContent");
        
      	if(jsonTable == null || tableColumns == null || tableColumns.size() == 0 || tableData == null || tableData.size() == 0){
      	    throw new WebApplicationException(Response.status(404).entity("Atributos de \"Table\" não foram encontrados!").build());
      	}
    }
    
    
	  @GET
	  @Path("/testPdfCreation")
	  @Produces(MediaType.APPLICATION_OCTET_STREAM)
	  public Response testGeneratePDF(){ // String jsonDataObject
	      
	      String fileName = "teste.pdf";
	      String pageTitle = "PAGE TITLE";
	      String[] strLineContent = {
                  									"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ultrices euismod arcu, a molestie enim rhoncus eu. Vestibulum suscipit hendrerit vehicula. Cras? imperdiet ut enim a aliquet. Maecenas ac lacinia enim.",
                  									"Sed in lectus nunc. Morbi venenatis lacinia magna at tincidunt. Aenean id sem sit amet nulla consequat malesuada. Morbi ac tempor purus, vestibulum rhoncus augue. Suspendisse dolor est, dictum vitae dui et, porta", 
                  									"egestas ipsum. Fusce mollis metus mi, quis maximus lectus cursus non. Integer sit amet quam sed felis lacinia facilisis sit amet ac orci. Nulla ut augue porta, posuere urna in, porta quam. Sed vel placerat nisi.",
                  									"",
                  									"THE END"
	                                };
	      
	      ByteArrayOutputStream out = null;
	      
	      
	      try {
    	      PDFGenerator pdfGen = new PDFGenerator();
    	      pdfGen.createDocument(fileName);
    	      pdfGen.addNewPage(pageTitle, strLineContent);
    	      
    	      out = (ByteArrayOutputStream) pdfGen.saveToOutputStream();
    	      
    	       System.out.print(out.toByteArray());
	      } catch(Exception e){
	          throw new WebApplicationException("File Not Found !!");
	      }
	      
	      return Response.ok(out.toByteArray())
                       .header("content-disposition","attachment; filename = " + fileName)
                       .build();
	  }
}
