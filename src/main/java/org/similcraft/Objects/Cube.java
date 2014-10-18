/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.Objects;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;
import org.similcraft.engine.Utility;
import org.similcraft.log.LogFormatter;

import java.nio.FloatBuffer;
import java.util.logging.Logger;

/**
 *
 * @author Per
 */
public class Cube extends Mesh3D implements SimilCraftObject {
    public static final Logger log = Logger.getLogger(Cube.class.getName());
    static { (new LogFormatter()).setFormater(log); }

    public final float[] topLeftBack = new float[] {-0.5f,  0.5f, -0.5f, 1.0f};
    public final float[] topLeftFront = new float[] {-0.5f,  0.5f,  0.5f, 1.0f};
    public final float[] topRightFront = new float[] { 0.5f,  0.5f,  0.5f, 1.0f};
    public final float[] topRightBack = new float[] { 0.5f,  0.5f, -0.5f, 1.0f};
    public final float[] bottomLeftBack = new float[] {-0.5f, -0.5f, -0.5f, 1.0f};
    public final float[] bottomLeftFront = new float[] {-0.5f, -0.5f,  0.5f, 1.0f};
    public final float[] bottomRightFront = new float[] { 0.5f, -0.5f,  0.5f, 1.0f};
    public final float[] bottomRightBack = new float[] { 0.5f, -0.5f, -0.5f, 1.0f};

    private int textureSelector = 0;

    public float[] vboOffsetHolder = new float[4];

    public Cube(Vector3f position) {
        this.position = position;
        textureIds[0] = Utility.loadPNGTexture("assets/images/chess_board.png", GL13.GL_TEXTURE0);
        textureIds[1] = Utility.loadPNGTexture("assets/images/Board.png", GL13.GL_TEXTURE0);
        BasicVertex[] cubeVertices = createCubeVertices();
        int[] cubeIndices = createCubeIndices();
        mesh3D(cubeVertices, cubeIndices);
    }

    public void animate() {
        // Apply and update BasicVertex data
        // Update vertices in the VBO, first bind the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        for (int i = 0; i < vertices.length; i++) {
            BasicVertex basicVertex = vertices[i];

//            // Define offset
//            float offsetX = (float) (Math.cos(vboOffsetHolder[i] += 0.02f) * 0.1f);
//            float offsetY = (float) (Math.sin(vboOffsetHolder[i] += 0.02f) * 0.1f);
//            float offsetZ = (float) (Math.cos(vboOffsetHolder[i] += 0.02f) * 0.1f);
//
//            // Offset the BasicVertex position
//            float[] xyzwOriginalCopy = basicVertex.xyzw.clone();
//            basicVertex.xyzw[0] += offsetX;
//            basicVertex.xyzw[1] += offsetY;
//            basicVertex.xyzw[2] += offsetZ;


            // Put the new data in a ByteBuffer (in the view of a FloatBuffer)
            FloatBuffer vertexFloatBuffer = verticesByteBuffer.asFloatBuffer();
            vertexFloatBuffer.rewind();
            vertexFloatBuffer.put(basicVertex.getElements());
            vertexFloatBuffer.flip();

            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i * BasicVertex.stride, vertexFloatBuffer);

            // Restore the BasicVertex data
            //basicVertex.xyzw  = xyzwOriginalCopy;
        }
        // And of course unbind
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private int[] createCubeIndices() {
        int i = 0;
        return new int[]{
                (i += 0),   1+i,    2+i,
                2+i,        3+i,    0+i,
                (i += 4),   1+i,    2+i,
                2+i,        3+i,    0+i,
                (i += 4),   1+i,    2+i,
                2+i,        3+i,    0+i,
                (i += 4),   1+i,    2+i,
                2+i,        3+i,    0+i,
                (i += 4),   1+i,    2+i,
                2+i,        3+i,    0+i,
                (i += 4),   1+i,    2+i,
                2+i,        3+i,    0+i
        };
    }

    public BasicVertex[] createCubeVertices() {
        return new BasicVertex[]{

            //createQuadFront
            new BasicVertex(topLeftFront, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,0,1}),
            new BasicVertex(bottomLeftFront, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,0,1}),
            new BasicVertex(bottomRightFront, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,0,1}),
            new BasicVertex(topRightFront, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,0,1}),

            //createQuadTop
            new BasicVertex(topRightBack, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,1,0}),
            new BasicVertex(topRightFront, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,1,0}),
            new BasicVertex(topLeftFront, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,1,0}),
            new BasicVertex(topLeftBack, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,1,0}),

            //createQuadBottom
            new BasicVertex(bottomLeftBack, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,-1,0}),
            new BasicVertex(bottomLeftFront, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,-1,0}),
            new BasicVertex(bottomRightFront, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,-1,0}),
            new BasicVertex(bottomRightBack, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,-1,0}),

            //createQuadLeft
            new BasicVertex(topLeftBack, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{-1,0,0}),
            new BasicVertex(bottomLeftBack, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{-1,0,0}),
            new BasicVertex(bottomLeftFront, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{-1,0,0}),
            new BasicVertex(topLeftFront, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{-1,0,0}),

            //createQuadRight
            new BasicVertex(topRightFront, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{1,0,0}),
            new BasicVertex(bottomRightFront, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{1,0,0}),
            new BasicVertex(bottomRightBack, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{1,0,0}),
            new BasicVertex(topRightBack, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{1,0,0}),

            //createQuadBack
            new BasicVertex(topRightBack, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,0,-1}),
            new BasicVertex(bottomRightBack, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,0,-1}),
            new BasicVertex(bottomLeftBack, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,0,-1}),
            new BasicVertex(topLeftBack, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,0,-1}),
        };
    }
}
