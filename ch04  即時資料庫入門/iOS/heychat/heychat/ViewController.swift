//
//  ViewController.swift
//  heychat
//
//  Created by 申潤五 on 2018/4/21.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import Firebase

class ViewController: UIViewController {

    @IBOutlet weak var nickName: UITextField!
    @IBOutlet weak var msg: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        Auth.auth().signInAnonymously(completion: nil)
        //取得之前暱稱
        if let lastNickname = UserDefaults.standard.string(forKey: "nickname"){
            self.nickName.text = lastNickname
        }
    }



    @IBAction func enterForum(_ sender: UIButton) {
        if let enteredNickname = nickName.text{
            if enteredNickname.count < 2{
                showMsg(msg: "錯誤：暱稱至少兩字元")
                msg.text = "錯誤：暱稱至少兩字元"
            }else{
                if Auth.auth().currentUser != nil{
                    UserDefaults.standard.set(enteredNickname, forKey: "nickname")
                    performSegue(withIdentifier: "goForumList", sender: self)
                }else{
                    showMsg(msg: "錯誤：無法成功連線 ")
                    msg.text = "錯誤：無法成功連線"
                }
            }
        }
    }
}

extension UIViewController{

    func showMsg(msg:String){
        let alert = UIAlertController(title: msg, message: nil, preferredStyle: .alert)
        present(alert, animated: true) {
            alert.dismiss(animated: true, completion: nil)
        }
    }
}


