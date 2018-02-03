//
//  Quote.swift
//  Quote
//
//  Created by John Christopher Briones on 2/3/18.
//  Copyright © 2018 John Christopher Briones. All rights reserved.
//

import Foundation

class Quote {
    
    //MARK: Properties
    
    var author: String
    var quoteText: String
    
    //MARK: Initialization
    
    init?(author: String, quoteText: String) {
        self.author = author
        self.quoteText = quoteText
    }
    
    // Getter and setter for author
    func getAuthor() -> String {
        return author
    }
    
    func setAuthor(author: String) {
        self.author = author
    }
    
    // Getter and setter for quoteText
    func getQuoteText() -> String {
        return quoteText;
    }
    
    func setQuoteText(quoteText: String) {
        self.quoteText = quoteText;
    }
    
    func toString() -> String {
        return "Quote {" + "author='" + author + '\'' + ", quoteText='" + quoteText + '\'' + '}'
    }
}
