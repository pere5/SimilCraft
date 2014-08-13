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
public class MouseButtonEvent extends EventObject {
    
    public int buttonIndex;
    public int x;
    public int y;
    public boolean pressed;
    
    public MouseButtonEvent(Object source, int buttonIndex, int x, int y, boolean pressed) {
        super(source);
        this.buttonIndex = buttonIndex;
        this.x = x;
        this.y = y;
        this.pressed = pressed;
    }
}
