/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.Objects;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.similcraft.engine.Utility;
import org.similcraft.log.LogFormatter;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Per
 */
public class Cube implements SimilCraftObject {
    public static final Logger log = Logger.getLogger(Cube.class.getName());
    static { (new LogFormatter()).setFormater(log); }

    public final CubeVertex v1 = new CubeVertex(new float[] {-0.5f,  0.5f, -0.5f, 1.0f});
    public final CubeVertex v2 = new CubeVertex(new float[] {-0.5f,  0.5f,  0.5f, 1.0f});
    public final CubeVertex v3 = new CubeVertex(new float[] { 0.5f,  0.5f,  0.5f, 1.0f});
    public final CubeVertex v4 = new CubeVertex(new float[] { 0.5f,  0.5f, -0.5f, 1.0f});
    public final CubeVertex v5 = new CubeVertex(new float[] {-0.5f, -0.5f, -0.5f, 1.0f});
    public final CubeVertex v6 = new CubeVertex(new float[] {-0.5f, -0.5f,  0.5f, 1.0f});
    public final CubeVertex v7 = new CubeVertex(new float[] { 0.5f, -0.5f,  0.5f, 1.0f});
    public final CubeVertex v8 = new CubeVertex(new float[] { 0.5f, -0.5f, -0.5f, 1.0f});

    private List<CubeSide> cubeSideList = new ArrayList<>();

    private static final Vector3f AXIS_Z = new Vector3f(0, 0, 1);
    private static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);
    private static final Vector3f AXIS_X = new Vector3f(1, 0, 0);
    private int[] texIds = new int[]{0, 0};
    private int textureSelector = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f angle = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);

    public Cube(Vector3f position) {
        this.position = position;
        texIds[0] = Utility.loadPNGTexture("assets/images/chess_board.png", GL13.GL_TEXTURE0);
        texIds[1] = Utility.loadPNGTexture("assets/images/Board.png", GL13.GL_TEXTURE0);
        CubeVertex[] cubeVertex1 = createQuadFront();
        CubeVertex[] cubeVertex2 = createQuadTop();
        CubeVertex[] cubeVertex3 = createQuadBottom();
        CubeVertex[] cubeVertex4 = createQuadLeft();
        CubeVertex[] cubeVertex5 = createQuadRight();
        CubeVertex[] cubeVertex6 = createQuadBack();
        cubeSideList.add(setupCubeSide(cubeVertex1));
        cubeSideList.add(setupCubeSide(cubeVertex2));
        cubeSideList.add(setupCubeSide(cubeVertex3));
        cubeSideList.add(setupCubeSide(cubeVertex4));
        cubeSideList.add(setupCubeSide(cubeVertex5));
        cubeSideList.add(setupCubeSide(cubeVertex6));
    }

    public void draw() {
        for (CubeSide cubeSide : cubeSideList) {
            // Bind the texture
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[textureSelector]);

            // Bind to the VAO that has all the information about the vertices
            GL30.glBindVertexArray(cubeSide.vaoId);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL20.glEnableVertexAttribArray(3);

            // Bind to the index VBO that has all the information about the order of the vertices
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, cubeSide.vboiId);

            // Draw the vertices
            GL11.glDrawElements(GL11.GL_TRIANGLES, cubeSide.indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

            // Put everything back to default (deselect)
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(3);
            GL30.glBindVertexArray(0);
        }
    }

    public void animate() {
        // Apply and update cubeVertex data
        for (CubeSide cubeSide : cubeSideList) {
            // Update vertices in the VBO, first bind the VBO
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cubeSide.vboId);
            for (int i = 0; i < cubeSide.vertices.length; i++) {
                CubeVertex cubeVertex = cubeSide.vertices[i];

                // Define offset
                float offsetX = (float) (Math.cos(cubeSide.vboOffsetHolder[i] += 0.02f) * 0.1f);
                float offsetY = (float) (Math.sin(cubeSide.vboOffsetHolder[i] += 0.02f) * 0.1f);
                float offsetZ = (float) (Math.cos(cubeSide.vboOffsetHolder[i] += 0.02f) * 0.1f);

                // Offset the cubeVertex position
                float[] xyzwOriginalCopy = cubeVertex.xyzw.clone();
                cubeVertex.xyzw[0] += offsetX;
                cubeVertex.xyzw[1] += offsetY;
                cubeVertex.xyzw[2] += offsetZ;
                

                // Put the new data in a ByteBuffer (in the view of a FloatBuffer)
                FloatBuffer vertexFloatBuffer = cubeSide.verticesByteBuffer.asFloatBuffer();
                vertexFloatBuffer.rewind();
                vertexFloatBuffer.put(cubeVertex.getElements());
                vertexFloatBuffer.flip();

                GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i * CubeVertex.stride, vertexFloatBuffer);

                // Restore the cubeVertex data
                cubeVertex.xyzw  = xyzwOriginalCopy;
            }
            // And of course unbind
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
    }

    public Matrix4f scaleTranslateAndRotate() {
        Matrix4f modelMatrix = new Matrix4f();
        Matrix4f.scale(scale, modelMatrix, modelMatrix);
        Matrix4f.translate(position, modelMatrix, modelMatrix);
        Matrix4f.rotate(Utility.degreesToRadians(angle.z), AXIS_Z, modelMatrix, modelMatrix);
        Matrix4f.rotate(Utility.degreesToRadians(angle.y), AXIS_Y, modelMatrix, modelMatrix);
        Matrix4f.rotate(Utility.degreesToRadians(angle.x), AXIS_X, modelMatrix, modelMatrix);
        return modelMatrix;
    }

    private CubeSide setupCubeSide(CubeVertex[] cubeVertex) {
        CubeSide cubeSide = new CubeSide();
        cubeSide.vertices = cubeVertex;

        // Put each 'CubeVertex' in one FloatBuffer
        cubeSide.verticesByteBuffer = BufferUtils.createByteBuffer(cubeSide.vertices.length * CubeVertex.stride);
        FloatBuffer verticesFloatBuffer = cubeSide.verticesByteBuffer.asFloatBuffer();
        for (int i = 0; i < cubeSide.vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesFloatBuffer.put(cubeSide.vertices[i].getElements());
        }
        verticesFloatBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = {
                0, 1, 2,
                2, 3, 0
        };
        cubeSide.indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(cubeSide.indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        // Create a new CubeVertex Array Object in memory and select it (bind)
        cubeSide.vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(cubeSide.vaoId);

        // Create a new CubeVertex Buffer Object(VBO) in memory and select it (bind)
        cubeSide.vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cubeSide.vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);

        // Put the position coordinates in attribute list 0
        GL20.glVertexAttribPointer(0, CubeVertex.positionElementCount, GL11.GL_FLOAT, false, CubeVertex.stride, CubeVertex.positionByteOffset);
        // Put the color components in attribute list 1
        GL20.glVertexAttribPointer(1, CubeVertex.colorElementCount, GL11.GL_FLOAT, false, CubeVertex.stride, CubeVertex.colorByteOffset);
        // Put the texture coordinates in attribute list 2
        GL20.glVertexAttribPointer(2, CubeVertex.textureElementCount, GL11.GL_FLOAT, false, CubeVertex.stride, CubeVertex.textureByteOffset);
        // Put the normal components in attribute list 3
        GL20.glVertexAttribPointer(3, CubeVertex.normalElementCount, GL11.GL_FLOAT, false, CubeVertex.stride, CubeVertex.normalByteOffset);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        GL30.glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        cubeSide.vboiId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, cubeSide.vboiId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        Utility.exitOnGLError("setupQuad");
        return cubeSide;
    }

    /* create */
    private CubeVertex c(CubeVertex vX, int[] rgb, int[] st) {
        CubeVertex cubeVertex = new CubeVertex();
        cubeVertex.xyzw = new float[] {vX.xyzw[0], vX.xyzw[1], vX.xyzw[2], vX.xyzw[3]};
        cubeVertex.rgba = new float[] {rgb[0], rgb[1], rgb[2], rgb[3]};
        cubeVertex.st = new float[] {st[0], st[1]};
        cubeVertex.normal = new float[] { 0, 0, 0 };
        return cubeVertex;
    }
    
    /* create */
    private CubeVertex c(CubeVertex vX, int[] rgb, int[] st, int[] normal) {
        CubeVertex cubeVertex = new CubeVertex();
        cubeVertex.xyzw = new float[] {vX.xyzw[0], vX.xyzw[1], vX.xyzw[2], vX.xyzw[3]};
        cubeVertex.rgba = new float[] {rgb[0], rgb[1], rgb[2], rgb[3]};
        cubeVertex.st = new float[] {st[0], st[1]};
        cubeVertex.normal = new float[] { normal[0], normal[1], normal[2] };
        return cubeVertex;
    }
    
    public CubeVertex[] createQuadFront() {
        CubeVertex[] cubeVertex = new CubeVertex[]{
            c(v2, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,0,1}),
            c(v6, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,0,1}),
            c(v7, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,0,1}),
            c(v3, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,0,1})};
        return cubeVertex;
    }

    public CubeVertex[] createQuadTop() {
        CubeVertex[] cubeVertex = new CubeVertex[]{
            c(v4, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,1,0}),
            c(v3, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,1,0}),
            c(v2, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,1,0}),
            c(v1, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,1,0})};
        return cubeVertex;
    }

    public CubeVertex[] createQuadBottom() {
        CubeVertex[] cubeVertex = new CubeVertex[]{
            c(v5, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,-1,0}),
            c(v6, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,-1,0}),
            c(v7, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,-1,0}),
            c(v8, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,-1,0})};
        return cubeVertex;
    }

    public CubeVertex[] createQuadLeft() {
        CubeVertex[] cubeVertex = new CubeVertex[]{
            c(v1, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{-1,0,0}),
            c(v5, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{-1,0,0}),
            c(v6, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{-1,0,0}),
            c(v2, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{-1,0,0})};
        return cubeVertex;
    }

    public CubeVertex[] createQuadRight() {
        CubeVertex[] cubeVertex = new CubeVertex[]{
            c(v3, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{1,0,0}),
            c(v7, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{1,0,0}),
            c(v8, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{1,0,0}),
            c(v4, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{1,0,0})};
        return cubeVertex;
    }

    public CubeVertex[] createQuadBack() {
        CubeVertex[] cubeVertex = new CubeVertex[]{
            c(v4, new int[]{1, 0, 0, 1}, new int[]{0, 0}, new int[]{0,0,-1}),
            c(v8, new int[]{0, 1, 0, 1}, new int[]{0, 1}, new int[]{0,0,-1}),
            c(v5, new int[]{0, 0, 1, 1}, new int[]{1, 1}, new int[]{0,0,-1}),
            c(v1, new int[]{1, 1, 1, 1}, new int[]{1, 0}, new int[]{0,0,-1})};
        return cubeVertex;
    }

    boolean keyUp = false;
    boolean keyDown = false;
    boolean keyLeft = false;
    boolean keyRight = false;
    boolean keyAdd = false;
    boolean keySubtract = false;
    float scaleDelta = 0.03f;
    float rotationDelta = 1.5f;
    Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
    Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);
    public void processKeyboard() {
        if (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_1) {
                textureSelector = 0;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_2) {
                textureSelector = 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
                keyUp = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
                keyDown = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
                keyLeft = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
                keyRight = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_ADD) {
                keyAdd = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_SUBTRACT) {
                keySubtract = Keyboard.getEventKeyState();
            }
        }
        if (keyUp) {
            angle.x -= rotationDelta;
        }
        if (keyDown) {
            angle.x += rotationDelta;
        }
        if (keyLeft) {
            angle.y -= rotationDelta;
        }
        if (keyRight) {
            angle.y += rotationDelta;
        }
        if (keyAdd) {
            Vector3f.add(scale, scaleAddResolution, scale);
        }
        if (keySubtract) {
            Vector3f.add(scale, scaleMinusResolution, scale);
        }
    }

    public void destroy() {
        for (CubeSide cubeSide : cubeSideList) {
            GL30.glBindVertexArray(cubeSide.vaoId);

            // Disable the VBO index from the VAO attributes list
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);

            // Delete the cubeVertex VBO
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL15.glDeleteBuffers(cubeSide.vboId);

            // Delete the index VBO
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL15.glDeleteBuffers(cubeSide.vboiId);

            // Delete the VAO
            GL30.glBindVertexArray(0);
            GL30.glDeleteVertexArrays(cubeSide.vaoId);
        }
        // Delete the texture
        GL11.glDeleteTextures(texIds[0]);
        GL11.glDeleteTextures(texIds[1]);
    }

    public void scroll() {
        float posDelta = 0.1f;
        int dw = Mouse.getDWheel();
        if (dw > 0) {
            position.z += posDelta;
        } else if (dw < 0) {
            position.z -= posDelta;
        }
    }

    private boolean firstTimeMouseDown = false;
    private float firstTimeDownSystemValueX = 0;
    private float firstTimeDownSystemValueY = 0;
    private float firstTimeDownMouseValueX = 0;
    private float firstTimeDownMouseValueY = 0;
    public void mouseButton() {
        int button = Mouse.getButtonIndex(Mouse.getButtonName(0));
        if (Mouse.isButtonDown(button)) {
            if (firstTimeMouseDown) {
                firstTimeDownSystemValueX = angle.x;
                firstTimeDownSystemValueY = angle.y;
                firstTimeDownMouseValueX = Mouse.getX();
                firstTimeDownMouseValueY = Mouse.getY();
                firstTimeMouseDown = false;
            }

            angle.y = firstTimeDownSystemValueY - (firstTimeDownMouseValueX - Mouse.getX());
            angle.x = firstTimeDownSystemValueX + (firstTimeDownMouseValueY - Mouse.getY());
        } else {
            firstTimeMouseDown = true;
        }
    }

    public class CubeSide {
        public CubeVertex[] vertices;
        public ByteBuffer verticesByteBuffer;
        public int indicesCount;
        public int vaoId;
        public int vboId;
        public int vboiId;
        public float[] vboOffsetHolder = new float[4];
    }

    private class CubeVertex {
        // CubeVertex data
        public float[] xyzw;
        public float[] rgba;
        public float[] st;
        public float[] normal;
        // The amount of bytes an element has
        public static final int elementBytes = 4;
        // Elements per parameter
        public static final int positionElementCount = 4;
        public static final int colorElementCount = 4;
        public static final int textureElementCount = 2;
        public static final int normalElementCount = 3;
        // Bytes per parameter
        public static final int positionBytesCount = positionElementCount * elementBytes;
        public static final int colorByteCount = colorElementCount * elementBytes;
        public static final int textureByteCount = textureElementCount * elementBytes;
        public static final int normalByteCount = normalElementCount * elementBytes;
        // Byte offsets per parameter
        public static final int positionByteOffset = 0;
        public static final int colorByteOffset = positionByteOffset + positionBytesCount;
        public static final int textureByteOffset = colorByteOffset + colorByteCount;
        public static final int normalByteOffset = textureByteOffset + textureByteCount;
        // The amount of elements that a vertex has
        public static final int elementCount = positionElementCount + colorElementCount + textureElementCount + normalElementCount;
        // The size of a vertex in bytes, like in C/C++: sizeof(CubeVertex)
        public static final int stride = positionBytesCount + colorByteCount + textureByteCount + normalByteCount;

        public CubeVertex() {

        }

        public CubeVertex(float[] xyzw) {
            this.xyzw = xyzw;
        }

        // Getters
        public float[] getElements() {
            float[] out = new float[CubeVertex.elementCount];
            int i = 0;

            // Insert XYZW elements
            out[i++] = xyzw[0];
            out[i++] = xyzw[1];
            out[i++] = xyzw[2];
            out[i++] = xyzw[3];
            // Insert RGBA elements
            out[i++] = rgba[0];
            out[i++] = rgba[1];
            out[i++] = rgba[2];
            out[i++] = rgba[3];
            // Insert ST elements
            out[i++] = st[0];
            out[i++] = st[1];
            // Insert Normal elements
            out[i++] = normal[0];
            out[i++] = normal[1];
            out[i++] = normal[2];

            return out;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getAngle() {
        return angle;
    }

    public Vector3f getScale() {
        return scale;
    }
}
