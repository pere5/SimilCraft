/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.Objects;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.similcraft.engine.Utility;
import org.similcraft.input.KeyEvent;
import org.similcraft.input.KeyListener;
import org.similcraft.input.MouseButtonEvent;
import org.similcraft.input.MouseButtonListener;
import org.similcraft.input.MouseWheelEvent;
import org.similcraft.input.MouseWheelListener;

/**
 *
 * @author Uldrer 2.0
 */
public class Camera extends Object3D implements MouseWheelListener, MouseButtonListener, KeyListener {
    
    Matrix4f projectionMatrix;
    float near_plane;
    float far_plane;
    float fovy; //in degrees
    int width;
    int height;
    float radius;
    float cameraRotationDepth;
    
    private boolean ctrlPressed = false;
    
    private final float SCROLL_DELTA = 0.1f;
    
    
    public Camera()
    {
        Reset();
        updateProjectionMatrix();
    }
    
    public void Reset()
    {
        near_plane = 0.1f;
        far_plane = 10.0f;
        fovy = 45.0f;
        width = 1;
        height = 1;
        radius = 1.0f;
    }
    
    public void SetSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        updateProjectionMatrix();
    }
    
    public void SetRadius( float radius )
    {
        this.radius = radius;
	setClippingPlanes(0.01f*radius, 10.0f*radius);
    }
    
    public void SetFOV(float fovy)
    {
        this.fovy = fovy;
    }
    
    // zoom camera with fraction between [0,1]
    public void Zoom(float fraction)
    {
        // calculate zoom vector in camera coordinates
        float dist = -radius * fraction * 3.0f;
        Vector3f zoomVector = new Vector3f(0, 0, dist);
        cameraRotationDepth += dist;
        
        translateObject( zoomVector );
    }
	
    public Matrix4f GetProjectionMatrix()
    {
        return projectionMatrix;
    }
    
    @Override
    public void keyEvent(KeyEvent e)
    {
        // Handle key events
        if(e.key == Keyboard.KEY_LCONTROL || e.key == Keyboard.KEY_RCONTROL)
        {
            if(e.pressed)
            {
                ctrlPressed = true;
            }
            else 
            {
                ctrlPressed = false;
            }
        }
    }
    
    @Override
    public void mouseWheelEvent(MouseWheelEvent e)
    {
        // Handle mouse wheel events
        if(e.direction == MouseWheelEvent.Direction.Downwards)
        {
            // Move camera in -z direction
            translateObject(new Vector3f(0,0,-SCROLL_DELTA));
        }
        else if(e.direction == MouseWheelEvent.Direction.Upwards)
        {
            // Move camera in +z direction
            translateObject(new Vector3f(0,0,SCROLL_DELTA));
        }
    }
    
    @Override
    public void mouseButtonEvent(MouseButtonEvent e)
    {
        // Handle mouse button events
        if(ctrlPressed && e.buttonIndex == 0)
        {
            // Left button
            if (e.pressed)
            {
                rotate(e.x, e.y, e.lastX, e.lastY);
            }
        }
        else if(e.buttonIndex == 0)
        {
            // Left button
            if (e.pressed) {
                
                float dx = -(float)(e.x - e.lastX)/width;
                float dy = -(float)(e.y - e.lastY)/height;
                
                translate(dx, dy);
            } 
        }
    }
    
    private void translate(float dx, float dy )
    {
        Matrix4f transform = getTransformationMatrix();
        Matrix4f inverseTransform = new Matrix4f();
        Matrix4f.invert(transform, inverseTransform);
        Vector3f ptCamera = Utility.multiplyM4x4WithV3(inverseTransform, new Vector3f(0,0,0)); // multiply with world center
                
        float z = -ptCamera.z;
        
        Rectangle screen = getScreenExtents();
        
        float tx = 2.0f * dx * screen.right/near_plane * z;
        float ty = 2.0f * dy * screen.top/near_plane * z;
       
        translateObject(new Vector3f(tx, ty, 0));
    }
    
    private void rotate(int x, int y, int lastX, int lastY)
    {
        Vector3f lastPoint = new Vector3f();
        boolean lastOk = mapToSphere(lastX, lastY, lastPoint);
        
        if(lastOk)
        {
            Vector3f currentPoint = new Vector3f();
            boolean currentOk = mapToSphere(x, y, currentPoint);
            
            if(currentOk)
            {
                Vector3f axis = new Vector3f();
                Vector3f.cross(lastPoint,currentPoint, axis);
                float cosa = Vector3f.dot(lastPoint, currentPoint);
                
                if(!(lastPoint.x == currentPoint.x && lastPoint.y == currentPoint.y && lastPoint.z == currentPoint.z))
                {
                    axis.normalise(axis);
                    float angle = 2.0f * (float) Math.acos(cosa);
                    // rotate camera around point
                    rotateAroundAxisObject(new Vector3f(0, 0, -cameraRotationDepth), axis, -angle);
                }    
            }
        }
        
        
        
    }
    
    private boolean mapToSphere(int x, int y, Vector3f v)
    {
        // Check constraints
        if((x >= 0) && (x <= width) && (y >= 0) && (y <= height))
        {
            float xBox = (x - 0.5f*width) / width;
            float yBox = (y - 0.5f*height) / height;
            
            float sinx = (float)Math.sin(Math.PI * xBox * 0.5);
            float siny = (float)Math.sin(Math.PI * yBox * 0.5);
            float sinx2siny2 = sinx * sinx + siny * siny;
            
            v.x = sinx;
            v.y = siny;
            v.z = sinx2siny2 < 1 ? (float) Math.sqrt(1.0 - sinx2siny2) : 0.0f;
            return true;
        }
        else 
        {
            return false;
        }
    }
    
    private Rectangle getScreenExtents()
    {
        Rectangle rect = new Rectangle();
        float alpha = (float)(fovy*2*Math.PI/360);
        rect.top = (float)Math.tan(alpha)*near_plane;
        rect.bottom = -rect.top;
        rect.left = -rect.top;
        rect.right = rect.top;
        
        return rect;
    }
    
    private void setClippingPlanes( float near, float far )
    {
        near_plane = near;
        far_plane = far;
        updateProjectionMatrix();
    }
    
    private void updateProjectionMatrix()
    {
        Rectangle rect = getScreenExtents();
        
        float a1 = (2*near_plane)/(rect.right-rect.left);
        float b2 = (2*near_plane)/(rect.top-rect.bottom);
        float a3 = (rect.right+rect.left)/(rect.right-rect.left);
        float b3 = (rect.top+rect.bottom)/(rect.top-rect.bottom);
        float c3 = -(far_plane+near_plane)/(far_plane-near_plane);
        float c4 = -(2*near_plane*far_plane)/(far_plane-near_plane);
        
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = a1;
        //projectionMatrix.m01 = a3; //behövs?
        projectionMatrix.m11 = b2;
        //projectionMatrix.m21 = b3; //behövs?
        projectionMatrix.m22 = c3;
        projectionMatrix.m23 = -1.0f;
        projectionMatrix.m32 = c4;
    }
    
    public class Rectangle
    {
        public float top;
        public float bottom;
        public float left;
        public float right; 
        
        public Rectangle(float top, float bottom, float left, float right)
        {
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
        }
        
        public Rectangle()
        {
            this.top = 0;
            this.bottom = 0;
            this.left = 0;
            this.right = 0;
        }
    }
    
}
