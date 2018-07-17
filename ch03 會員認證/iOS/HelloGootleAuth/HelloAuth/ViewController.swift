//
//  ViewController.swift
//  HelloAuth
//
//  Created by 申潤五 on 2018/3/9.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import Firebase
import GoogleSignIn

class ViewController: UIViewController,GIDSignInUIDelegate,GIDSignInDelegate {
    @IBOutlet weak var googleSingInButton: GIDSignInButton!

    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if error == nil {
            if let authentication = user.authentication{
                let authCredential = GoogleAuthProvider.credential(withIDToken: authentication.idToken, accessToken: authentication.accessToken)
                Auth.auth().signIn(with: authCredential, completion: { (firebaseUser, error) in
                    if error == nil{
                        self.msg.text = "F user:\(firebaseUser!.displayName!)"
                        self.googleSingInButton.isEnabled = false
                        let alertView = UIAlertController.init(title: "成功登入", message: "", preferredStyle: .alert)
                        alertView.addAction(UIAlertAction.init(title: "OK", style: .default, handler: nil))
                        self.present(alertView, animated: true, completion: nil)
                    }
                })
            }
        }else{
            print(error.localizedDescription)
        }
    }




    @IBOutlet weak var msg: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        GIDSignIn.sharedInstance().clientID = FirebaseApp.app()?.options.clientID
        GIDSignIn.sharedInstance().delegate = self
        GIDSignIn.sharedInstance().uiDelegate = self
    }
    
    @IBAction func singOut(_ sender: UIButton) {
        do {
            try Auth.auth().signOut()
            GIDSignIn.sharedInstance().signOut()
            self.googleSingInButton.isEnabled = true
            self.msg.text = "請選擇登入方式"
        } catch  {
            print(error.localizedDescription)
        }
    }
    
}

