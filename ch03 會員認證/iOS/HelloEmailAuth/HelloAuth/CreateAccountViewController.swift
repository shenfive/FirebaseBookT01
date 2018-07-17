//
//  CreateAccountViewController.swift
//  HelloAuth
//
//  Created by 申潤五 on 2018/3/16.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import FirebaseAuth

class CreateAccountViewController: UIViewController {


    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var repassword: UITextField!
    @IBOutlet weak var nickName: UITextField!

    weak var loginVC:ViewController? = nil

    override func viewDidLoad() {
        super.viewDidLoad()

    }



    @IBAction func createAccount(_ sender: UIButton) {
        let theMail = email.text
        let thePassword = password.text
        let theRePassword = repassword.text
        let theNickName = nickName.text

        // 各種檢查
        if theMail == ""
        || thePassword == ""
        || theRePassword == ""
            || theNickName == ""{
            self.showMsg(message: "請輸入資料")
            return
        }

        if thePassword != theRePassword{
            self.showMsg(message: "請確認密碼")
            return
        }

        // 建帳號
        Auth.auth().createUser(withEmail: theMail!, password: thePassword!) { (user, error) in
            if error == nil{
                let request  = user?.createProfileChangeRequest()
                request?.displayName = theNickName!
                request?.commitChanges(completion: { (error) in
                    if error != nil{
                        print(error?.localizedDescription)
                    }
                })
                self.loginVC?.updateUserStatus()
                self.navigationController?.popViewController(animated: true)

            }else{
                self.showMsg(message: (error?.localizedDescription)!)
            }
        }

    }
}
