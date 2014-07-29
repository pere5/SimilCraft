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

import org.similcraft.Objects.Cube;
import org.similcraft.Objects.SimilCraftObject;
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
    // Moving variables
    private int projectionMatrixLocation = 0;
    private int viewMatrixLocation = 0;
    private int modelMatrixLocation = 0;
    private Matrix4f projectionMatrix = null;
    private FloatBuffer matrix44Buffer = null;
    private Vector3f cameraPos;
    private List<SimilCraftObject> similCraftObjectList = new ArrayList<>();

    public void run() {
        // Initialize OpenGL (Display)
        setupOpenGL();
        setupShaders();
        setupMatrices();
        setupCameraPos();
        setupObjects();
        GL20.glUseProgram(pId);
        while (!Display.isCloseRequested()) {
            // Do a single loop (logic/render)
            loopCycle();

            // Force a maximum FPS of about 60
            Display.sync(60);
            // Let the CPU synchronize with the GPU if GPU is tagging behind
            Display.update();
        }
        GL20.glUseProgram(0);
        // Destroy OpenGL (Display)
        destroyOpenGL();
    }

    private void setupCameraPos() {
        cameraPos = new Vector3f(0, 0, -2);
    }

    private void setupObjects() {
        similCraftObjectList.add(new Cube(new Vector3f(-1, -1, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(-1, 0, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(-1, 1, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(0, -1, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(0, 0, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(0, 1, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(1, -1, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(1, 0, 0)));
        similCraftObjectList.add(new Cube(new Vector3f(1, 1, 0)));
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

        // Create a FloatBuffer with the proper size to store our matrices later
        matrix44Buffer = BufferUtils.createFloatBuffer(16);
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

        processModelViewProjection();
        Utility.exitOnGLError("loopCycle");
    }

    private void processModelViewProjection() {
        //-- Update matrices
        // Reset view and model matrices
        Matrix4f viewMatrix = new Matrix4f();

        // Translate camera
        Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        // Scale, translate and rotate model
        for (SimilCraftObject sco : similCraftObjectList) {
            Matrix4f modelMatrix = sco.scaleTranslateAndRotate();

            // Upload matrices to the uniform variables
            projectionMatrix.store(matrix44Buffer);
            matrix44Buffer.flip();
            GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
            viewMatrix.store(matrix44Buffer);
            matrix44Buffer.flip();
            GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
            modelMatrix.store(matrix44Buffer);
            matrix44Buffer.flip();
            GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);

            sco.processKeyboard();
            sco.scroll();
            sco.mouseButton();
            sco.animate();
            sco.draw();
        }
    }

    private void destroyOpenGL() {

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