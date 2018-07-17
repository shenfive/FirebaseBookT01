//
//  DiscViewController.swift
//  heychat
//
//  Created by 申潤五 on 2018/4/24.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import Firebase

class DiscViewController: UIViewController,UITableViewDelegate,UITableViewDataSource {

    var subject = ""
    var key = ""
    var discRef:DatabaseReference? = nil
    var forumRef:DatabaseReference? = nil
    var discArray:[DiscItem] = [DiscItem]()
    var fourmListVC:FourmListViewController? = nil

    @IBOutlet weak var subjectLabel: UILabel!
    @IBOutlet weak var msg: UITextField!
    @IBOutlet weak var discTable: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()

        subjectLabel.text = subject

        // 軟體鍵盤訊息
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardDidShow), name: .UIKeyboardDidShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardDidHide), name: .UIKeyboardDidHide, object: nil)

        discTable.delegate = self
        discTable.dataSource = self
        discArray.removeAll()



        discRef = Database.database().reference().child("forum/disc").child(key)
        forumRef = Database.database().reference().child("forum/subject").child(key)
        print(key)
        discRef?.observe(.childAdded, with: { (dataSnapshot) in
                var discItem = DiscItem()
                if let content = dataSnapshot.childSnapshot(forPath: "content").value as? String{
                    discItem.content = content
                }
                if let timestamp = dataSnapshot.childSnapshot(forPath: "timestamp").value as? Int{
                    discItem.timestamp = timestamp
                }
                if let nickname = dataSnapshot.childSnapshot(forPath: "nickname").value as? String{
                    discItem.nickname = nickname
                }
                discItem.key = dataSnapshot.key
                self.discArray.append(discItem)

                self.discArray.sort(by: { (lhs, rhs) -> Bool in
                    return lhs.timestamp > rhs.timestamp
                })
                self.discTable.reloadData()
                print("get ID:\(self.discArray.count)")
                self.showMsg(msg: "新訊息")
        })
    }

    override func viewWillDisappear(_ animated: Bool) {
        super .viewWillDisappear(animated)
        fourmListVC?.updateList()
    }


    //MARK:TableViewDelegate
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return discArray.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "discContentViewCell") as! DiscItemTableViewCell
   


        let discItem = discArray[indexPath.row]
        cell.nickname.text = discItem.nickname
        cell.content.text = discItem.content
        let date = Date.init(timeIntervalSince1970: Double(discItem.timestamp) / 1000)
        let formater = DateFormatter()
        formater.dateFormat = "MM/dd HH:mm"
        cell.lastUpdate.text = formater.string(from: date)

        return cell
    }


    @IBAction func newMessage(_ sender: Any) {
        if let enteredMsg = msg.text{
            if enteredMsg.count > 0{
                msg.text = ""
                self.view.endEditing(true)
                let nickname = UserDefaults.standard.string(forKey: "nickname") ?? ""
                let newMsg = ["content":enteredMsg,"nickname":nickname,"timestamp":ServerValue.timestamp()] as [String : Any]
                discRef?.childByAutoId().setValue(newMsg)
                forumRef?.child("lastUpdate").setValue(ServerValue.timestamp())
                forumRef?.child("lastUpdateUserNickname").setValue(nickname)
            }
        }


    }


    //MARK:SoftKeyBoard
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }

    @objc func keyboardDidShow(notification:NSNotification){
        //升鍵盤
        if let info = notification.userInfo{
            if let value = info[UIKeyboardFrameEndUserInfoKey] as? NSValue{
                let rawFrame: CGRect? = value.cgRectValue
                let keyboardFrame: CGRect = view.convert(rawFrame ?? CGRect.zero, from: nil)
                let rect = CGRect(x: 0,
                                  y: 0 - keyboardFrame.height,
                                  width: self.view.frame.size.width,
                                  height: self.view.frame.size.height)
                UIView.animate(withDuration: 0.1) {
                    self.view.frame = rect
                }
            }
        }

    }
    @objc func keyboardDidHide(notification:NSNotification){
        print("hind")
        UIView.animate(withDuration: 0.1) {
            self.view.frame = CGRect(origin: CGPoint(x: 0, y: 0),
                                     size: self.view.frame.size)
        }
    }


    struct DiscItem{
        var content = ""
        var nickname = ""
        var key = ""
        var timestamp = 0
    }


}
