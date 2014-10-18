/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.Objects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.similcraft.engine.Utility;
import org.similcraft.log.LogFormatter;

/**
 *
 * @author Uldrer 2.0
 */
public class Mesh3D extends Object3D{
    public static final Logger log = Logger.getLogger(Mesh3D.class.getName());
    static { (new LogFormatter()).setFormater(log); }

    protected BasicVertex[] vertices;
    protected int[] vertexIndices; // stores indices for 3 vetrex of each face
    protected ByteBuffer verticesByteBuffer;
    protected int vaoId;
    protected int vboId;
    protected int vboiId;
    protected int[] textureIds = new int[]{0, 0};
    protected int activeTextureIndex = 0;

    protected Vector3f position = new Vector3f(0, 0, 0);
    protected Vector3f angle = new Vector3f(0, 0, 0);
    protected Vector3f scale = new Vector3f(1, 1, 1);

    public void mesh3D(BasicVertex[] theVertices, int[] indices)
    {
        vertices = theVertices;
        vertexIndices = indices;

        // Put each 'BasicVertex' in one FloatBuffer
        verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * BasicVertex.stride);
        FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
        for (BasicVertex vertex : vertices) {
            // Add position, color, texture and normals as floats to the buffer
            verticesFloatBuffer.put(vertex.getElements());
        }
        verticesFloatBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(vertexIndices.length);
        indicesBuffer.put(vertexIndices);
        indicesBuffer.flip();

        // Create a new CubeVertex Array Object in memory and select it (bind)
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // Create a new CubeVertex Buffer Object(VBO) in memory and select it (bind)
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);

        // Put the position coordinates in attribute list 0
        GL20.glVertexAttribPointer(0, BasicVertex.positionElementCount, GL11.GL_FLOAT, false, BasicVertex.stride, BasicVertex.positionByteOffset);
        // Put the color components in attribute list 1
        GL20.glVertexAttribPointer(1, BasicVertex.colorElementCount, GL11.GL_FLOAT, false, BasicVertex.stride, BasicVertex.colorByteOffset);
        // Put the texture coordinates in attribute list 2
        GL20.glVertexAttribPointer(2, BasicVertex.textureElementCount, GL11.GL_FLOAT, false, BasicVertex.stride, BasicVertex.textureByteOffset);
        // Put the normal components in attribute list 3
        GL20.glVertexAttribPointer(3, BasicVertex.normalElementCount, GL11.GL_FLOAT, false, BasicVertex.stride, BasicVertex.normalByteOffset);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        GL30.glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        vboiId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        Utility.exitOnGLError("setupMesh3D");
    }
    
    public void draw() {
        // Bind the texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIds[activeTextureIndex]);

        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexIndices.length, GL11.GL_UNSIGNED_BYTE, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
    }
    
    public void destroy() {
        
        GL30.glBindVertexArray(vaoId);

        // Disable the VBO index from the VAO attributes list
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);

        // Delete the cubeVertex VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboId);

        // Delete the index VBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboiId);

        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
            
        // Delete the texture
        for(int texturId : textureIds)
        {
            GL11.glDeleteTextures(texturId);
        }
    }
}
