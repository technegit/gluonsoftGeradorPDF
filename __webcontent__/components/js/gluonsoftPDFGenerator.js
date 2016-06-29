/**
 * gluonsoftPDFGenerator.js
 * 
 * Code for Gluonsfot PDFGenerator
 * 2016-06-24
 * Version 1.0.0
 * 
 */
var pdfDATA = {};
var PAGES = {
                PDFFileName: null,
                Page : {
                            title: null, 
                            pageContent: null
                        }
            };
            
var TABLES = { 
                PDFFileName: null,
                Table : { 
                            tableTitle: null,
                            columnsNames: null, 
                            tableContent: null
                        }
            };


/**
 * 
 * PDF File Generator
 * 
 */ 
var PDFGenerator = function(){
    //var _debugActive = false;
    //var _pdfFileName;
};


PDFGenerator.prototype.setFileName = function(pdfFileName){
    this._pdfFileName = pdfFileName;
};


PDFGenerator.prototype.getFileName = function(){
    return this._pdfFileName;
};


PDFGenerator.prototype.getFileType = function(){
    return ".pdf";
};


PDFGenerator.prototype.getDefaultRestServiceURL = function(){
    return "api/rest/gluonsoft/pdfgenerator/generator";
};


PDFGenerator.prototype.getServiceStatus = function(){
    var result = false;
    var xhr = new XMLHttpRequest();
    var url = this.getDefaultRestServiceURL();
    xhr.open("GET", url, true);
    xhr.setRequestHeader("Content-type", "application/json");
    
    xhr.onreadystatechange = function () { 
        if (xhr.readyState == 4 && xhr.status == 200) {
            var json = JSON.parse(xhr.responseText);
            result = json.PDFGeneratorRESTServiceSTATUS == "OK" ? true : false;
            this.log("Service Status: " + json.PDFGeneratorRESTServiceSTATUS)
        }
    }
    
    //var data = JSON.stringify({"email":"@mail.com","password":"10"});
    //xhr.send(data);
    xhr.send();
};

PDFGenerator.prototype.addNewPage = function(pageTitle, pageLinesContentArray) {
    this.log("on addNewPage... (pageTigle: " + pageTitle + " - pageLinesContentJSON:");
    this.log(pageLinesContentArray);
};


PDFGenerator.prototype.addNewTable = function(tableTitle, tableColumnsNamesJSON, tableContentJSON){
    this.log("on createTable... (tableTitle: " + tableTitle + " - tableContentJSON:");
    
    TABLES.Table.tableTitle = tableTitle;
    TABLES.Table.columnsNames = tableColumnsNamesJSON;
    TABLES.Table.tableContent = tableContentJSON;
    
    this.log(TABLES);
};

PDFGenerator.prototype.createPDF = function(pdfFileName){
    var fileName = (pdfFileName === undefined || pdfFileName.trim() === "") ? new Date().getTime().toString() : pdfFileName;
    
    this.log(fileName);
    if(fileName.indexOf(this.getFileType()) <= -1){
        fileName = fileName + this.getFileType();
    }
    
    this.setFileName(fileName);
    this.log("on createPDFFile... (Filename: " + this.getFileName() + ")");
    TABLES.PDFFileName = this.getFileName();
    
    var xhr = new XMLHttpRequest();
    var url = this.getDefaultRestServiceURL() + '/create';
    xhr.open("POST", url, true);
    xhr.responseType = 'arraybuffer';
    xhr.setRequestHeader("Content-type", "application/json");
    
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";
						    	   
    xhr.onreadystatechange = function () { 
        if (xhr.readyState == 4 && xhr.status == 200) {
            // var json = JSON.parse(xhr.responseText);
            // console.log(xhr.responseText);
            
            var blob = new Blob([xhr.response], {type: "octet/stream"});
            var url = URL.createObjectURL(blob);
            
            a.href = url;
            a.download = fileName;
            a.click();
            window.URL.revokeObjectURL(url);
        }
    }
    
    var data = JSON.stringify(TABLES); //TODO: Somente TABLES por enquanto. Adicionar PAGES e novas melhorias depois
    xhr.send(data);
};

PDFGenerator.prototype.debugON = function(){
    this._debugActive = true;
};


PDFGenerator.prototype.debugOFF = function(){
    this._debugActive = false;
};


PDFGenerator.prototype.isDebugON = function(){
    return this._debugActive;
};

PDFGenerator.prototype.log = function(obj){
    if(this._debugActive){
      console.log(obj);
    }
};

/*
PDFGenerator.prototype.test = function(){
    var dbgInitialState = this.isDebugON();
    
    var pageTitle = "TEST PAGE";
    var pageLinesContent = [
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ultrices euismod arcu, a molestie enim rhoncus eu. Vestibulum suscipit hendrerit vehicula. Cras? imperdiet ut enim a aliquet. Maecenas ac lacinia enim.",
              									"Sed in lectus nunc. Morbi venenatis lacinia magna at tincidunt. Aenean id sem sit amet nulla consequat malesuada. Morbi ac tempor purus, vestibulum rhoncus augue. Suspendisse dolor est, dictum vitae dui et, porta", 
              									"egestas ipsum. Fusce mollis metus mi, quis maximus lectus cursus non. Integer sit amet quam sed felis lacinia facilisis sit amet ac orci. Nulla ut augue porta, posuere urna in, porta quam. Sed vel placerat nisi.",
              									"",
              									"Quisque at sapien molestie, imperdiet lectus vitae, consectetur tortor. Quisque at consequat dui, a feugiat velit. Aenean a tristique turpis, in pellentesque nisi. Duis vitae tortor magna. Sed venenatis fermentum",
              									"lorem eu varius. Aliquam erat volutpat. Praesent auctor hendrerit suscipit. Donec mollis erat tincidunt neque malesuada, et pellentesque augue faucibus. Mauris pharetra sapien vel ligula finibus, fringilla posuere",
              									"urna scelerisque. Vivamus quis odio vel nisl luctus congue non sed mauris. Maecenas molestie consequat libero, sed aliquam risus pharetra non. Suspendisse fermentum et justo nec vulputate.",
              									""
                           ];
    
    var tableTitle = "TEST TABLE";
    var tableContentJSON = {
                              columns : ["Column 1", "Column 2", "Column 3", "Column 4", "Column 5", "Column 6"],
                              celData : ["Content Cel 1", "Content Cel 2", "Content Cel 3", "Content Cel 4", "Content Cel 5", "Content Cel 6"]
                           };
    
    this.debugON();
    this.log(">>> Starting test...");
    this.createPDF("TESTE_FILE");
    this.addNewPage(pageTitle, pageLinesContent);
    this.addNewTable(tableTitle, tableContentJSON);
    this.saveToFile();
    
    
    if(!dbgInitialState){
      this.debugOFF();
    }
};
*/

app.controller('PDFGeneratorController', ['$scope', '$rootScope', '$timeout', '$translate', '$http', 'Notification', function($scope, $rootScope, $timeout, $translate, $http, Notification) {
    $scope.PDFGen = new PDFGenerator();
    $scope.fileName = "";
    
    /**
     * Get the Columns Name from a database table
     */ 
    $scope.getColumnsNamesFromDatasource = function(dataSource){
        var columnsTmp = [];  // Temp Array
      	var columns = [];     // Array of columns
      	
      	if(dataSource === undefined || dataSource.data === undefined || dataSource.data.length === 0){
      	    Notification.warning("Sem dados para exportação!");
      	    console.log("getColumnsNamesFromDatasource: Sem dados para exportação!");
      	    return columns;
      	}
      	
      	var strJSON = JSON.stringify(dataSource.data[0]);
      	var objJSON = JSON.parse(strJSON);
      	
      	// Get the DataSource fields names that will be available in the PDF file
        var elementFieldsFilter = event.srcElement.attributes.getNamedItem("datasourceIgnoredFields");
        var dataSourceToPdf_IgnoredFields = [];  // Array of fields that will be used in the pdf file - defined by user (programmer).
        
        // If user defined fields that must be used in PDF, then it will be executed
        if(elementFieldsFilter){
            var dataFieldsToPdfArray = elementFieldsFilter.value.split(",");
            
            for(key in dataFieldsToPdfArray){
                var field = dataFieldsToPdfArray[key] !== undefined ? dataFieldsToPdfArray[key].trim() : "";
                if(field) dataSourceToPdf_IgnoredFields.push(field);
            }
        }
    
        // Add the datasource fields name to the array 'columnsTmp'
      	for (var key in objJSON) {  // Column names comes in inverted order from datasource
      		  //console.log(' name=' + key + ' value=' + objJSON[key]);
        		if(key !== "$$hashKey"){
        		    columnsTmp.push(key);
        		}
      	}
      	
      	// Add only the fields that will be used in pdf file to the array 'columns'
      	for(var index = columnsTmp.length - 1; index >= 0; index--){  // Revert the column names order
      	    var ignoreColumn = false;
      	    
      	    for(key in dataSourceToPdf_IgnoredFields){ // Filter the fields that will be used in the pdf file.
      	        if(columnsTmp[index] == dataSourceToPdf_IgnoredFields[key]){
      	            ignoreColumn = true;
      	            break;
      	        }
      	    }
      	    
      	    if(!ignoreColumn){
      	      columns.push(columnsTmp[index]);
      	    }
      	}
      	
      	return columns;
    };

    /**
     * Get data from the dataSource
     */ 
    $scope.getDataFromDatasource = function(dataSource){
        if(!(dataSource === undefined || dataSource.data === undefined || dataSource.data.length == 0)){
            var dsLength = dataSource.data.length;
            var columnsNames = this.getColumnsNamesFromDatasource(dataSource);
            var dataValues = [];
            
            //for(key in columnsNames){
            //    console.log(columnsNames[key]);
            //}
            
            for(var i = 0; i < dsLength; i++){
                //console.log(dataSource.data[i]);
                
                for(key in columnsNames){
                  var colName = columnsNames[key];
                  var fieldValue = dataSource.data[i][colName] === undefined ? '' : dataSource.data[i][colName];
                  
                  //console.log(fieldValue);
                  dataValues.push(fieldValue);
                }
          }
        } else {
            Notification.warning("Sem dados para exportação!");
            console.log("getDataFromDatasource: Sem dados para exportação!");
        }
        
        return dataValues;
    };
    
    /**
     * Return if service is ok (true) or not (false)
     */ 
    $scope.isServiceOK = function(){
        return this.PDFGen.getServiceStatus();  
    };
    
    /**
     * Creates the PDF File from a Datasource object
     */ 
    $scope.createPDF = function(dataSource){
        if(dataSource === undefined){
            Notification.error("DataSource inválido!");
            console.log("createPDF: DataSource inválido!");  
            return;
        } else if(dataSource.data === undefined || dataSource.data.length == 0){
            Notification.warning("Sem dados para exportação!");
            console.log("createPDF: Sem dados para exportação!");
            return;
        }
        
        var pdfNameComplement = new Date().getTime().toString();
        var tableTitle = dataSource.name !== undefined ? dataSource.name : "";
        var columnsNameJSON = this.getColumnsNamesFromDatasource(dataSource);
        var celValuesJSON = this.getDataFromDatasource(dataSource);
        
        // this.PDFGen.addNewTable("tableTitle_1", ["tableColumnsNamesJSON_1", "tableColumnsNamesJSON_2"], ["tableContentJSON_1", "tableContentJSON_2"]);
        // this.PDFGen.createPDF("PDF_" + pdfNameComplement);
        this.PDFGen.addNewTable(tableTitle.toUpperCase(), columnsNameJSON, celValuesJSON);
        this.PDFGen.createPDF("PDF_" + tableTitle + pdfNameComplement);
    };
    
}]);