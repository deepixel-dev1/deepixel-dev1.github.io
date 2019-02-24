//
//  ViewController.swift
//  StyleARiOSAppExample
//
//  Created by dpxl on 24/02/2019.
//  Copyright © 2019 deepixel. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var previewView: UIView!
    @IBOutlet weak var metaDataView: UITextView!
    let wrapper = StyleARWrapper()

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        metaDataView.isHidden = true;
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        wrapper.setTargetView(previewView);
        wrapper.start();
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(ViewController.handleTap))
        previewView.addGestureRecognizer(tap);
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @objc func handleTap() {
        let str = wrapper.getMetaData()
        metaDataView.text = str;
        metaDataView.isHidden = false;
    }

}

