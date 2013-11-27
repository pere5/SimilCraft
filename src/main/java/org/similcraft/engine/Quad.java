/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.engine;

import java.nio.ByteBuffer;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Per
 */
public class Quad {
       
    private VertexData[] vertices;
    private ByteBuffer verticesByteBuffer;
    private int indicesCount;
    private int vaoId;
    private int vboId;
    private int vboiId;
    private Vector3f modelPos;
    private Vector3f modelAngle;
    private Vector3f modelScale;
    private float[] vboOffsetHolder = new float[4];
    
    public Quad() {
        
    }
    
    public Quad(VertexData[] vertices,
            ByteBuffer verticesByteBuffer,
            int indicesCount,
            int vaoId,
            int vboId,
            int vboiId,
            Vector3f modelPos,
            Vector3f modelAngle,
            Vector3f modelScale) {
        this.vertices = vertices;
        this.verticesByteBuffer = verticesByteBuffer;
        this.indicesCount = indicesCount;
        this.vaoId = vaoId;
        this.vboId = vboId;
        this.vboiId = vboiId;
        this.modelPos = modelPos;
        this.modelAngle = modelAngle;
        this.modelScale = modelScale;
    }
    
    /**
     * @return the vertices
     */
    public VertexData[] getVertices() {
        return vertices;
    }

    /**
     * @param vertices the vertices to set
     */
    public void setVertices(VertexData[] vertices) {
        this.vertices = vertices;
    }

    /**
     * @return the verticesByteBuffer
     */
    public ByteBuffer getVerticesByteBuffer() {
        return verticesByteBuffer;
    }

    /**
     * @param verticesByteBuffer the verticesByteBuffer to set
     */
    public void setVerticesByteBuffer(ByteBuffer verticesByteBuffer) {
        this.verticesByteBuffer = verticesByteBuffer;
    }

    /**
     * @return the indicesCount
     */
    public int getIndicesCount() {
        return indicesCount;
    }

    /**
     * @param indicesCount the indicesCount to set
     */
    public void setIndicesCount(int indicesCount) {
        this.indicesCount = indicesCount;
    }

    /**
     * @return the vaoId
     */
    public int getVaoId() {
        return vaoId;
    }

    /**
     * @param vaoId the vaoId to set
     */
    public void setVaoId(int vaoId) {
        this.vaoId = vaoId;
    }

    /**
     * @return the vboId
     */
    public int getVboId() {
        return vboId;
    }

    /**
     * @param vboId the vboId to set
     */
    public void setVboId(int vboId) {
        this.vboId = vboId;
    }

    /**
     * @return the vboiId
     */
    public int getVboiId() {
        return vboiId;
    }

    /**
     * @param vboiId the vboiId to set
     */
    public void setVboiId(int vboiId) {
        this.vboiId = vboiId;
    }

    /**
     * @return the modelPos
     */
    public Vector3f getModelPos() {
        return modelPos;
    }

    /**
     * @param modelPos the modelPos to set
     */
    public void setModelPos(Vector3f modelPos) {
        this.modelPos = modelPos;
    }

    /**
     * @return the modelAngle
     */
    public Vector3f getModelAngle() {
        return modelAngle;
    }

    /**
     * @param modelAngle the modelAngle to set
     */
    public void setModelAngle(Vector3f modelAngle) {
        this.modelAngle = modelAngle;
    }

    /**
     * @return the modelScale
     */
    public Vector3f getModelScale() {
        return modelScale;
    }

    /**
     * @param modelScale the modelScale to set
     */
    public void setModelScale(Vector3f modelScale) {
        this.modelScale = modelScale;
    }

    /**
     * @return the vboOffsetHolder
     */
    public float[] getVboOffsetHolder() {
        return vboOffsetHolder;
    }

    /**
     * @param vboOffsetHolder the vboOffsetHolder to set
     */
    public void setVboOffsetHolder(float[] vboOffsetHolder) {
        this.vboOffsetHolder = vboOffsetHolder;
    }
}
