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
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.similcraft.Objects.Cube;
import org.similcraft.Objects.Camera;
import org.similcraft.Objects.SimilCraftObject;
import org.similcraft.input.InputHandler;
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
    private int worldCameraTransformLocation = 0;
    private int modelWorldTransformLocation = 0;
    private int worldCameraNormalTransformLocation = 0;
    private int modelWorldNormalTransformLocation = 0;
    private int lightPositionLocation = 0;
    private int lightColorIntensityLocation = 0;
    
    private FloatBuffer matrix44Buffer = null;
    private FloatBuffer matrix33Buffer = null;
    private Camera camera;
    private Vector3f lightPositionInWorldCoords;
    private Vector3f lightColorIntensity;
    private List<SimilCraftObject> similCraftObjectList = new ArrayList<>();
    private InputHandler inputHandler;

    public void run() {
        // Initialize OpenGL (Display)
        setupOpenGL();
        setupShaders();
        setupMatrices();
        setupCamera();
        setupLighting();
        setupObjects();
        setupInputHandler();
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

    private void setupCamera() {
        
        camera = new Camera();
        //camera.translateObject(new Vector3f(0,0,-2));
        camera.Zoom(-1.0f);
        camera.SetSize(WIDTH, HEIGHT);
        camera.SetFOV(60f);
        camera.SetRadius(10f);
    }
    
    private void setupInputHandler()
    {
        inputHandler = new InputHandler();
        inputHandler.addMouseWheelEventListener(camera);
        inputHandler.addMouseButtonEventListener(camera);
        inputHandler.addKeyEventListener(camera);
    }
    
    private void setupLighting()
    {
        // Lighting position
        lightPositionInWorldCoords = new Vector3f(0, 0, 2);
        
        // Lighting color
        lightColorIntensity = new Vector3f(1, 1, 1);
    }

    private void setupObjects() {
        similCraftObjectList.add(new Cube(new Vector3f(0, 0, 0)));
        
        Cube sco = (Cube) similCraftObjectList.get(0);
        sco.translateWorld(new Vector3f(0,0,1));
    } 

    private void setupMatrices() {
        // Create a FloatBuffer with the proper size to store our matrices later
        matrix44Buffer = BufferUtils.createFloatBuffer(16);
        matrix33Buffer = BufferUtils.createFloatBuffer(9);
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
        // Normal information will be attribute 3
        GL20.glBindAttribLocation(pId, 3, "in_Normal");
       

        GL20.glLinkProgram(pId);
        // Get matrices uniform locations
        projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
        worldCameraTransformLocation = GL20.glGetUniformLocation(pId, "worldCameraTransform");
        modelWorldTransformLocation = GL20.glGetUniformLocation(pId, "modelWorldTransform");
        worldCameraNormalTransformLocation = GL20.glGetUniformLocation(pId, "worldCameraNormalTransform");
        modelWorldNormalTransformLocation = GL20.glGetUniformLocation(pId, "modelWorldNormalTransform");
        lightPositionLocation = GL20.glGetUniformLocation(pId, "lightPosition");
        lightColorIntensityLocation = GL20.glGetUniformLocation(pId, "lightColorIntensity");

        GL20.glValidateProgram(pId);

        Utility.exitOnGLError("setupShaders");
    }

    private void loopCycle() {

        processModelViewProjection();
        Utility.exitOnGLError("loopCycle");
    }

    private void processModelViewProjection() {
        
        // Clear screen
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
        //-- Update matrices
        Matrix4f worldCameraTransform = new Matrix4f();
        Matrix4f worldCameraNormalTransform = new Matrix4f();
        Matrix4f projectionMatrix = camera.GetProjectionMatrix();
        Matrix4f cameraTransform = camera.getTransformationMatrix();
        
        cameraTransform.transpose(worldCameraNormalTransform);
        // Load worldCameraNormalTransform
        worldCameraNormalTransform.store3f(matrix33Buffer);
        matrix33Buffer.flip();
        GL20.glUniformMatrix3(worldCameraNormalTransformLocation, false, matrix33Buffer);
        
        // Load projectionMatrix
        projectionMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
        
        Matrix4f.invert(cameraTransform, worldCameraTransform);
        // Load worldCameraTransform
        worldCameraTransform.store(matrix44Buffer);
        matrix44Buffer.flip();
        GL20.glUniformMatrix4(worldCameraTransformLocation, false, matrix44Buffer);
        
        // Transform light
        Vector3f lightPositionInCameraCoords = Utility.multiplyM4x4WithV3(worldCameraTransform, lightPositionInWorldCoords);
        
        // Load lighting
        GL20.glUniform3f(lightPositionLocation, lightPositionInCameraCoords.x, lightPositionInCameraCoords.y, lightPositionInCameraCoords.z);
        GL20.glUniform3f(lightColorIntensityLocation, lightColorIntensity.x, lightColorIntensity.y, lightColorIntensity.z);
        
        // Scale, translate and rotate model
        for (SimilCraftObject sco : similCraftObjectList) {
            Matrix4f modelMatrix = sco.getTransformationMatrix();
            
            Matrix4f modelTransform = new Matrix4f();
            Matrix4f.invert(modelMatrix,modelTransform);
            modelTransform.transpose();
            
            modelTransform.store3f(matrix33Buffer);
            matrix33Buffer.flip();
            
            Matrix3f modelNormalTransform = new Matrix3f();
            modelNormalTransform.load(matrix33Buffer);
            matrix33Buffer.flip();
            
            
            // Upload matrices to the uniform variables
            modelMatrix.store(matrix44Buffer);
            matrix44Buffer.flip();
            GL20.glUniformMatrix4(modelWorldTransformLocation, false, matrix44Buffer);
            modelNormalTransform.store(matrix33Buffer);
            matrix33Buffer.flip();
            GL20.glUniformMatrix3(modelWorldNormalTransformLocation, false, matrix33Buffer);
            
            
            inputHandler.processKeyboard();
            inputHandler.processScroll();
            inputHandler.mouseButton();
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