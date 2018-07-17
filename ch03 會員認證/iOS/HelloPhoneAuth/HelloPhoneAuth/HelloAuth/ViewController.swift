//
//  ViewController.swift
//  HelloAuth
//
//  Created by 申潤五 on 2018/3/9.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import FirebaseAuth



class ViewController: UIViewController {

    @IBOutlet weak var phone: UITextField!
    @IBOutlet weak var verification: UITextField!
    @IBOutlet weak var loginStatus: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        updateUserStatus()
    }


    @IBAction func singOut(_ sender: Any) {

        do {
            try Auth.auth().signOut()
            verification.text = ""
            updateUserStatus()
        } catch  {
            showMsg(message: "錯誤：\(error.localizedDescription)")
        }
    }

    @IBAction func checkPhoneNumber(_ sender: Any) {
        if let phoenNmuber = phone.text{
            PhoneAuthProvider.provider().verifyPhoneNumber(phoenNmuber, uiDelegate: nil, completion: { (verificationID, error) in
                if error == nil{
                    UserDefaults.standard.set(verificationID, forKey: "authVerificationID")
                    self.showMsg(message: "請注意收取簡訊")
                    self.view.endEditing(true)
                }else{
                    self.showMsg(message: "錯誤\(error?.localizedDescription)")
                }
            })
        }
    }
    @IBAction func checkSingIn(_ sender: Any) {
        if let verificationCode = verification.text,
            let verificationID = UserDefaults.standard.string(forKey: "authVerificationID"){
        let credential = PhoneAuthProvider.provider().credential(
            withVerificationID: verificationID,
            verificationCode: verificationCode)
            Auth.auth().signIn(with: credential, completion: { (user, error) in
                if error == nil{
                    self.updateUserStatus()
                    self.view.endEditing(true)

                }else{
                    self.showMsg(message: "錯誤\(error?.localizedDescription)")
                }
            })
        }

    }

    func updateUserStatus(){
        if let user = Auth.auth().currentUser{
            let last4Char = (user.phoneNumber! as NSString).substring(from: 9)
            loginStatus.text = "己登入 \n 電話未四碼:\(last4Char)"
        }else{
            loginStatus.text = "請登入"
        }
    }
}


extension UIViewController{
    func showMsg(message:String){
        let alertView = UIAlertController.init(title: "訊息", message: message, preferredStyle: .alert)
        alertView.addAction(UIAlertAction.init(title: "OK", style: .default, handler: nil))
        present(alertView, animated: true, completion: nil)
    }
}

