package br.com.techne.gluonsoft.pdfgenerator;


/* ====================================================================
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==================================================================== */


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
 * Used Lib: Apache PDFBox 2.0.2 (https://pdfbox.apache.org/)
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
  	 * Create a new PDF document
  	 * @param filename File name.
  	 */
  	public void createDocument(String fileName){
    		PDDocument doc = new PDDocument();
    		
    		this.setPdfFileName(fileName);
    		this.setPdfDocument(doc);
  	}
  	
  	/**
  	 * Add a new page to the PDF document
  	 * @param page Object PDPage to be added to the PDF document.
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
    		
    		PDPageContentStream pageContent = new PDPageContentStream(this.getPdfDocument(), page); // Page's Stream
    		
    		int line = 1;
    		
    		// Add the page's title
    		if(pageTitle != null){
    			pageContent.beginText();
    			pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);
    			pageContent.newLineAtOffset(PAGE_X_ALIGN_CENTER, PAGE_INITIAL_Y_POSITION); // CENTER
    			pageContent.showText(pageTitle);	// Title
    			pageContent.endText();
    			
    			pageContent.beginText();
    			pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);
    			pageContent.newLineAtOffset(PAGE_MARGIN, PAGE_INITIAL_Y_POSITION - 10 * (line++)); // pageContent.newLineAtOffset(PAGE_MARGIN, PAGE_INITIAL_Y_POSITION);
    			pageContent.showText("");	// Line after title
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
  	 * Create a page with table.
  	 * @param tableTitle Table Title
  	 * @param tableColumnsName	Array of Strings with table columns titles.
  	 * @param strTableContent Array of Strings with the table data.
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
    		
    		// Draw the lines
    		float nextY = PAGE_INITIAL_Y_POSITION;
    		for(int r = 0; r <= tableRows; r++){
    			pageContent.drawLine(margin, nextY, margin + tableWidth, nextY);
    			nextY -= rowHeigth;
    		}
    		
    		// Draw the columns
  	    float nextX = margin;
  	    for (int i = 0; i <= tableColumns; i++) {
  	    	pageContent.drawLine(nextX, PAGE_INITIAL_Y_POSITION, nextX, PAGE_INITIAL_Y_POSITION - tableHeight);
  	        nextX += tableColWidth;
  	    }
  	    
  	    pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);	// Initial Font for the columns' titles
  	    
  	    float textPosX = margin + tableCelMargin;
  	    float textPosY = PAGE_INITIAL_Y_POSITION - 15;
  	    
  	    // Title
  	    float centerX = tableWidth / 2 - (margin * 2);
  	    float xAlignLeft = margin;
  	    pageContent.beginText();
  	    pageContent.newLineAtOffset(xAlignLeft, PAGE_INITIAL_Y_POSITION + 5);
  	    pageContent.showText(tableTitle);
  	    pageContent.endText();
  		
  	    // Columns' names
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
      	
      	// Cels' content (Add the text)
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
  	 * Create a page with table.
  	 * @param tableTitle Table Title
  	 * @param tableColumnsName	Array of Strings with table columns titles.
  	 * @param strTableContent Array of Strings with the table data.
  	 * @throws IOException
  	 */
    /**
     * Create a page with table.
     * @param tableTitle Table Title
     * @param strTableContent	Array of Strings, where the column's titles goes into the first array line and the data goes into others lines.
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
      	
      	// Draw the lines
      	float nextY = PAGE_INITIAL_Y_POSITION;
      	for(int r = 0; r <= tableRows; r++){
      		pageContent.drawLine(margin, nextY, margin + tableWidth, nextY);
      		nextY -= rowHeigth;
      	}
    	
    	  // Draw the columns
        float nextX = margin;
        for (int i = 0; i <= tableColumns; i++) {
        	pageContent.drawLine(nextX, PAGE_INITIAL_Y_POSITION, nextX, PAGE_INITIAL_Y_POSITION - tableHeight);
            nextX += tableColWidth;
        }
        
        pageContent.setFont(FONT_BOLD, FONT_SIZE_DEFAULT);	// Fonte inicial para o título das colunas
        
        float textPosX = margin + tableCelMargin;
        float textPosY = PAGE_INITIAL_Y_POSITION - 15;
        
        // Title
        float centerX = tableWidth / 2 - (margin * 2);
        pageContent.beginText();
        pageContent.newLineAtOffset(centerX, PAGE_INITIAL_Y_POSITION + 5);
        pageContent.showText(tableTitle);
        pageContent.endText();
    	
      	// Cels' content (Add the text)
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
    * Break a line into others.
    * @param line String with line content
    * @param lineMaxSize Line's Max size
    * @return An array of Strings where each value correspond to a new line.
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
    		  (c == ' ' || c == ','  || c == ';' || c == '.' || c == '!' || c == '?')) {	// Add to the array if max length reached and the actual character is none of espace, dot, comma...
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
