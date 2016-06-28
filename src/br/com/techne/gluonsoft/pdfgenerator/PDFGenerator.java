package br.com.techne.gluonsoft.pdfgenerator;


import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


/**
 * Classe Manipuladora de arquivos PDF
 * 
 * @author Rudiney Patrick
 * @version 1.0
 * @since 2016-06-23
 *
 */
public class PDFGenerator {
  
    	
  	// Page
  	final float PAGE_INITIAL_Y_POSITION = 800f;	// Y Initial position
  	final float PAGE_MARGIN = 20f; 				      // PAGE MARGIN
  	
  	// Fonts
  	final int FONT_SIZE_DEFAULT = 12;                                  // Default Font Size
  	final PDFont FONT_PLAIN = PDType1Font.TIMES_ROMAN;                 // Plain Font
  	final PDFont FONT_BOLD = PDType1Font.TIMES_BOLD;                   // Bold Font
  	final PDFont FONT_ITALIC = PDType1Font.TIMES_ITALIC;               // Italic Font
  	final PDFont FONT_BOLD_ITALIC = PDType1Font.TIMES_BOLD_ITALIC;     // Bold and Italic Font
  			
  	// PDF Document
  	private PDDocument pdfDocument = null;
  	
  	// PDF File name
  	private String pdfFileName;
  	
  	// PDF Pages
  	private ArrayList<PDPage> PAGES_LIST = new ArrayList<>();
  	
  	private int _MAX_LINE_CHARACTERS = 110;                           // Max characters by line
  	
  	
  	/**
  	 * Construtor
  	 **/
  	public PDFGenerator (){
  	    
  	}
  	
  	/*
  	 * *** Getters and Setters ***
  	 */
  	
  	/**
  	 * @return the pdfDocument
  	 */
  	public PDDocument getPdfDocument() {
  		  return this.pdfDocument;
  	}
  
  	/**
  	 * @param pdfDocument the pdfDocument to set
  	 */
  	private void setPdfDocument(PDDocument pdfDocument) {
  		  this.pdfDocument = pdfDocument;
  	}
  	
  	
  	/**
  	 * @return the pdfFileName
  	 */
  	public String getPdfFileName() {
  		  return this.pdfFileName;
  	}
  
  	/**
  	 * @param pdfFileName the pdfFileName to set
  	 */
  	private void setPdfFileName(String pdfFileName) {
  		  this.pdfFileName = pdfFileName;
  	}
  
    
  	/*
  	 * *** Others methods ***
  	 */
  	
  	/**
  	 * Cria um novo documento PDF
  	 * @param filename Nome do arquivo a ser criado.
  	 */
  	public void createDocument(String fileName){
    		PDDocument doc = new PDDocument();
    		
    		this.setPdfFileName(fileName);
    		this.setPdfDocument(doc);
  	}
  	
  	/**
  	 * Adiciona uma nova página ao documento PDF
  	 * @param page Objeto PDPage referente à nova página a ser criada.
  	 */
  	public void addNewPage(PDPage page){
    		PAGES_LIST.add(page);
    		this.getPdfDocument().addPage(page);
  	}
  	
  	/**
  	 * Add a new page to the PDF document
  	 * @param title
  	 * @param strLineContent Text to be added to the page
  	 * @throws IOException 
  	 */
  	public void addNewPage(String pageTitle, String[] strLineContent) throws IOException{
    		PDPage page = new PDPage(PDRectangle.A4);
    		this.addNewPage(page);
    		
    		PDRectangle rect = new PDRectangle();
    		rect = page.getMediaBox();
    
    		final float PAGE_MAX_WIDTH = rect.getWidth() - (2 * PAGE_MARGIN);
    		final float PAGE_MAX_HEIGHT = rect.getHeight();
    		
    		final float PAGE_X_ALIGN_CENTER = PAGE_MAX_WIDTH / 2; // (PAGE_MAX_WIDTH + (2 * PAGE_MARGIN)) / 2 ;
    		
    		PDPageContentStream pageContent = new PDPageContentStream(this.getPdfDocument(), page); // Stream da página
    		
    		int line = 1;
    		
    		// Add the page's title
    		if(pageTitle != null){
    			pageContent.beginText();
    			pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);
    			pageContent.newLineAtOffset(PAGE_X_ALIGN_CENTER, PAGE_INITIAL_Y_POSITION); // CENTER
    			pageContent.showText(pageTitle);	// Título
    			pageContent.endText();
    			
    			pageContent.beginText();
    			pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);
    			pageContent.newLineAtOffset(PAGE_MARGIN, PAGE_INITIAL_Y_POSITION - 10 * (line++)); // pageContent.newLineAtOffset(PAGE_MARGIN, PAGE_INITIAL_Y_POSITION);
    			pageContent.showText("");	// Linha em branco após o título
    			pageContent.endText();
    		}
    		
    		// Add the page's content
    		if(strLineContent != null && strLineContent.length > 0){
    			for(String strLine : strLineContent){
    				ArrayList<String> newLines = autoBreakLineIntoOthers(strLine, _MAX_LINE_CHARACTERS); // Break a text line into others lines to fit into page width.
    				
    				for(String str : newLines){
    					pageContent.beginText();
    					pageContent.setFont(FONT_PLAIN, FONT_SIZE_DEFAULT);
    					pageContent.newLineAtOffset(PAGE_MARGIN, PAGE_INITIAL_Y_POSITION - 10 * (line++));
    					pageContent.showText(str);
    					pageContent.endText();
    				}
    			}
    		}
    	
    		pageContent.close();
  	}
	
	  
  	/**
  	 * Cria uma tabela com o conteúdo recebido.
  	 * @param tableTitle Título da tabela
  	 * @param tableColumnsName	Array de Strings contendo os titulos de colunas da tabela.
  	 * @param strTableContent Array de Strings contendo os dados da tabela.
  	 * @throws IOException
  	 */
  	@SuppressWarnings("deprecation")
  	public void addNewTable(String tableTitle, ArrayList<String>tableColumnsName, ArrayList<String> strTableContent) throws IOException{
  		PDPage tablePage = new PDPage(PDRectangle.A4);
  		
  		PDRectangle rect = new PDRectangle();
  		rect = tablePage.getMediaBox();
  		
  		this.getPdfDocument().addPage(tablePage);
  		
  		PDPageContentStream pageContent = new PDPageContentStream(this.getPdfDocument(), tablePage);
  		
  		final float margin = 20f;
  		final int tableRows = strTableContent.size();
  		final int tableColumns = tableColumnsName.size();
  		final float rowHeigth = 20f;
  		final float tableWidth = rect.getWidth() - (2 * margin);
  		final float tableHeight = rowHeigth * tableRows;
  		final float tableColWidth = tableWidth / (float)tableColumns;
  		final float tableCelMargin = 5f;
  		
  		// Desenha as linhas
  		float nextY = PAGE_INITIAL_Y_POSITION;
  		for(int r = 0; r <= tableRows; r++){
  			pageContent.drawLine(margin, nextY, margin + tableWidth, nextY);
  			nextY -= rowHeigth;
  		}
  		
  		// Desenha as colunas
  	    float nextX = margin;
  	    for (int i = 0; i <= tableColumns; i++) {
  	    	pageContent.drawLine(nextX, PAGE_INITIAL_Y_POSITION, nextX, PAGE_INITIAL_Y_POSITION - tableHeight);
  	        nextX += tableColWidth;
  	    }
  	    
  	    pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);	// Fonte inicial para o título das colunas
  	    
  	    float textPosX = margin + tableCelMargin;
  	    float textPosY = PAGE_INITIAL_Y_POSITION - 15;
  	    
  	    // Titulo
  	    float centerX = tableWidth / 2 - (margin * 2);
  	    pageContent.beginText();
  	    pageContent.newLineAtOffset(centerX, PAGE_INITIAL_Y_POSITION + 5);
  	    pageContent.showText(tableTitle);
  	    pageContent.endText();
  		
  	    // Nome das colunas
  	    for(int i = 0; i < tableColumnsName.size(); i++){
  	    	String columnName = tableColumnsName.get(i);
  	    	System.out.println(columnName);
  	    	
  	    	pageContent.beginText();
      		pageContent.newLineAtOffset(textPosX, textPosY);
      		pageContent.showText(columnName);
      		pageContent.endText();
      		textPosX += tableColWidth;
  	    }
  	    
  //	    textPosY -= rowHeigth;
  //    	textPosX = margin + tableCelMargin;
      	
      	// Conteudo das células (Adiciona o texto)
  	    int actualCol = 0;
  	    pageContent.setFont(FONT_PLAIN, FONT_SIZE_DEFAULT);
  	    for(int i = 0; i < strTableContent.size(); i++){
  	    	if(actualCol % tableColumns == 0){
  	    		actualCol = 0;
  		    	textPosY -= rowHeigth;
  		    	textPosX = margin + tableCelMargin;
  	    	}
  	    	
  	    	String celText = strTableContent.get(i);
  	    	System.out.println(celText);
  	    	pageContent.beginText();
      		pageContent.newLineAtOffset(textPosX, textPosY);
      		pageContent.showText(celText);
      		pageContent.endText();
      		textPosX += tableColWidth;
      		
  	    	actualCol++;
  	    }
  	    
  	    pageContent.close();
  	}
	  
	  
    /**
     * Cria uma tabela com o conteúdo recebido.
     * @param tableTitle Título da tabela
     * @param strTableContent	Array de Strings, onde os titulos das colunas ficam na primeira linha e os dados nas demais linhas.
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public void addNewTable(String tableTitle, String[][] strTableContent) throws IOException{
      	PDPage tablePage = new PDPage(PDRectangle.A4);
      	
      	PDRectangle rect = new PDRectangle();
      	rect = tablePage.getMediaBox();
      	
      	this.getPdfDocument().addPage(tablePage);
      	
      	PDPageContentStream pageContent = new PDPageContentStream(this.getPdfDocument(), tablePage);
      	
      	final float margin = 20f;
      	final int tableRows = strTableContent.length;
      	final int tableColumns = strTableContent[0].length;
      	final float rowHeigth = 20f;
      	final float tableWidth = rect.getWidth() - (2 * margin);
      	final float tableHeight = rowHeigth * tableRows;
      	final float tableColWidth = tableWidth / (float)tableColumns;
      	final float tableCelMargin = 5f;
      	
      	// Desenha as linhas
      	float nextY = PAGE_INITIAL_Y_POSITION;
      	for(int r = 0; r <= tableRows; r++){
      		pageContent.drawLine(margin, nextY, margin + tableWidth, nextY);
      		nextY -= rowHeigth;
      	}
    	
    	  // Desenha as colunas
        float nextX = margin;
        for (int i = 0; i <= tableColumns; i++) {
        	pageContent.drawLine(nextX, PAGE_INITIAL_Y_POSITION, nextX, PAGE_INITIAL_Y_POSITION - tableHeight);
            nextX += tableColWidth;
        }
        
        pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);	// Fonte inicial para o título das colunas
        
        float textPosX = margin + tableCelMargin;
        float textPosY = PAGE_INITIAL_Y_POSITION - 15;
        
        // Titulo
        float centerX = tableWidth / 2 - (margin * 2);
        pageContent.beginText();
        pageContent.newLineAtOffset(centerX, PAGE_INITIAL_Y_POSITION + 5);
        pageContent.showText(tableTitle);
        pageContent.endText();
    	
      	// Conteudo das células (Adiciona o texto)
        for(int l = 0; l < strTableContent.length; l++){
        	for(int c = 0; c < strTableContent[l].length; c++){
        		String celText = strTableContent[l][c];
        		
        		if(l > 0){
        			pageContent.setFont(FONT_PLAIN, FONT_SIZE_DEFAULT);
        		}
        		
        		pageContent.beginText();
        		pageContent.newLineAtOffset(textPosX, textPosY);
        		pageContent.showText(celText);
        		pageContent.endText();
        		textPosX += tableColWidth;
        	}
        	
        	textPosY -= rowHeigth;
        	textPosX = margin + tableCelMargin;
        }
        
        pageContent.close();
    }	  
	  
    /**
    * Quebra uma linha em várias linhas, de acordo com o lineMaxSize informado.
    * @param line String contendo o conteúdo da linha
    * @param lineMaxSize Tamanho da linha
    * @return Um array de Strings onde cada valor do array corresponde a uma nova linha
    */
    public ArrayList<String> autoBreakLineIntoOthers(String line, int lineMaxSize){
    	ArrayList<String> resultLines = new ArrayList<>();
    	
    	if(line == null || line.length() <= lineMaxSize || lineMaxSize <= 0){
    		resultLines.add(line);
    		
    		return resultLines;
    	}
    	
    	StringBuffer sbTemp = new StringBuffer();
    	
    	for(int pos = 0; pos < line.length(); pos++){
    		char c = line.charAt(pos);
    		sbTemp.append(c);
    		
    		
    		if(sbTemp.length() >= lineMaxSize && 
    		  (c == ' ' || c == ','  || c == ';' || c == '.' || c == '!' || c == '?')) {	// Adiciona ao array se max length for alcançado e o caractere atual não for espaço ou vírgula
    			resultLines.add(sbTemp.toString());
    			sbTemp.delete(0, sbTemp.length());
    		} else {
    			if(pos == (line.length() - 1)){
    				resultLines.add(sbTemp.toString());
    				sbTemp.delete(0, sbTemp.length());
    			}
    		}
    	}
    	
    	return resultLines;
    }
	
	 /**
  	* Save the PDF document created to a file.
  	* @throws IOException
  	*/
    public void saveToFile() throws IOException{
    	this.getPdfDocument().save(this.getPdfFileName());
    	this.getPdfDocument().close();
    }
    
    /**
  	* Save the PDF document created to a OutputStream object.
  	* @throws IOException
  	*/
    public OutputStream saveToOutputStream() throws IOException{
  		ByteArrayOutputStream output = new ByteArrayOutputStream();
  		this.getPdfDocument().save(output);
  		
  		return output;
	  }
	
    /**
     * Returns the total of pages added to the document
     */ 
    public int getTotalPages(){
    	return this.PAGES_LIST.size();
    }
}
