//
//  ViewController.swift
//  StyleAREarringSample-Live
//
//  Created by dpxl on 14/01/2020.
//  Copyright © 2020 deepixel. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet var cameraView: UIView!
    var wrapper: StyleARWrapper? = nil

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        wrapper = StyleARWrapper(frame: cameraView.bounds);
        let styleAREarringView = wrapper!.getStyleAREarringView()!;
        self.cameraView.addSubview(styleAREarringView);
        self.cameraView.sendSubviewToBack(styleAREarringView);

    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        wrapper!.start();
    }

}

