//
//  ViewController.swift
//  helloAna
//
//  Created by 申潤五 on 2018/4/9.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import Firebase


class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()


    }

    override func viewDidAppear(_ animated: Bool) {
        super .viewDidAppear(animated)

        if let likeGame = UserDefaults.standard.string(forKey: "likeGame"){
            Analytics.setUserProperty(likeGame, forName: "likeGame")
        }else{
            let alerView = UIAlertController(title: "你喜歡遊戲嗎?", message: nil, preferredStyle: .alert)
            alerView.addAction(UIAlertAction(title: "喜歡", style: .default, handler: { (action) in
                UserDefaults.standard.set("true", forKey: "likeGame")
                Analytics.setUserProperty("true", forName: "likeGame")
            }))
            alerView.addAction(UIAlertAction(title: "不喜歡", style: .default, handler: { (action) in
                UserDefaults.standard.set("false", forKey: "likeGame")
                Analytics.setUserProperty("false", forName: "likeGame")
            }))
            present(alerView, animated: true, completion: nil)
        }
    }


    @IBAction func buttonTouched(_ sender: Any) {
        Analytics.logEvent("button", parameters: nil)
    }

    @IBAction func sliderTouched(_ sender: Any) {
        let slider = sender as! UISlider
        Analytics.logEvent("sliderClick", parameters: ["value":"\(slider.value)"])
    }
    
    @IBAction func switchValueChanged(_ sender: UISwitch) {
        Analytics.logEvent("switch", parameters: nil)
    }    
}

