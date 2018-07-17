//
//  DiscItemTableViewCell.swift
//  heychat
//
//  Created by 申潤五 on 2018/4/24.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit

class DiscItemTableViewCell: UITableViewCell {



    @IBOutlet weak var nickname: UILabel!
    @IBOutlet weak var lastUpdate: UILabel!
    @IBOutlet weak var content: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
