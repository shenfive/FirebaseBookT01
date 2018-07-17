//
//  ViewController.swift
//  HelloAuth
//
//  Created by 申潤五 on 2018/3/9.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import FBSDKLoginKit
import FirebaseAuth

class ViewController: UIViewController,FBSDKLoginButtonDelegate {

    @IBOutlet weak var loginButton: FBSDKLoginButton!
    @IBOutlet weak var msg: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        loginButton.readPermissions = ["public_profile","email"]
        loginButton.delegate = self
    }

    func loginButton(_ loginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
        if error == nil{
            if let token = FBSDKAccessToken.current(){
                let credential = FacebookAuthProvider.credential(withAccessToken: token.tokenString)
                Auth.auth().signIn(with: credential, completion: { (user, error) in
                    if error == nil{
                        self.msg.text = "歡迎\(user!.displayName!)"
                        let alert = UIAlertController.init(title: "登入成功", message: "", preferredStyle: .alert)
                        alert.addAction(UIAlertAction.init(title: "OK", style: .default, handler: nil))
                        self.present(alert, animated: true, completion: nil)
                    }else{
                        print(error?.localizedDescription)
                    }
                })
            }
        }else{
            print(error.localizedDescription)
        }
    }

    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!) {
        msg.text = "請登入"
        let alert = UIAlertController.init(title: "登出成功", message: "", preferredStyle: .alert)
        alert.addAction(UIAlertAction.init(title: "OK", style: .default, handler: nil))
        self.present(alert, animated: true, completion: nil)
        do {
            try Auth.auth().signOut()
        } catch  {
            print(error.localizedDescription)
        }

    }

}

