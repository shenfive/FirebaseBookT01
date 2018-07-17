//  ViewController.swift
//  hellostorage
//
//  Created by 申潤五 on 2018/3/31.
//  Copyright © 2018年 申潤五. All rights reserved.
//
import UIKit
import Firebase

class ViewController: UIViewController,UIImagePickerControllerDelegate,UINavigationControllerDelegate,UICollectionViewDelegate, UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return uploadedImages.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = sharedImagesCollection.dequeueReusableCell(withReuseIdentifier: "myCell", for: indexPath) as! MyCollectionViewCell

        var theImage:UIImage? = nil

        if let linkString = uploadedImages[indexPath.row]["link"]{
            if let imageUrl = URL.init(string: linkString){
                do{
                    let data = try Data.init(contentsOf: imageUrl)
                    theImage = UIImage(data: data)
                }catch{

                }
            }
        }
        cell.image.image = theImage
        return cell
    }



    @IBOutlet weak var sharedImagesCollection: UICollectionView!
    @IBOutlet weak var uploadStatus: UIProgressView!
    var uploadedImages = [[String:String]]()

    override func viewDidLoad() {
        super.viewDidLoad()
        // 匿名登入
        Auth.auth().signInAnonymously(completion: nil)
        // 不顯示進度條
        uploadStatus.isHidden = true

        sharedImagesCollection.delegate = self
        sharedImagesCollection.dataSource = self

        let dataRef = Database.database().reference().child("pic")
        dataRef.observe(.value) { (snapshot) in
            self.uploadedImages.removeAll()
            for item in snapshot.children{
                if let itemSnapshot = item as? DataSnapshot{
                    let uid = itemSnapshot.childSnapshot(forPath: "uid").value as! String
                    let link = itemSnapshot.childSnapshot(forPath: "link").value as! String
                    let value = ["uid":uid,"link":link]
                    self.uploadedImages.append(value)
                }
            }
            self.sharedImagesCollection.reloadData()
        }

    }

    @IBAction func uploadImage(_ sender: UIButton) {
        let imagePicker = UIImagePickerController()
        imagePicker.sourceType = UIImagePickerControllerSourceType.photoLibrary
        imagePicker.allowsEditing = false
        imagePicker.delegate = self
        present(imagePicker, animated: true, completion: nil)
    }

    //MARK: UIImagePickerController
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        // 設定儲存位置
        let storageRef = Storage.storage().reference().child("pic")

        // 取得選取影像
        let image = info[UIImagePickerControllerOriginalImage] as! UIImage

        // 設定上傳檔案名
        var filename = "image.JPG"
        if let url = info[UIImagePickerControllerImageURL] as? URL{
            filename = url.lastPathComponent
        }

        // 取得目前使用者 ID
        if let theUid = Auth.auth().currentUser?.uid{
            // 取得破壞性壓縮 Jpeg 影像
            if let data = UIImageJPEGRepresentation(image, 0.5){

                //建立中介資料
                let myMetadata = StorageMetadata()
                myMetadata.customMetadata = ["myKye":"my Value"]

                // 顯示進度條
                uploadStatus.isHidden = false

                //上傳到 Storage
                let task = storageRef.child(theUid).child(filename).putData(data, metadata: myMetadata) { (metadata, error) in
                    self.uploadStatus.isHidden = true
                    if error == nil{

                        //上傳成功更新資料庫
                        let dataRef = Database.database().reference().child("pic")
                        let value = ["uid":theUid,"link":(metadata?.downloadURL())!.absoluteString]
                        dataRef.childByAutoId().setValue(value)

                        //通知使用者上傳成功
                        let alert = UIAlertController.init(title: "上傳成功", message: nil, preferredStyle: .alert)
                        alert.addAction(UIAlertAction.init(title: "OK", style: .default, handler: nil))
                        self.present(alert, animated: true, completion: nil)
                    }else{
                        print(error?.localizedDescription)
                    }
                }

                // 進度顯示
                task.observe(.progress) { (snapshot) in
                    if let theProgress = snapshot.progress?.fractionCompleted{
                        self.uploadStatus.progress = Float(theProgress)
                    }
                }
            }
        }
        picker.dismiss(animated: true, completion: nil)
    }



}

