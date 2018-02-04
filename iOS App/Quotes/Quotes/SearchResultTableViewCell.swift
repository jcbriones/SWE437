//
//  SearchResultTableViewCell.swift
//  Quotes
//
//  Created by John Christopher Briones on 2/3/18.
//  Copyright © 2018 John Christopher Briones. All rights reserved.
//

import UIKit

class SearchResultTableViewCell: UITableViewCell {
    
    //MARK: Properties
    @IBOutlet weak var author: UILabel!
    @IBOutlet weak var quoteText: UITextView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
}

