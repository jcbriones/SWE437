 // Function borrowed from Thilo Rusche
 function ClearForm ()
 {  // Set all the form values to blank.
    var form = document.forms[0];
    for (i=0; i < form.elements.length; i++)
    {
       if (form.elements[i].name != "submit" &&
           form.elements[i].name != "clear")
          form.elements[i].value = "";
  }
  return false;
 }

 // Puts the mouse focus in the right box
 // Add onLoad="setFocus()" to <body>
 function setFocus()
 {
    document.quoteServ.searchText.focus();
 }
