package org.similcraft.Objects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: perer
 * Date: 2013-12-08
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public interface SimilCraftObject {

    public Vector3f position = new Vector3f(0, 0, 0);
    public Vector3f angle = new Vector3f(0, 0, 0);
    public Vector3f scale = new Vector3f(1, 1, 1);

    public void animate();
    public void draw(int[] texIds,int textureSelector);
    public void scaleTranslateAndRotate(Matrix4f modelMatrix);
    public void mouseButton();
    public void scroll();
    public void destroy();
}
