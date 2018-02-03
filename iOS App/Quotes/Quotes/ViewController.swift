//
//  ViewController.swift
//  Quotes
//
//  Created by John Christopher Briones on 2/2/18.
//  Copyright © 2018 John Christopher Briones. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var randomQuoteText: UITextView!
    @IBOutlet weak var searchQuote: UISearchBar!
    @IBOutlet weak var searchResult: UITextView!
    
    var randomGen = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        var quoteCount = 1
        // Generate Random Number
        randomGen = randomIntFrom(start: 0, to: quoteCount)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func getAnotherRandomQuoteButton(_ sender: UIButton) {
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
}

