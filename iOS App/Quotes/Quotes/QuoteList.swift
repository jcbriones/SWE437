//
//  QuoteList.swift
//  Quotes
//
//  Created by John Christopher Briones on 2/3/18.
//  Copyright © 2018 John Christopher Briones. All rights reserved.
//

import Foundation

class QuoteList {
    
    //MARK: Properties
    
    var quoteArray: [Quote]
    
    //MARK: Initialization
    init?() {
        self.quoteArray = [Quote]()
    }
    
    // Called when a quote is found, added to the array
    func setQuote(q: Quote) {
        quoteArray.append(q);
    }
    
    // Current size of the quote list
    func getSize() -> Int {
        return quoteArray.count;
    }
    
    // Returns the ith quote from the list
    func getQuote(i: Int) -> Quote {
        return quoteArray[i];
    }
    
    /**
     * Search the quotes in the list, based on searchString
     * @param searchString String input for search
     * @param mode search in the author, quotr, or both
     * @return QuoteList containing the search results (may be multiple quotes)
     */
    func search(searchString: String, mode: Int) -> QuoteList {
        var returnQuote = QuoteList()
        var quote: Quote
        for i in 0...getSize() {
            quote = quoteArray[i];
            if (mode == SearchAuthorVal && quote.getAuthor().toLowerCase().indexOf (searchString.toLowerCase()) != -1) {
                // Found a matching author, save it
                // print("Matched Author ");
                returnQuote?.setQuote(q: quote);
            }
            else if (mode == SearchTextVal && quote.getQuoteText().toLowerCase().indexOf (searchString.toLowerCase()) != -1) {
                // Found a matching quote, save it
                // print("Matched Text ");
                returnQuote?.setQuote(q: quote);
            }
            else if ((mode == SearchBothVal) && (quote.getAuthor().toLowerCase().indexOf (searchString.toLowerCase()) != -1 || quote.getQuoteText().toLowerCase().indexOf (searchString.toLowerCase()) != -1)) {
                // Found a matching author or quote, save it
                // print("Matched Both ");
                returnQuote?.setQuote(q: quote);
            }
        }
        return returnQuote!;
    }
    
    /**
     * Retuen a random quote object from the list.
     * @return a random Quote
     */
    func getRandomQuote() -> Quote {
        let i = randomIntFrom(start: 0, to: quoteArray.count-1)
        return quoteArray[i];
    }
    
    /**
     * Generate a random number from start to end
     * @return a random Int
     */
    func randomIntFrom(start: Int, to end: Int) -> Int {
        var a = start
        var b = end
        // swap to prevent negative integer crashes
        if a > b {
            swap(&a, &b)
        }
        return Int(arc4random_uniform(UInt32(b - a + 1))) + a
    }
}
