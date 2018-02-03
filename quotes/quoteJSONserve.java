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
 * @author Modified by John Christopher Briones
 *         Date: Feb, 2018
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
 public class quoteJSONserve extends HttpServlet
 {
	 private static final String FileURL = "https://cs.gmu.edu/~offutt/classes/642/examples/servlets/quotes";
	 private static final String FileJavascript = FileURL + "/quotes.js";

	 // Data file
	 private static final String quoteFileName = "./quotes.xml";

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
	 printRandomQuote(out);
	 printSearch  (out, searchText, searchScope);
	 printSearches (out, searchList, searchContextList);
	}

	/**
	* Print a random quote
	* @param out PrintWriter
	* @throws ServletException
	* @throws IOException
	*/
	private void printRandomQuote (PrintWriter out)
	throws ServletException, IOException
	{
		Quote quoteTmp = quoteList.getRandomQuote();
		quoteTmp.getQuoteText();
		quoteTmp.getAuthor();
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
		String searchTmp = "";
		for (int i = 0; i < sessionList.size(); i++)
		{  // The ith search string
			searchTmp = sessionList.get (i);
			out.println ("link:\" =>  \"" + thisServlet + "?searchText=" + searchTmp + "&searchScope=both\" title => "+searchTmp+"");
		}
		for (int i = 0; i < contextList.size(); i++)
		{  // The ith search string
			searchTmp = contextList.get (i);
			out.println ("link:\" => \"" + thisServlet + "?searchText=" + searchTmp + "&searchScope=both\", title => "+searchTmp+"");
		}
	}

} // end quoteJSONserver class