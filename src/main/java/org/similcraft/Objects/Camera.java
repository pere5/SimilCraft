/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.Objects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.similcraft.input.MouseWheelEvent;
import org.similcraft.input.MouseWheelListener;

/**
 *
 * @author Uldrer 2.0
 */
public class Camera implements MouseWheelListener {
    
    Matrix4f projectionMatrix;
    float near_plane;
    float far_plane;
    float fovy; //in degrees
    int width;
    int height;
    float radius;
    Vector3f position;
    
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
        position = new Vector3f();
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
    
    public void SetPosition(Vector3f newPosition)
    {
        position = newPosition;
    }
    
    public void Zoom()
    {
        // TODO
    }
	
    public Matrix4f GetProjectionMatrix()
    {
        return projectionMatrix;
    }
    
    public Vector3f GetPosition()
    {
        return position;
    }
    
    @Override
    public void mouseWheelEvent(MouseWheelEvent e)
    {
        // Handle mouse wheel events
        if(e.direction == MouseWheelEvent.Direction.Downwards)
        {
            // Move camera in -z direction
            position.z -= SCROLL_DELTA;
        }
        else if(e.direction == MouseWheelEvent.Direction.Upwards)
        {
            // Move camera in +z direction
            position.z += SCROLL_DELTA;
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
