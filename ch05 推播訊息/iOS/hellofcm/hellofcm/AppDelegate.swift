//
//  AppDelegate.swift
//  hellofcm
//
//  Created by 申潤五 on 2018/4/10.
//  Copyright © 2018年 申潤五. All rights reserved.
//

import UIKit
import UserNotifications
import Firebase

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate,MessagingDelegate,UNUserNotificationCenterDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()

        //指定 FCM 的代理人
        Messaging.messaging().delegate = self
        //要求三種型式的推播
        UNUserNotificationCenter.current().delegate = self
        let authOptions:UNAuthorizationOptions = [.alert, .sound, .badge]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { (_, _) in}
        application.registerForRemoteNotifications()

        return true
    }
//
//    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
//        print("Get Token:\(fcmToken)")
//    }
//
//    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
//        print("User Info:\(userInfo)")
//    }

}

