//
//  ShareViewController.swift
//  MoreStuffShare
//
//  Created by Àlex Gallardo Moreno on 27/11/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit
import Social
import UniformTypeIdentifiers

@objc(ShareExtensionViewController)
class ShareViewController: UIViewController {
    
    var docPath = ""
    var sharedFilePath = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("View did load in ShareViewController")
        
        guard
            let extensionItem = extensionContext?.inputItems.first as? NSExtensionItem,
            let itemProvider = extensionItem.attachments?.first else {
            print("No valid extension item or attachments found.")
            close()
            return
        }
        
        guard let containerURL = FileManager().containerURL(forSecurityApplicationGroupIdentifier: "group.io.middlepoint.morestuff") else {
            print("Failed to access App Group container.")
            close()
            return
        }

        docPath = "\(containerURL.path)/share"
        
        // Log container URL
        print("Container URL: \(containerURL.path)")
        
        // Create directory if not exists
        do {
            try FileManager.default.createDirectory(atPath: docPath, withIntermediateDirectories: true, attributes: nil)
            print("Directory created successfully at path: \(docPath)")
        } catch let error as NSError {
            print("Could not create the directory: \(error.localizedDescription)")
        } catch {
            print("Unknown error while creating directory.")
            close()
            fatalError()
        }
        
        // Removing previous stored files
        do {
            let files = try FileManager.default.contentsOfDirectory(atPath: docPath)
            for file in files {
                try FileManager.default.removeItem(at: URL(fileURLWithPath: "\(docPath)/\(file)"))
                print("Removed file: \(file)")
            }
        } catch {
            print("Failed to clean up directory: \(error)")
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        print("View did appear in ShareViewController")
        
        let alertView = UIAlertController(title: "Export", message: " ", preferredStyle: .alert)
        self.present(alertView, animated: true, completion: {
            let group = DispatchGroup()
            
            print("Input items count: \(self.extensionContext?.inputItems.count ?? 0)")
            
            for item in self.extensionContext?.inputItems ?? [] {
                guard let inputItem = item as? NSExtensionItem else {
                    print("Item is not of type NSExtensionItem.")
                    continue
                }
                
                for attachment in inputItem.attachments ?? [] {
                    guard let itemProvider = attachment as? NSItemProvider else {
                        print("Attachment is not of type NSItemProvider.")
                        continue
                    }
                    
                    if itemProvider.hasItemConformingToTypeIdentifier(UTType.text.identifier) {
                        group.enter()
                        itemProvider.loadItem(forTypeIdentifier: UTType.text.identifier, options: nil) { (item, error) in
                            if let text = item as? String {
                                print("Received text: \(text)")
                                self.sharedFilePath = "\(self.docPath)/shared_text.txt"
                                try? text.write(toFile: self.sharedFilePath, atomically: true, encoding: .utf8)
                            } else if let error = error {
                                print("Error loading text: \(error.localizedDescription)")
                            }
                            group.leave()
                        }
                    } else if itemProvider.hasItemConformingToTypeIdentifier(UTType.url.identifier) {
                        group.enter()
                        itemProvider.loadItem(forTypeIdentifier: UTType.url.identifier, options: nil) { (item, error) in
                            if let url = item as? URL {
                                print("Received URL: \(url)")
                                let urlString = url.absoluteString
                                self.sharedFilePath = "\(self.docPath)/shared_url.txt"
                                try? urlString.write(toFile: self.sharedFilePath, atomically: true, encoding: .utf8)
                            } else if let error = error {
                                print("Error loading URL: \(error.localizedDescription)")
                            }
                            group.leave()
                        }
                    } else if itemProvider.hasItemConformingToTypeIdentifier(UTType.image.identifier) {
                        group.enter()
                        itemProvider.loadItem(forTypeIdentifier: UTType.image.identifier, options: nil) { (item, error) in
                            if let url = item as? URL {
                                print("Received image: \(url)")
                                self.sharedFilePath = "\(self.docPath)/\(url.lastPathComponent)"
                                try? FileManager.default.copyItem(at: url, to: URL(fileURLWithPath: self.sharedFilePath))
                            } else if let error = error {
                                print("Error loading image: \(error.localizedDescription)")
                            }
                            group.leave()
                        }
                    } else if itemProvider.hasItemConformingToTypeIdentifier(UTType.pdf.identifier) {
                        group.enter()
                        itemProvider.loadItem(forTypeIdentifier: UTType.pdf.identifier, options: nil) { (item, error) in
                            if let url = item as? URL {
                                print("Received PDF: \(url)")
                                self.sharedFilePath = "\(self.docPath)/\(url.lastPathComponent)"
                                try? FileManager.default.copyItem(at: url, to: URL(fileURLWithPath: self.sharedFilePath))
                            } else if let error = error {
                                print("Error loading PDF: \(error.localizedDescription)")
                            }
                            group.leave()
                        }
                    }
                }
            }
            
            group.notify(queue: .main) {
                let pathQuery = "morestuff://io.middlepoint.morestuff?\(self.sharedFilePath)".addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
                print("Generated path query: \(pathQuery)")
                if let url = URL(string: pathQuery) {
                    let result = self.openURL(url)
                    print("Open URL result: \(result)")
                } else {
                    print("Failed to create URL from pathQuery.")
                }
                
                self.dismiss(animated: false) {
                    self.close()
                }
            }
        })
    }


    
    /// Close the Share Extension
    func close() {
        self.extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
    }
    
    func toast(text: String) {
        DispatchQueue.main.async {
            let alert = UIAlertController(title: nil, message: text, preferredStyle: .actionSheet)
            self.present(alert, animated: true)
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                alert.dismiss(animated: true)
            }
        }
    }

    
    //  Function must be named exactly like this so a selector can be found by the compiler!
    //  Anyway - it's another selector in another instance that would be "performed" instead.
    @objc func openURL(_ url: URL) -> Bool {
        var responder: UIResponder? = self
        while responder != nil {
            if let application = responder as? UIApplication {
                application.open(url, options: [:]) { success in
                    print("URL opened successfully: \(success)")
                }
                return true
            }
            responder = responder?.next
        }
        return false
    }

}

