#Gluonsoft PDF Files Generator
##gluonsoftGeradorPDF

#Introduction
This gluonsoft exports json data into a PDF file.
It uses:
- Apache PDFBox 2.0.2 library, published under APACHE 2.0 LICENSE, to build PDF files. 
- Jersey REST 2.2.1 for Web Service REST.

###How to use
An html tag Button 'PDF' will be added to the project. This button is responsible for calling an javascript function that calls
a rest service to generate the pdf file.
You only need to feed the button properties with the right data info ('datasourceobject' and 'datasource-Ignored-Fields'), as the example below:

###Example
```
<!--
	datasourceobject = Datasource Object Name
	datasource-Ignored-Fields = name of datasource's fields that will be ignored (fields that will not be printed in the pdf file).
-->
<button class="btn btn-danger fa fa-file-pdf-o" 
        ng-click="createPDF(datasourceobject)"
        datasource-Ignored-Fields="">PDF</button>

```

