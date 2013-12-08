/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.engine;

/**
 *
 * @author LWJGL website & Per
 */
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.similcraft.log.LogFormatter;
import org.springframework.stereotype.Component;

@Component
public class Engine {

    public static final Logger log = Logger.getLogger(Engine.class.getName());
    static { (new LogFormatter()).setFormater(log); }

    // Setup variables
    private final String WINDOW_TITLE = "It is the cube";
    private final int WIDTH = 640;
    private final int HEIGHT = 420;

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
    private List<SimilCraftObject> similCraftObjectList = new ArrayList<>();

    public void run() {
        // Initialize OpenGL (Display)
        setupOpenGL();
        setupShaders();
        setupTextures();
        setupMatrices();
        setupCameraPos();
        setupObjects();
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

    private void setupCameraPos() {
        cameraPos = new Vector3f(0, 0, -1);
    }

    private void setupObjects() {
        similCraftObjectList.add(new Cube());
        similCraftObjectList.add(new Cube());
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

    private void setupShaders() {
        // Load the cubeVertex shader
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
        logicCycle();
        renderCycle();

        Utility.exitOnGLError("loopCycle");
    }

    private void logicCycle() {

        processKeyboard();
        processMouse();
        processModelViewProjection();
        processVBO();
    }

    private void processVBO() {
        for (SimilCraftObject sco : similCraftObjectList) {
            sco.animate();
        }
    }

    private void renderCycle() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(pId);
        for (SimilCraftObject sco : similCraftObjectList) {
            sco.draw(texIds, textureSelector);
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
        for (SimilCraftObject sco : similCraftObjectList) {
            sco.scaleTranslateAndRotate(modelMatrix);
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

    private void processMouse() {
        proccessButtonOne();
        proccessScroll();
    }

    private void proccessScroll() {
        for (SimilCraftObject sco : similCraftObjectList) {
            sco.scroll();
        }
    }

    private void proccessButtonOne() {
        for (SimilCraftObject sco : similCraftObjectList) {
            sco.mouseButton();
        }
    }

    private void processKeyboard() {
        float scaleDelta = 0.1f;
        Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
        Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);
        float rotationDelta = 1f;
        float posDelta = 0.05f;

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
                    for (SimilCraftObject sco : similCraftObjectList) {
                        sco.angle.x += rotationDelta;
                    }
                    break;
                case Keyboard.KEY_DOWN:
                    for (SimilCraftObject sco : similCraftObjectList) {
                        sco.angle.y += rotationDelta;
                    }
                    break;
                case Keyboard.KEY_LEFT:
                    for (SimilCraftObject sco : similCraftObjectList) {
                        sco.angle.z += rotationDelta;
                    }
                    break;
                case Keyboard.KEY_RIGHT:
                    for (SimilCraftObject sco : similCraftObjectList) {
                        sco.angle.z -= rotationDelta;
                    }
                    break;
                case Keyboard.KEY_ADD:
                    for (SimilCraftObject sco : similCraftObjectList) {
                        Vector3f.add(sco.scale, scaleAddResolution, sco.scale);
                    }
                    break;
                case Keyboard.KEY_SUBTRACT:
                    for (SimilCraftObject sco : similCraftObjectList) {
                        Vector3f.add(sco.scale, scaleMinusResolution, sco.scale);
                    }
                    break;
            }
        }
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

        for (SimilCraftObject sco : similCraftObjectList) {
            sco.destroy();
        }
        Utility.exitOnGLError("destroyOpenGL");

        Display.destroy();
    }
}