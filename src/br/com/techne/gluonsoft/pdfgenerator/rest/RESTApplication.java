package br.com.techne.gluonsoft.pdfgenerator.rest;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;


/**
 * Realiza disponibiliza os pacotes rest.
 * 
 * @author Rudiney Patrick
 * @version 1.0
 * @since 2016-06-24
 *
 */
@ApplicationPath("/api/rest/gluonsoft/pdfgenerator")
public class RESTApplication extends ResourceConfig {

	/**
	 * Construtor
	 **/
	public RESTApplication (){
	    property("org.glassfish.jersey.server.ServerProperties.PROVIDER_SCANNING_RECURSIVE", Boolean.TRUE);
	    packages("br.com.techne.gluonsoft.pdfgenerator.rest");
	}

}