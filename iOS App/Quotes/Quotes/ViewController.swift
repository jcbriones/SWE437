//
//  ViewController.swift
//  Quotes
//
//  Created by John Christopher Briones on 2/2/18.
//  Copyright © 2018 John Christopher Briones. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate, XMLParserDelegate {

    @IBOutlet weak var randomQuoteText: UITextView!
    @IBOutlet weak var searchQuote: UISearchBar!
    @IBOutlet weak var searchResult: UITableView!
    @IBOutlet weak var userSearches: UITableView!
    @IBOutlet weak var communitySearches: UITableView!
    
    var quoteArray = [Quote]()
    var filteredQuotes = [Quote]()
    
    var userSearchesList = [String]()
    var communitySearchesList = [String]()
    
    var currentContent = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Initialize variables
        quoteArray = [Quote]()
        
        // Insert Quotes
//        quoteArray.append(Quote(author: "Test 1", quoteText: "Lorem ipsum dolor sit er elit lamet, consectetaur cillium adipisicing pecu, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Nam liber te conscient to factor tum poen legum odioque civiuda.")!)
//        quoteArray.append(Quote(author: "Test 2", quoteText: "Cillium adipisicing pecu, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Nam liber te conscient to factor tum poen legum odioque civiuda.")!)
//        quoteArray.append(Quote(author: "Test 3", quoteText: "Dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Nam liber te conscient to factor tum poen legum odioque civiuda.")!)
//
        // Parse XML file
        beginParsing(urlString: "https://gmu.jcbriones.com/quotes.xml")
        
        // Generate Random Quotes
        let quote = quoteArray[generateNumber()]
        randomQuoteText.text = quote.getQuoteText() + " \n" + quote.getAuthor()
        
        // Copy Quote Array data to filtered Quote Array
        filteredQuotes = quoteArray
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch tableView.tag {
        case 1:
            return filteredQuotes.count
        case 2:
            return userSearchesList.count
        case 3:
            return communitySearchesList.count
        default:
            return 0
        }
        
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        switch tableView.tag {
        case 1:
            let cellIdentifier = "SearchResultTableViewCell"
            
            guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? SearchResultTableViewCell  else {
                fatalError("The dequeued cell is not an instance of SearchResultTableViewCell.")
            }
            
            let quote = filteredQuotes[indexPath.row]
            
            cell.author.text = quote.getAuthor()
            cell.quoteText.text = quote.getQuoteText()
            
            return cell
        case 2:
            let cellIdentifier = "UserSearchesTableViewCell"
            
            guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? UserSearchesTableViewCell  else {
                fatalError("The dequeued cell is not an instance of UserSearchesTableViewCell.")
            }
            
            cell.search.text = userSearchesList[indexPath.row]
            
            return cell
        case 3:
            let cellIdentifier = "CommunitySearchesTableViewCell"
            
            guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? CommunitySearchesTableViewCell  else {
                fatalError("The dequeued cell is not an instance of CommunitySearchesTableViewCell.")
            }
            
            cell.search.text = communitySearchesList[indexPath.row]
            
            return cell
        default:
            return tableView.dequeueReusableCell(withIdentifier: "")!
        }
    }
    
    // Search bar
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        let searchText = searchBar.text!
        filteredQuotes = quoteArray.filter({ quote -> Bool in
            if searchText.isEmpty { return true }
            switch searchBar.selectedScopeButtonIndex {
            case 0:
                return quote.getQuoteText().lowercased().contains(searchText.lowercased())
            case 1:
                return quote.getAuthor().lowercased().contains(searchText.lowercased())
            default:
                return quote.getAuthor().lowercased().contains(searchText.lowercased()) || quote.getQuoteText().lowercased().contains(searchText.lowercased())
            }
            
        })
        searchResult.reloadData()
        
        // User and Community searches
        userSearchesList.append(searchBar.text!)
        communitySearchesList.append(searchBar.text!)
        userSearches.reloadData()
        communitySearches.reloadData()
        searchBar.resignFirstResponder()
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchBar.text = ""
        searchBar.resignFirstResponder()
    }
    
    @IBAction func getAnotherRandomQuoteButton(_ sender: UIButton) {
        let quote = quoteArray[generateNumber()]
        randomQuoteText.text = quote.getQuoteText() + " " + quote.getAuthor()
    }
    
    func generateNumber() -> Int {
        return randomIntFrom(start: 0, to: quoteArray.count - 1)
    }
    
    func randomIntFrom(start: Int, to end: Int) -> Int {
        var a = start
        var b = end
        // swap to prevent negative integer crashes
        if a > b {
            swap(&a, &b)
        }
        return Int(arc4random_uniform(UInt32(b - a + 1))) + a
    }
    
    //MARK: - XML Parsing Code
    func beginParsing(urlString:String){
        guard let myURL = URL(string:urlString) else {
            print("URL not defined properly")
            return
        }
        guard let parser = XMLParser(contentsOf: myURL) else {
            print("Cannot Read Data")
            return
        }
        parser.delegate = self
        if !parser.parse(){
            print("Data Errors Exist:")
            let error = parser.parserError!
            print("Error Description:\(error.localizedDescription)")
            print("Line number: \(parser.lineNumber)")
        }
    }
    
    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String]){
        print("Beginning tag: <\(elementName)>")
        if elementName == "quote"{
            quoteArray.append(Quote(author: "", quoteText: "")!)
        }
        currentContent = ""
    }
    
    func parser(_ parser: XMLParser, foundCharacters string: String){
        currentContent += string
        print("Added to make \(currentContent)")
    }
    
    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        switch elementName {
        case"quote-text":
            quoteArray.last?.setQuoteText(quoteText: currentContent)
        case "author":
            quoteArray.last?.setAuthor(author: currentContent)
        default:
            return
        }
    }
}

