//
//  ViewController.swift
//  StyleARRingApp
//
//  Created by dpxl on 19/06/2019.
//  Copyright © 2019 deepixel. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var metaDataView: UITextView!
    var wrapper: StyleARWrapper? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        metaDataView.isHidden = true
        wrapper = StyleARWrapper(frame: self.view.frame);
        let styleARRingView = wrapper!.getStyleARRingView()!;
        self.view.addSubview(styleARRingView);
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        wrapper!.start();
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(ViewController.handleTap))
        self.view.addGestureRecognizer(tap);
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func handleTap() {
        let str = wrapper!.getHandMetaData()
        metaDataView.text = str;
        metaDataView.isHidden = false;
    }

}

