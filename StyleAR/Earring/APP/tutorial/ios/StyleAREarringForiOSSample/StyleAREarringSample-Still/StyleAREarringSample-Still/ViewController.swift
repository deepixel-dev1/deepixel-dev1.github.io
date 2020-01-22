//
//  ViewController.swift
//  StyleAREarringSample-Still
//
//  Created by dpxl on 14/01/2020.
//  Copyright © 2020 deepixel. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var button: UIButton!
    var wrapper: StyleARWrapper? = nil

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        wrapper = StyleARWrapper();
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        wrapper!.start();
    }

    @IBAction func touchDown(_ sender: Any) {
        let image = wrapper!.getStyleARImage()
        if(image == nil) { return }
        
        guard let cgImage = image?.takeRetainedValue() else { return }
        let uiimage = UIImage(cgImage: cgImage)
        UIImageWriteToSavedPhotosAlbum(uiimage, nil, nil, nil)

    }
}

