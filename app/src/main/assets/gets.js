var oIframe = document.getElementById('Frame0');
var doc = oIframe.contentWindow.document;
function getTerm(){
    return doc.getElementByName("semester").value;
}
function getTermText(){
    return doc.getElementByName("semester").options[0].text;
}
function getTimes(){
    var items = doc.getElementById("week").options;
    var result = "";
    for (var i = 0; i < items.length; i++) {
        result = items[i].value+";"
    }
    return result;
}
function getAll(){
    return getTermText()+";"+getTimes();
}
getAll();