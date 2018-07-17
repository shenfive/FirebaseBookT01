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
    var auth:Auth? = nil
    
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var accountMsg: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        auth = Auth.auth()
        updateUserStatus()

    }

    func updateUserStatus(){
        if let user = auth?.currentUser{
            accountMsg.text = "己登入"
            accountMsg.text = accountMsg.text! + "Email:\(user.email!)\n"
            accountMsg.text = accountMsg.text! + "是否己確認 Email:\(user.isEmailVerified)\n"
            accountMsg.text = accountMsg.text! + "UID:\(user.uid)"
            if user.isEmailVerified != true{
                user.sendEmailVerification(completion: { (error) in
                    if error != nil{
                        print(error?.localizedDescription)
                    }
                })
            }
        }else{
            accountMsg.text = "請登入"
        }

    }


    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goCreateVC"{
            (segue.destination as? CreateAccountViewController)?.loginVC = self
        }
    }    

    @IBAction func forgetPassword(_ sender: Any) {
        if let theEmail = email.text{
            if theEmail == ""{
                showMsg(message: "請輸入電子郵件")
                return
            }
            auth?.sendPasswordReset(withEmail: theEmail, completion: { (error) in
                if error == nil{
                    self.showMsg(message: "己發出密碼變更郵件，請檢查你的電子郵件信箱")

                }else{
                    self.showMsg(message: "錯誤\(error!.localizedDescription)")
                }
            })

        }
    }
    
    @IBAction func singIn(_ sender: Any) {
        if let theEmail = email.text,
            let thePassword = password.text{
            auth?.signIn(withEmail: theEmail, password: thePassword, completion: { (user, error) in
                if error == nil{
                    self.updateUserStatus()
                }else{
                    self.showMsg(message: error!.localizedDescription)
                }
            })
        }
    }
    @IBAction func singOut(_ sender: Any) {
        do {
            try auth?.signOut()
            updateUserStatus()
        } catch  {
            print(error.localizedDescription)
        }

    }
    
    @IBAction func goCreateVC(_ sender: Any) {
        performSegue(withIdentifier: "goCreateVC", sender: nil)
    }
    
}


extension UIViewController{
    func showMsg(message:String){
        let alertView = UIAlertController.init(title: "訊息", message: message, preferredStyle: .alert)
        alertView.addAction(UIAlertAction.init(title: "OK", style: .default, handler: nil))
        present(alertView, animated: true, completion: nil)
    }
}

