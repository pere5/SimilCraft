/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.Objects;

/**
 *
 * @author Uldrer 2.0
 */
public class BasicVertex {
    // BasicVertex data
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

    public BasicVertex() {

    }

    public BasicVertex(float[] xyzw) {
        this.xyzw = xyzw;
    }
    
    public BasicVertex(float[] xyzw, int[] rgb, int[] st, int[] normal) {
        this.xyzw = new float[] {xyzw[0], xyzw[1], xyzw[2], xyzw[3]};
        this.rgba = new float[] {rgb[0], rgb[1], rgb[2], rgb[3]};
        this.st = new float[] {st[0], st[1]};
        this.normal = new float[] { normal[0], normal[1], normal[2] };
    }
    
    // Getters
    public float[] getElements() {
        float[] out = new float[elementCount];
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
