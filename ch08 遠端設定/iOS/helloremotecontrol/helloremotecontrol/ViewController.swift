//
//  ViewController.swift
//  helloremotecontrol
//
//  Created by 申潤五 on 2018/5/23.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import Firebase

class ViewController: UIViewController {
    @IBOutlet weak var uilabel: UILabel!

    var mRemoteconfig:RemoteConfig!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //取得 RemotConfig 實體
        mRemoteconfig = RemoteConfig.remoteConfig()

        //設定為測試模式，實作不用加
        mRemoteconfig.configSettings = RemoteConfigSettings(developerModeEnabled: true)!

        //設定當後台未設定時的預設值，也可以用 .plst 檔設定
        mRemoteconfig.setDefaults(["titleBackgroudColor":"#ffff00" as NSObject])

        //即時要求取得設定檔，快取時間為 0 秒也就是每次更新，如不加該參數時，預設為 12 小時
        mRemoteconfig.fetch(withExpirationDuration: 0) { (status, error) in
            //成功更新設定檔時的處理
            if status == .success{
                //啟用新設定檔
                self.mRemoteconfig.activateFetched()

                //取得更新內容
                let colorString = self.mRemoteconfig["titleBackgroudColor"].stringValue ?? "#ffffff"

                //設定UI並刷新畫面
                self.uilabel.backgroundColor = self.hexToCGColor(hexColor: colorString)
                self.reflashScreen()
            }
        }
    }

    //解決多執行緒畫面刷新問題
    func reflashScreen(){
        let alert = UIAlertController(title: "", message: nil, preferredStyle: .actionSheet)
        present(alert, animated: false) {
            sleep(100)
            alert.dismiss(animated: false, completion: nil)
        }
    }

    //由 hex 的顏色表示字串，轉換為 UIColor
    func hexToCGColor(hexColor:String) -> UIColor{
        var red:CGFloat = 0.0
        var green:CGFloat = 0.0
        var blue:CGFloat = 0.0
        if let redDec = Int((hexColor as NSString).substring(with: NSMakeRange(1, 2)), radix:16),
            let greenDec = Int((hexColor as NSString).substring(with: NSMakeRange(3, 2)), radix:16),
            let blueDec = Int((hexColor as NSString).substring(with: NSMakeRange(5, 2)), radix:16)
        {
            red = CGFloat(redDec) / 255
            green = CGFloat(greenDec) / 255
            blue = CGFloat(blueDec) / 255
        }
        return UIColor(red: red, green: green, blue: blue, alpha: 0)
    }
}

