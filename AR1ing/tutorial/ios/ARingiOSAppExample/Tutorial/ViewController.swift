//
//  ViewController.swift
//  TestStyleARiOSApp
//
//  Created by dpxl on 29/08/2018.
//  Copyright © 2018 deepixel. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet weak var previewView: UIView!
    let wrapper = TutorialWrapper()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        wrapper.setTargetView(previewView);
        wrapper.start();
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}

