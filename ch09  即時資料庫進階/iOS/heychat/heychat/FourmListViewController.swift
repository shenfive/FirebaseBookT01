//
//  FourmListViewController.swift
//  heychat
//
//  Created by 申潤五 on 2018/4/23.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import Firebase

class FourmListViewController: UIViewController,UITableViewDelegate,UITableViewDataSource {


    @IBOutlet weak var fourmList: UITableView!
    @IBOutlet weak var subject: UILabel!
    var forumArray:[ForumItem] = [ForumItem]()
    var forumDataRef:DatabaseReference  = Database.database().reference().child("forum/subject")
    var selectedKey:String?
    var selectedSubject:String?


    override func viewDidLoad() {
        super.viewDidLoad()

        fourmList.delegate = self
        fourmList.dataSource = self

        self.title = "討論主題清單"
        updateList()


    }
    func updateList(){
        forumDataRef.observeSingleEvent(of: .value) { (dataSnapshot) in
            self.forumArray.removeAll()
            for dataItem in dataSnapshot.children {
                var forumItme = ForumItem()
                if let data = dataItem as? DataSnapshot{
                    if let subject = data.childSnapshot(forPath: "subject").value as? String{
                        forumItme.subject = subject
                    }
                    if let lastUpdateTime = data.childSnapshot(forPath: "lastUpdate").value as? Int{
                        forumItme.lastUpdateTime = lastUpdateTime
                    }
                    if let lastUpdateUserNickname = data.childSnapshot(forPath: "lastUpdateUserNickname").value as? String{
                        forumItme.lastUpdateUserNickname = lastUpdateUserNickname
                    }
                    forumItme.key = data.key
                }
                self.forumArray.append(forumItme)
            }
            self.fourmList.reloadData()
            self.showMsg(msg: "....")
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super .viewWillDisappear(animated)
        forumDataRef.removeAllObservers()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goDisc"{
            (segue.destination as? DiscViewController)?.key = selectedKey ?? ""
            (segue.destination as? DiscViewController)?.fourmListVC = self
            (segue.destination as? DiscViewController)?.subject = selectedSubject ?? ""
        }
    }

    //MARK:TableView Delegate
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedKey = self.forumArray[indexPath.row].key
        selectedSubject = self.forumArray[indexPath.row].subject

        performSegue(withIdentifier: "goDisc", sender: self)

    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return forumArray.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "fourmListCell") as! FourmListTableViewCell
        cell.subject?.text = self.forumArray[indexPath.row].subject
        cell.lastUpdateUserNickname?.text = self.forumArray[indexPath.row].lastUpdateUserNickname

        let date = Date.init(timeIntervalSince1970: Double(self.forumArray[indexPath.row].lastUpdateTime) / 1000)
        let formater = DateFormatter()
        formater.dateFormat = "MM/dd HH:mm"
        
        cell.lastUpdate.text = formater.string(from: date)

        return cell
    }

    struct ForumItem {
        var key = ""
        var subject = ""
        var lastUpdateTime = 0
        var lastUpdateUserNickname = ""
    }
}
