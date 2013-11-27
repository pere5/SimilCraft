/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.engine;

/**
 *
 * @author LWJGL website & Per
 */
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.logging.Logger;
import org.lwjgl.input.Mouse;
import org.similcraft.log.LogFormatter;
import org.springframework.stereotype.Component;

@Component
public class Engine {

    public static final Logger log = Logger.getLogger(Engine.class.getName());
    static { (new LogFormatter()).setFormater(log); }
    
    // Setup variables
    private final String WINDOW_TITLE = "The Quad: Moving";
    private final int WIDTH = 640;
    private final int HEIGHT = 420;

    private List<Quad> quadList = new ArrayList();
    // Shader variables
    private int vsId = 0;
    private int fsId = 0;
    private int pId = 0;
    // Texture variables
    private int[] texIds = new int[]{0, 0};
    private int textureSelector = 0;
    // Moving variables
    private int projectionMatrixLocation = 0;
    private int viewMatrixLocation = 0;
    private int modelMatrixLocation = 0;
    private Matrix4f projectionMatrix = null;
    private Matrix4f viewMatrix = null;
    private Matrix4f modelMatrix = null;
    private FloatBuffer matrix44Buffer = null;
    private Vector3f cameraPos;

    public void run() {
        // Initialize OpenGL (Display)
        setupOpenGL();
        VertexData[] vertexData1 = CubeState.createQuadFront();
        VertexData[] vertexData2 = CubeState.createQuadTop();
        VertexData[] vertexData3 = CubeState.createQuadBottom();
        VertexData[] vertexData4 = CubeState.createQuadLeft();
        VertexData[] vertexData5 = CubeState.createQuadRight();
        VertexData[] vertexData6 = CubeState.createQuadBack();
        quadList.add(setupQuad(vertexData1));
        quadList.add(setupQuad(vertexData2));
        quadList.add(setupQuad(vertexData3));
        quadList.add(setupQuad(vertexData4));
        quadList.add(setupQuad(vertexData5));
        quadList.add(setupQuad(vertexData6));
        setupShaders();
        setupTextures();
        setupMatrices();
        setupCameraPos();

        while (!Display.isCloseRequested()) {
            // Do a single loop (logic/render)
            loopCycle();

            // Force a maximum FPS of about 60
            Display.sync(60);
            // Let the CPU synchronize with the GPU if GPU is tagging behind
            Display.update();
        }

        // Destroy OpenGL (Display)
        destroyOpenGL();
    }

    private void setupMatrices() {
        // Setup projection matrix
        projectionMatrix = new Matrix4f();
        float fieldOfView = 60f;
        float aspectRatio = (float) WIDTH / (float) HEIGHT;
        float near_plane = 0.1f;
        float far_plane = 100f;

        float y_scale = Utility.coTangent(Utility.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);

        // Setup view matrix
        viewMatrix = new Matrix4f();

        // Setup model matrix
        modelMatrix = new Matrix4f();

        // Create a FloatBuffer with the proper size to store our matrices later
        matrix44Buffer = BufferUtils.createFloatBuffer(16);
    }

    private void setupTextures() {
        texIds[0] = Utility.loadPNGTexture("assets/images/chess_board.png", GL13.GL_TEXTURE0);
        texIds[1] = Utility.loadPNGTexture("assets/images/Board.png", GL13.GL_TEXTURE0);

        Utility.exitOnGLError("setupTexture");
    }

    private void setupOpenGL() {
        // Setup an OpenGL context with API version 3.2
        try {
            PixelFormat pixelFormat = new PixelFormat();
            ContextAttribs contextAtrributes = new ContextAttribs(3, 2);
            contextAtrributes.withForwardCompatible(true);
            contextAtrributes.withProfileCore(true);

            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle(WINDOW_TITLE);
            Display.create(pixelFormat, contextAtrributes);

            GL11.glViewport(0, 0, WIDTH, HEIGHT);
        } catch (LWJGLException e) {
            System.out.println(e);
            System.exit(-1);
        }

        // Setup an XNA like background color
        GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        //GL11.glEnable(GL11.GL_LIGHTING);
        // Map the internal OpenGL coordinate system to the entire screen
        GL11.glViewport(0, 0, WIDTH, HEIGHT);

        Utility.exitOnGLError("setupOpenGL");
    }

    private static Quad setupQuad(VertexData[] vertexData) {
        Quad quad = new Quad();
        quad.setVertices(vertexData);

        // Put each 'Vertex' in one FloatBuffer
        quad.setVerticesByteBuffer(BufferUtils.createByteBuffer(quad.getVertices().length * VertexData.stride));
        FloatBuffer verticesFloatBuffer = quad.getVerticesByteBuffer().asFloatBuffer();
        for (int i = 0; i < quad.getVertices().length; i++) {
            // Add position, color and texture floats to the buffer
            verticesFloatBuffer.put(quad.getVertices()[i].getElements());
        }
        verticesFloatBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = {
            0, 1, 2,
            2, 3, 0
        };
        quad.setIndicesCount(indices.length);
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(quad.getIndicesCount());
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        // Create a new Vertex Array Object in memory and select it (bind)
        quad.setVaoId(GL30.glGenVertexArrays());
        GL30.glBindVertexArray(quad.getVaoId());

        // Create a new Vertex Buffer Object(VBO) in memory and select it (bind)
        quad.setVboId(GL15.glGenBuffers());
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quad.getVboId());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);

        // Put the position coordinates in attribute list 0
        GL20.glVertexAttribPointer(0, VertexData.positionElementCount, GL11.GL_FLOAT, false, VertexData.stride, VertexData.positionByteOffset);
        // Put the color components in attribute list 1
        GL20.glVertexAttribPointer(1, VertexData.colorElementCount, GL11.GL_FLOAT, false, VertexData.stride, VertexData.colorByteOffset);
        // Put the texture coordinates in attribute list 2
        GL20.glVertexAttribPointer(2, VertexData.textureElementCount, GL11.GL_FLOAT, false, VertexData.stride, VertexData.textureByteOffset);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        GL30.glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        quad.setVboiId(GL15.glGenBuffers());
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, quad.getVboiId());
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Set the default quad rotation, scale and position values
        quad.setModelPos(new Vector3f(0, 0, 0));
        quad.setModelAngle(new Vector3f(0, 0, 0));
        quad.setModelScale(new Vector3f(1, 1, 1));

        Utility.exitOnGLError("setupQuad");
        return quad;
    }

    private void setupShaders() {
        // Load the vertex shader
        vsId = Utility.loadShader("assets/glsl/vertex.glsl", GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        fsId = Utility.loadShader("assets/glsl/fragment.glsl", GL20.GL_FRAGMENT_SHADER);

        // Create a new shader program that links both shaders
        pId = GL20.glCreateProgram();
        GL20.glAttachShader(pId, vsId);
        GL20.glAttachShader(pId, fsId);

        // Position information will be attribute 0
        GL20.glBindAttribLocation(pId, 0, "in_Position");
        // Color information will be attribute 1
        GL20.glBindAttribLocation(pId, 1, "in_Color");
        // Textute information will be attribute 2
        GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");

        GL20.glLinkProgram(pId);
        // Get matrices uniform locations
        projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
        viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
        modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");

        GL20.glValidateProgram(pId);

        Utility.exitOnGLError("setupShaders");
    }

    private void loopCycle() {
        // Update logic
        logicCycle();
        // Update rendered frame
        renderCycle();

        Utility.exitOnGLError("loopCycle");
    }

    private void logicCycle() {

        processKeyboard();
        processMouse();
        processModelViewProjection();
        processVBO();

        Utility.exitOnGLError("logicCycle");
    }

    private void renderCycle() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(pId);
        for (Quad quad : quadList) {
            drawElement(quad, texIds, textureSelector);
        }
        GL20.glUseProgram(0);

        Utility.exitOnGLError("renderCycle");
    }

    private void processModelViewProjection() {
        //-- Update matrices
        // Reset view and model matrices
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();

        // Translate camera
        Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);

        // Scale, translate and rotate model
        for (Quad quad : quadList) {
            Matrix4f.scale(quad.getModelScale(), modelMatrix, modelMatrix);
            Matrix4f.translate(quad.getModelPos(), modelMatrix, modelMatrix);
            Matrix4f.rotate(Utility.degreesToRadians(quad.getModelAngle().z), new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
            Matrix4f.rotate(Utility.degreesToRadians(quad.getModelAngle().y), new Vector3f(0, 1, 0), modelMatrix, modelMatrix);
            Matrix4f.rotate(Utility.degreesToRadians(quad.getModelAngle().x), new Vector3f(1, 0, 0), modelMatrix, modelMatrix);
        }
        // Upload matrices to the uniform variables
        GL20.glUseProgram(pId);

        projectionMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
        viewMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
        modelMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);

        GL20.glUseProgram(0);
    }

    private void processVBO() {
        // Apply and update vertex data
        for (Quad quad : quadList) {
            // Update vertices in the VBO, first bind the VBO
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quad.getVboId());
            for (int i = 0; i < quad.getVertices().length; i++) {
                VertexData vertex = quad.getVertices()[i];

                // Define offset
                float offsetX = (float) (Math.cos(quad.getVboOffsetHolder()[i] += 0.02) * 0.1);
                float offsetY = (float) (Math.sin(quad.getVboOffsetHolder()[i] += 0.02) * 0.1);
                float offsetZ = (float) (Math.cos(quad.getVboOffsetHolder()[i] += 0.02) * 0.1);

                // Offset the vertex position
                float[] xyz = vertex.getXYZ();
                vertex.setXYZ(xyz[0] + offsetX, xyz[1] + offsetY, xyz[2] + offsetZ);

                // Put the new data in a ByteBuffer (in the view of a FloatBuffer)
                FloatBuffer vertexFloatBuffer = quad.getVerticesByteBuffer().asFloatBuffer();
                vertexFloatBuffer.rewind();
                vertexFloatBuffer.put(vertex.getElements());
                vertexFloatBuffer.flip();

                GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i * VertexData.stride, vertexFloatBuffer);

                // Restore the vertex data
                vertex.setXYZ(xyz[0], xyz[1], xyz[2]);
            }
            // And of course unbind
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
        Utility.exitOnGLError("logicCycle");
    }

    private static void drawElement(Quad quad, int[] texIds, int textureSelector) {
        // Bind the texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[textureSelector]);

        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(quad.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, quad.getVboiId());

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, quad.getIndicesCount(), GL11.GL_UNSIGNED_BYTE, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void destroyOpenGL() {
        // Delete the texture
        GL11.glDeleteTextures(texIds[0]);
        GL11.glDeleteTextures(texIds[1]);

        // Delete the shaders
        GL20.glUseProgram(0);
        GL20.glDetachShader(pId, vsId);
        GL20.glDetachShader(pId, fsId);

        GL20.glDeleteShader(vsId);
        GL20.glDeleteShader(fsId);
        GL20.glDeleteProgram(pId);

        // Select the VAO

        for (Quad quad : quadList) {
            GL30.glBindVertexArray(quad.getVaoId());

            // Disable the VBO index from the VAO attributes list
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);

            // Delete the vertex VBO
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL15.glDeleteBuffers(quad.getVboId());

            // Delete the index VBO
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL15.glDeleteBuffers(quad.getVboiId());

            // Delete the VAO
            GL30.glBindVertexArray(0);
            GL30.glDeleteVertexArrays(quad.getVaoId());
        }
        Utility.exitOnGLError("destroyOpenGL");

        Display.destroy();
    }

    private void processMouse() {
        proccessButtonOne();
        proccessScroll();
    }

    private void proccessScroll() {
        for (Quad quad : quadList) {
            float posDelta = 0.1f;
            int dw = Mouse.getDWheel();
            if (dw > 0) {
                quad.getModelPos().z += posDelta;
            } else if (dw < 0) {
                quad.getModelPos().z -= posDelta;
            }
        }
    }
    private boolean firstTimeMouseDown = false;
    private float firstTimeDownSystemValueX = 0;
    private float firstTimeDownSystemValueY = 0;
    private float firstTimeDownMouseValueX = 0;
    private float firstTimeDownMouseValueY = 0;

    private void proccessButtonOne() {
        int button = Mouse.getButtonIndex(Mouse.getButtonName(0));
        for (Quad quad : quadList) {
            if (Mouse.isButtonDown(button)) {
                if (firstTimeMouseDown) {
                    firstTimeDownSystemValueX = quad.getModelAngle().x;
                    firstTimeDownSystemValueY = quad.getModelAngle().y;
                    firstTimeDownMouseValueX = Mouse.getX();
                    firstTimeDownMouseValueY = Mouse.getY();
                    firstTimeMouseDown = false;
                }

                quad.getModelAngle().y = firstTimeDownSystemValueY - (firstTimeDownMouseValueX - Mouse.getX());
                quad.getModelAngle().x = firstTimeDownSystemValueX + (firstTimeDownMouseValueY - Mouse.getY());
            } else {
                firstTimeMouseDown = true;
            }
        }
    }

    private void processKeyboard() {
        float scaleDelta = 0.1f;
        Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
        Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);
        float rotationDelta = 15f;
        float posDelta = 0.1f;

        while (Keyboard.next()) {
            // Only listen to events where the key was pressed (down event)
            if (!Keyboard.getEventKeyState()) {
                continue;
            }

            // Switch textures depending on the key released
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_1:
                    textureSelector = 0;
                    break;
                case Keyboard.KEY_2:
                    textureSelector = 1;
                    break;
            }
            // Change model scale, rotation and translation values
            switch (Keyboard.getEventKey()) {
                // Move
                case Keyboard.KEY_UP:
                    for (Quad quad : quadList) {
                        quad.getModelPos().y += posDelta;
                    }
                    break;
                case Keyboard.KEY_DOWN:
                    for (Quad quad : quadList) {
                        quad.getModelPos().y -= posDelta;
                    }
                    break;
                // Scale
                case Keyboard.KEY_ADD:
                    for (Quad quad : quadList) {
                        Vector3f.add(quad.getModelScale(), scaleAddResolution, quad.getModelScale());
                    }
                    break;
                case Keyboard.KEY_SUBTRACT:
                    for (Quad quad : quadList) {
                        Vector3f.add(quad.getModelScale(), scaleMinusResolution, quad.getModelScale());
                    }
                    break;
                // Rotation
                case Keyboard.KEY_LEFT:
                    for (Quad quad : quadList) {
                        quad.getModelAngle().z += rotationDelta;
                    }
                    break;
                case Keyboard.KEY_RIGHT:
                    for (Quad quad : quadList) {
                        quad.getModelAngle().z -= rotationDelta;
                    }
                    break;
            }
        }
    }

    private void setupCameraPos() {
        cameraPos = new Vector3f(0, 0, 0);
    }
}