package quotes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * @author Jeff Offutt & Mongkoldech Rajapakdee
 *         Date: Nov, 2009
 *
 * Wiring the pieces together:
 *    quoteserve.java -- The servlet (entry point)
 *    QuoteList.java -- A list of quotes, representing what's read from the XML file
 *                      Used by quoteserve.java
 *    Quote.java -- A simple Quote bean; two entries, author and quote-text
 *                      Used by QuoteList.java
 *    QuoteSaxHandler.java -- Callback methods for the parser, populates QuoteList
 *                      Used by QuoteSaxParser
 *    QuoteSaxParser.java -- Parses the XML file
 *                      Used by quoteserve.java
 *    quotes.js -- JS used by the HTML created in quoteserve
 *    quotes.xml -- Data file, read by QuoteSaxParser
 */
public class quoteserve extends HttpServlet
{
   private static final String FileURL = "https://cs.gmu.edu/~offutt/classes/642/examples/servlets/quotes";
   private static final String FileJavascript = FileURL + "/quotes.js";

   // Data file
   // This shows up in /home/offutt/CS/webapps/WEB-INF/data/ from a terminal window.
   private static final String quoteFileName = "/var/www/CS/webapps/offutt/WEB-INF/data/quotes.xml";

   private static final String thisServlet = "https://cs.gmu.edu:8443/offutt/servlet/quotes.quoteserve";

   // Stores all the quotes from the xml file
   private QuoteList quoteList;

// init() reads the quotes file from disk and stores in quoteList
@Override
public void init() throws ServletException
{
   super.init();
   QuoteSaxParser qParser = new QuoteSaxParser (quoteFileName);
   quoteList = qParser.getQuoteList();
}

// doPost just calls doGet(), where all the action is
@Override
protected void doPost (HttpServletRequest request, HttpServletResponse response)
                      throws ServletException, IOException
{
   doGet (request, response);
}

// doGet() : Stores the search into the in-memory search lists
//           Then prints the HTML page
@Override
protected void doGet (HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException
{
   String searchText  = request.getParameter ("searchText");
   String searchScope = request.getParameter ("searchScope");

   // First step is to set up the two lists in memory
   // 1) The last 5 searches in this session are stored
   // 2) The last 5 searches in this context are stored
   // Get the session / context objects
   // Ask for the string
   // Add the currect search into the objects
   // Get a session object
   HttpSession session = request.getSession();
   // Get a servlet context
   ServletContext servContext = getServletContext();
   // Attribute name
   String sessionAttName = "sessionSearchList";
   String contextAttName = "contextSearchList";

   // Retrieve (or create) the session search list
   ArrayList<String> searchList = (ArrayList<String>) session.getAttribute (sessionAttName);
   if (searchList == null)
   {  // if the session is new, the searchList won't exist.
      searchList = new ArrayList<String>();
      session.setAttribute (sessionAttName, searchList);
   }

   // Retrieve (or create) the context search list
   ArrayList<String> searchContextList = (ArrayList<String>) servContext.getAttribute (contextAttName);
   if (searchContextList == null)
   {  // if the session is new, the searchContextList won't exist.
      searchContextList = new ArrayList<String>();
      servContext.setAttribute (contextAttName, searchContextList);
   }

   // Add the search String into the lists
   if (searchText != null && searchText.length() > 0)
   {
      searchList.add (searchText);
      searchContextList.add (searchText);
   }
   // Remove the oldest searches if more than 5
   if (searchList.size() > 5)
   {
      searchList.remove(0);
   }
   if (searchContextList.size() > 5)
   {
      searchContextList.remove(0);
   }
   // Done with updating the search lists

   /* Print HTML response page */
   response.setContentType ("text/html");
   PrintWriter out = response.getWriter ();
   printHeader  (out);
   printBody    (out);
   printSearch  (out, searchText, searchScope);
   printSearches (out, searchList, searchContextList);
   printFooter  (out);
}

/**
 * Print the header of the HTML page
 * @param out PrintWriter
 * @throws ServletException
 * @throws IOException
*/
private void printHeader (PrintWriter out) throws ServletException, IOException
{
   out.println ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
   out.println ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
   out.println ("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
   out.println ("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />");
   out.println ("<head>");
   out.println (" <title>Quotes Server</title>");
   out.println (" <script type=\"text/javascript\" src=\""+ FileJavascript +"\"></script>");
   out.println ("</head>");

   out.println ("<body onLoad=\"setFocus()\" bgcolor=\"#DDEEDD\">");
}

/**
 * Print the body of HTML
 * @param out PrintWriter
 * @throws ServletException
 * @throws IOException
*/
private void printBody (PrintWriter out)
             throws ServletException, IOException
{
   out.println ("<center><h2>The GMU Quote Generator</h2></center>");
   out.println ("<hr/>");

   Quote quoteTmp = quoteList.getRandomQuote();
   out.println ("<p>");
   out.println ("Random quote of the day:");
   out.println ("<div style=\"width:600px; color:blue; background-color:#99dd99\">");
   out.println ("<br/>&nbsp;&nbsp;&nbsp;&nbsp;" + quoteTmp.getQuoteText());
   out.println ("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&mdash;" + quoteTmp.getAuthor());
   out.println ("</div>");

   out.println ("<br/>");
   out.println ("<form name=\"random\" method=\"post\" action=\"" + thisServlet + "\" >");
   out.println ("  <input type=\"submit\" value=\"Get Another Random Quote\" name=\"submit\"/>");
   out.println ("</form>");
   out.println ("<hr/>");

   // print the main form
   // outer table: one row, one td for the form, one td for the last 5 searches
   out.println ("<table> <!-- Outer table -->");
   out.println ("<tr valign=\"top\"><td>");
   out.println ("<form name=\"quoteServ\" method=\"post\" action=\"" + thisServlet + "\" >");
   out.println ("  <table>");

   out.println ("    <tr>");
   out.println ("      <th>Search :</th>");
   out.println ("      <td>");
   out.println ("        <input type=\"text\" id=\"searchText\" name=\"searchText\" value=\"\" size=\"50\" />");
   out.println ("      </td>");
   out.println ("    </tr>");

   out.println ("    <tr>");
   out.println ("      <th>Scope :</th>");
   out.println ("      <td>");
   out.println ("        <input type=\"radio\" id=\"quote\"  name=\"searchScope\" value=\"quote\"/>");
   out.println ("        <label for=\"quote\">quote</label>");
   out.println ("        <input type=\"radio\" id=\"author\" name=\"searchScope\" value=\"author\"/>");
   out.println ("        <label for=\"author\">author</label>");
   out.println ("        <input type=\"radio\" id=\"both\"   name=\"searchScope\" value=\"both\" checked=\"checked\"/>");
   out.println ("        <label for=\"both\">both</label>");

   out.println ("      </td>");
   out.println ("    </tr>");

   out.println ("    <tr>");
   out.println ("      <th></th>");
   out.println ("      <td align=\"center\">");
   out.println ("        <input type=\"submit\" value=\"search\" name=\"submit\"/>");
   out.println ("        <input type=\"reset\"  value=\"reset\"  name=\"reset\"/>");
   out.println ("      </td>");
   out.println ("    </tr>");

   out.println ("  </table>");
   out.println ("</form>");
}

/**
 * Print the search result
 * @param out PrintWriter
 * @param searchText search String input from user
 * @param searchScope scope for this search
 * @throws ServletException
 * @throws IOException
*/
private void printSearch (PrintWriter out, String searchText, String searchScope)
             throws ServletException, IOException
{
   if (searchText != null && !searchText.equals(""))
   {  // Received a search request
      int searchScopeInt = QuoteList.SearchBothVal; // Default
      if (searchScope != null && !searchScope.equals(""))
      {  // If no parameter value, let it default.
         if (searchScope.equals ("quote"))
         {
            searchScopeInt = QuoteList.SearchTextVal;
         } else if (searchScope.equals ("author"))
         {
            searchScopeInt = QuoteList.SearchAuthorVal;
         } else if (searchScope.equals ("both"))
         {
            searchScopeInt = QuoteList.SearchBothVal;
         }
      }

      QuoteList searchRes = quoteList.search (searchText, searchScopeInt);
      Quote quoteTmp;
      if (searchRes.getSize() == 0)
      {
         out.println ("<p>Your search - <b>"+ searchText +"</b> - did not match any quotes.</p>");
      }
      else
      {
         out.println ("<dl>");
         for (int i = 0; i < searchRes.getSize() ; i++)
         {
            quoteTmp = searchRes.getQuote(i);
            out.println ("<dt>" + quoteTmp.getQuoteText() + "</dt>");
            out.println ("<dd>&mdash;" + quoteTmp.getAuthor() + "</dd>");
         }
         out.println ("</dl>");
      }
   }
   out.println ("</td>");
}

/**
 * Print the recent searches
 * @param out PrintWriter
 * @param sessionList List of recent searches from session
 * @param contextList List of recent searches from servlet context
 * @throws ServletException
 * @throws IOException
 */
private void printSearches (PrintWriter out, ArrayList<String> sessionList, ArrayList<String> contextList)
             throws ServletException, IOException
{
   out.println ("<td width=100>&nbsp;</td>");
   out.println ("<td style=\"text-align:center\">");
   out.println ("  <span style=\"font-weight:bold;font-size:120%\">Recent Searches</span>");
   out.println ("  <table border=1 style=\"background-color:#99dd99\">");
   out.println ("    <tr>");
   out.println ("    <td style=\"text-align:center\">");
   out.println ("    <span style=\"font-weight:bold;font-size:110%\">User Searches</span>");
   out.println ("    <td style=\"text-align:center\">");
   out.println ("    <span style=\"font-weight:bold;font-size:110%\">Community Searches</span>");
   out.println ("    <tr>");
   out.println ("    <td style=\"text-align:left\">");
   out.println ("    <ol>");
   String searchTmp = "";
   for (int i = 0; i < sessionList.size(); i++)
   {  // The ith search string
      searchTmp = sessionList.get (i);
      out.println ("      <li><a href=\"" + thisServlet + "?searchText=" + searchTmp + "&searchScope=both\" >"+searchTmp+"</a></li>");
   }
   out.println ("    </ol>");

   out.println ("    <td style=\"text-align:left\">");

   out.println ("    <ol>");
   for (int i = 0; i < contextList.size(); i++)
   {  // The ith search string
      searchTmp = contextList.get (i);
      out.println ("      <li><a href=\"" + thisServlet + "?searchText=" + searchTmp + "&searchScope=both\" >"+searchTmp+"</a></li>");
   }
   out.println ("    </ol>");
   out.println ("  </tr></table>");

   out.println ("</td></tr></table>");
}

/**
 * Print the footer of HTML page
 * @param out PrintWriter
 * @throws ServletException
 * @throws IOException
 */
private void printFooter (PrintWriter out) throws ServletException, IOException
{
   out.println ("<p style=\"font-size:80%;font-family:monospace; color:#888888\">");
   out.println ("Mongkoldech Rajapakdee &amp; Jeff Offutt");
   out.println ("<br/>November 2009");
   out.println ("</p>");
   out.println ("</body>");
   out.println ("</html>");
}
} // end quoteserve class
