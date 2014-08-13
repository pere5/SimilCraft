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
public class MouseWheelEvent extends EventObject {
    public Direction direction;
    
    public MouseWheelEvent(Object source, int dw) {
        super(source);
        if(dw > 0)
        {
            this.direction = Direction.Downwards;
        }
        else if(dw < 0)
        {
            this.direction = Direction.Upwards;
        }
        else 
        {
            this.direction = Direction.Static;
        }
    }
    
    public enum Direction { Downwards, Upwards, Static };
}
