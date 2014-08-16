/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.input;

import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author Uldrer 2.0
 */
public class InputHandler {
    
    // Listener lists
    private ArrayList<KeyListener> keyEventListeners;
    private ArrayList<MouseWheelListener> mouseWheelEventListeners;
    private ArrayList<MouseButtonListener> mouseButtonEventListeners;
    
    // Init
    public InputHandler() {
        
    }
    
    // Process calls
    public void processKeyboard() {
        if (Keyboard.next()) {
            fireKeyEvent(Keyboard.getEventKey(), Keyboard.getEventKeyState());
        }
    }
    
    public void processScroll() {
        int dw = Mouse.getDWheel();
        fireMouseWheelEvent(dw);
    }
    
    // Keyboard methods
    
    synchronized public void addKeyEventListener(KeyListener listener) {
        if(keyEventListeners == null) {
            keyEventListeners = new ArrayList<>();
        }
        keyEventListeners.add(listener);
    }
	
    synchronized public void removeKeyEventListener(KeyListener listener) {
        if(keyEventListeners == null) {
            keyEventListeners = new ArrayList<>();
        }
        keyEventListeners.remove(listener);
    }
    
    protected void fireKeyEvent(int key, boolean pressed) {
        // Fire events for all listeners
        if(keyEventListeners != null && !keyEventListeners.isEmpty()) {
            
            KeyEvent event = new KeyEvent(this, key, pressed);

            // walk through the listener list
            Iterator<KeyListener> it = keyEventListeners.iterator();
            while (it.hasNext()) {
                KeyListener listener = (KeyListener) it.next();
                listener.keyEvent(event);
            }
        }
    }
    
    // Mouse wheel methods
    synchronized public void addMouseWheelEventListener(MouseWheelListener listener) {
        if(mouseWheelEventListeners == null) {
            mouseWheelEventListeners = new ArrayList<>();
        }
        mouseWheelEventListeners.add(listener);
    }
	
    synchronized public void removeMouseWheelEventListener(MouseWheelListener listener) {
        if(mouseWheelEventListeners == null) {
            mouseWheelEventListeners = new ArrayList<>();
        }
        mouseWheelEventListeners.remove(listener);
    }
    
    protected void fireMouseWheelEvent(int dw) {
        // Fire events for all listeners
        if(mouseWheelEventListeners != null && !mouseWheelEventListeners.isEmpty()) {
            
            MouseWheelEvent event = new MouseWheelEvent(this, dw);

            // walk through the listener list
            Iterator<MouseWheelListener> it = mouseWheelEventListeners.iterator();
            while (it.hasNext()) {
                MouseWheelListener listener = (MouseWheelListener) it.next();
                listener.mouseWheelEvent(event);
            }
        }
    }
    
    // Mouse button methods
    synchronized public void addMouseButtonEventListener(MouseButtonListener listener) {
        if(mouseButtonEventListeners == null) {
            mouseButtonEventListeners = new ArrayList<>();
        }
        mouseButtonEventListeners.add(listener);
    }
	
    synchronized public void removeMouseButtonEventListener(MouseButtonListener listener) {
        if(mouseButtonEventListeners == null) {
            mouseButtonEventListeners = new ArrayList<>();
        }
        mouseButtonEventListeners.remove(listener);
    }
    
    protected void fireMouseButtonEvent(int index, int x, int y, int lastX, int lastY, boolean pressed) {
        // Fire events for all listeners
        if(mouseButtonEventListeners != null && !mouseButtonEventListeners.isEmpty()) {
            
            MouseButtonEvent event = new MouseButtonEvent(this, index, x, y, lastX, lastY, pressed);

            // walk through the listener list
            Iterator<MouseButtonListener> it = mouseButtonEventListeners.iterator();
            while (it.hasNext()) {
                MouseButtonListener listener = (MouseButtonListener) it.next();
                listener.mouseButtonEvent(event);
            }
        }
    }
    
    
    private int lastX;
    private int lastY;
    
    // TODO Maybe we need delta values
    public void mouseButton() {
        
        int x = Mouse.getX();
        int y = Mouse.getY();
        
        int button0 = Mouse.getButtonIndex(Mouse.getButtonName(0));
        if (Mouse.isButtonDown(button0)) {
            fireMouseButtonEvent(button0, x, y, lastX, lastY, true);
        } else {
            fireMouseButtonEvent(button0, x, y, lastX, lastY, false);
        }
        
        int button1 = Mouse.getButtonIndex(Mouse.getButtonName(1));
        if (Mouse.isButtonDown(button1)) {
            fireMouseButtonEvent(button1, x, y, lastX, lastY, true);
        } else {
            fireMouseButtonEvent(button1, x, y, lastX, lastY, false);
        }
        
        lastX = x;
        lastY = y;
    }
}
