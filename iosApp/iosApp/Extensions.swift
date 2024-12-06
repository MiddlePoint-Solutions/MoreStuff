//
//  Extensions.swift
//  iosApp
//
//  Created by Àlex Gallardo Moreno on 27/11/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

extension String {
    func utf8DecodedString()-> String {
        let data = self.data(using: .utf8)
        let message = String(data: data!, encoding: .nonLossyASCII) ?? ""
        return message
    }
    
    func utf8EncodedString()-> String {
        let messageData = self.data(using: .nonLossyASCII)
        let text = String(data: messageData!, encoding: .utf8) ?? ""
        return text
    }
}
