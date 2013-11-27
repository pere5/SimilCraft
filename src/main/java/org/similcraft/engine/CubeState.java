/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.engine;

/**
 *
 * @author Per
 */
public class CubeState {

    public static final VertexData v1 = new VertexData();
    public static final VertexData v2 = new VertexData();
    public static final VertexData v3 = new VertexData();
    public static final VertexData v4 = new VertexData();
    public static final VertexData v5 = new VertexData();
    public static final VertexData v6 = new VertexData();
    public static final VertexData v7 = new VertexData();
    public static final VertexData v8 = new VertexData();

    static {
        v1.setXYZ(-0.5f,  0.5f, -0.5f);
        v2.setXYZ(-0.5f,  0.5f,  0.5f);
        v3.setXYZ( 0.5f,  0.5f,  0.5f);
        v4.setXYZ( 0.5f,  0.5f, -0.5f);
        v5.setXYZ(-0.5f, -0.5f, -0.5f);
        v6.setXYZ(-0.5f, -0.5f,  0.5f);
        v7.setXYZ( 0.5f, -0.5f,  0.5f);
        v8.setXYZ( 0.5f, -0.5f, -0.5f);
    }

    /* create */
    private static VertexData c(VertexData vertexData, int[] rgb, int[] st) {
        VertexData v = new VertexData();
        float[] xyz = vertexData.getXYZ();
        v.setXYZ(xyz[0], xyz[1], xyz[2]);
        v.setRGB(rgb[0], rgb[1], rgb[2]);
        v.setST(st[0], st[1]);
        return v;
    }
    
    public static VertexData[] createQuadFront() {
        VertexData[] vertexData = new VertexData[]{
            c(v2, new int[]{1, 0, 0}, new int[]{0, 0}),
            c(v6, new int[]{0, 1, 0}, new int[]{0, 1}),
            c(v7, new int[]{0, 0, 1}, new int[]{1, 1}),
            c(v3, new int[]{1, 1, 1}, new int[]{1, 0})};
        return vertexData;
    }

    public static VertexData[] createQuadTop() {
        VertexData[] vertexData = new VertexData[]{
            c(v4, new int[]{1, 0, 0}, new int[]{0, 0}),
            c(v3, new int[]{0, 1, 0}, new int[]{0, 1}),
            c(v2, new int[]{0, 0, 1}, new int[]{1, 1}),
            c(v1, new int[]{1, 1, 1}, new int[]{1, 0})};
        return vertexData;
    }

    public static VertexData[] createQuadBottom() {
        VertexData[] vertexData = new VertexData[]{
            c(v5, new int[]{1, 0, 0}, new int[]{0, 0}),
            c(v6, new int[]{0, 1, 0}, new int[]{0, 1}),
            c(v7, new int[]{0, 0, 1}, new int[]{1, 1}),
            c(v8, new int[]{1, 1, 1}, new int[]{1, 0})};
        return vertexData;
    }

    public static VertexData[] createQuadLeft() {
        VertexData[] vertexData = new VertexData[]{
            c(v1, new int[]{1, 0, 0}, new int[]{0, 0}),
            c(v5, new int[]{0, 1, 0}, new int[]{0, 1}),
            c(v6, new int[]{0, 0, 1}, new int[]{1, 1}),
            c(v2, new int[]{1, 1, 1}, new int[]{1, 0})};
        return vertexData;
    }

    public static VertexData[] createQuadRight() {
        VertexData[] vertexData = new VertexData[]{
            c(v3, new int[]{1, 0, 0}, new int[]{0, 0}),
            c(v7, new int[]{0, 1, 0}, new int[]{0, 1}),
            c(v8, new int[]{0, 0, 1}, new int[]{1, 1}),
            c(v4, new int[]{1, 1, 1}, new int[]{1, 0})};
        return vertexData;
    }

    public static VertexData[] createQuadBack() {
        VertexData[] vertexData = new VertexData[]{
            c(v4, new int[]{1, 0, 0}, new int[]{0, 0}),
            c(v8, new int[]{0, 1, 0}, new int[]{0, 1}),
            c(v5, new int[]{0, 0, 1}, new int[]{1, 1}),
            c(v1, new int[]{1, 1, 1}, new int[]{1, 0})};
        return vertexData;
    }
}
