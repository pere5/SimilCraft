/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.Objects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Uldrer 2.0
 */
public abstract class Object3D {
    
    protected Matrix4f transformationMatrix;

    public Object3D()
    {
        transformationMatrix = new Matrix4f();
        transformationMatrix.setIdentity();
    }
    
    public void setTransformationMatrix(Matrix4f transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }

    public Matrix4f getTransformationMatrix() {
        return transformationMatrix;
    }
    
    public void setIdentity()
    {
        transformationMatrix.setIdentity();
    }
    
    // Translate object in world coordinates
    public void translateWorld(Vector3f translation)
    {
        Matrix4f translationMatrix = getTranslationMatrix(translation);
        Matrix4f.mul(translationMatrix, transformationMatrix, transformationMatrix);
    }
    
    // Translate object in object coordinates
    public void translateObject(Vector3f translation)
    {
        Matrix4f translationMatrix = getTranslationMatrix(translation);
        Matrix4f.mul(transformationMatrix, translationMatrix, transformationMatrix);
    }
    
    // Scale object in world coordinates
    public void scaleWorld(Vector3f scale)
    {
        Matrix4f scaleMatrix = getScaleMatrix(scale);
        Matrix4f.mul(scaleMatrix, transformationMatrix, transformationMatrix);
    }
    
    // Scale object in object coordinates
    public void scaleObject(Vector3f scale)
    {
        Matrix4f scaleMatrix = getScaleMatrix(scale);
        Matrix4f.mul(transformationMatrix, scaleMatrix, transformationMatrix);
    }
    
    // Rotate object in world coordinates
    public void rotateWorld(Vector3f axis, float angle)
    {
        Matrix4f rotationMatrix = getRotationMatrix(axis, angle);
        Matrix4f.mul(rotationMatrix, transformationMatrix, transformationMatrix);
    }
    
    // Rotate object in object coordinates
    public void rotateObject(Vector3f axis, float angle)
    {
        Matrix4f rotationMatrix = getRotationMatrix(axis, angle);
        Matrix4f.mul(transformationMatrix, rotationMatrix, transformationMatrix);
    }
    
    // Rotate object around arbitrary axis centered at point in world coordinates
    public void rotateAroundAxisWorld(Vector3f point, Vector3f axis, float angle)
    {
        Matrix4f rotationMatrix = getRotationMatrix(axis, angle);
        Matrix4f tmp1 = new Matrix4f();
        Matrix4f tmp2 = new Matrix4f();
        Vector3f tmp3 = new Vector3f();
        
        // translate to object center, rotate, and move object center back
        // transformationMatrix = getTranslationMatrix(point) * rotationMatrix * getTranslationMatrix(-point) * transformationMatrix
        Matrix4f.mul(getTranslationMatrix(point), rotationMatrix, tmp1);
        point.negate(tmp3);
        Matrix4f.mul(tmp1, getTranslationMatrix(tmp3), tmp2);
        Matrix4f.mul(tmp2, transformationMatrix, transformationMatrix);
    }
    
    // Rotate object around arbitrary axis centered at point in object coordinates
    public void rotateAroundAxisObject(Vector3f point, Vector3f axis, float angle)
    {
        Matrix4f rotationMatrix = getRotationMatrix(axis, angle);
        Matrix4f tmp1 = new Matrix4f();
        Matrix4f tmp2 = new Matrix4f();
        Vector3f tmp3 = new Vector3f();
        
        // translate to object center, rotate, and move object center back
        // transformationMatrix = transformationMatrix * getTranslationMatrix(point) * rotationMatrix * getTranslationMatrix(-point)
        Matrix4f.mul(transformationMatrix,getTranslationMatrix(point), tmp1);
        Matrix4f.mul(tmp1, rotationMatrix, tmp2);
        point.negate(tmp3);
        Matrix4f.mul(tmp2, getTranslationMatrix(tmp3), transformationMatrix);
    }
    
    // Help methods
    static Matrix4f getTranslationMatrix(Vector3f translation) 
    {
        Matrix4f translationMatrix = new Matrix4f();
        translationMatrix.setIdentity();
        
        translationMatrix.m30 = translation.x;
        translationMatrix.m31 = translation.y;
        translationMatrix.m32 = translation.z;
        
        return translationMatrix;
    }
    
    static Matrix4f getScaleMatrix(Vector3f scale) 
    {
        Matrix4f scaleMatrix = new Matrix4f();
        scaleMatrix.setIdentity();
        
        scaleMatrix.m00 = scale.x;
        scaleMatrix.m11 = scale.y;
        scaleMatrix.m22 = scale.z;
        
        return scaleMatrix;
    }
    
    static Matrix4f getRotationMatrix(Vector3f axis, float angle) 
    {
        float cosa = (float)Math.cos(angle);
        float sina= (float)Math.sin(angle);
        
        Matrix4f rotationMatrix = new Matrix4f();
       
        rotationMatrix.m00 = cosa + (1-cosa)*axis.x*axis.x;
        rotationMatrix.m01 = (1-cosa)*axis.x*axis.y - axis.z*sina;
        rotationMatrix.m02 = (1-cosa)*axis.x*axis.z + axis.y*sina;
        rotationMatrix.m03 = 0.f;
        
        rotationMatrix.m10 = (1-cosa)*axis.x*axis.y + axis.z*sina; 
        rotationMatrix.m11 = cosa + (1-cosa)*axis.y*axis.y; 
        rotationMatrix.m12 = (1-cosa)*axis.y*axis.z - axis.x*sina;  
        rotationMatrix.m13 = 0.f;

        rotationMatrix.m20 = (1-cosa)*axis.x*axis.z - axis.y*sina; 
        rotationMatrix.m21 = (1-cosa)*axis.y*axis.z + axis.x*sina; 
        rotationMatrix.m22 = cosa + (1-cosa)*axis.z*axis.z;  
        rotationMatrix.m23 = 0.f;

        rotationMatrix.m30 = 0.f;
        rotationMatrix.m31 = 0.f;
        rotationMatrix.m32 = 0.f;
        rotationMatrix.m33 = 1.f;
        
        return rotationMatrix;
    }
    
   
}
