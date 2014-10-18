package org.similcraft.Objects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: perer
 * Date: 2013-12-08
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public interface SimilCraftObject {

    public void animate();
    public void draw();
    public Matrix4f getTransformationMatrix();
    public void destroy();

}
