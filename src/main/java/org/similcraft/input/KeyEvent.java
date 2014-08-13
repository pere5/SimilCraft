/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.input;

import java.util.EventObject;

/**
 *
 * @author Uldrer 2.0
 */
public class KeyEvent extends EventObject {
   
    public int key;
    public boolean pressed;
    
    public KeyEvent(Object source, int key, boolean pressed) {
        super(source);
        this.key = key;
        this.pressed = pressed;
    }
}
