#version 150 core
 
uniform mat4 projectionMatrix;
uniform mat4 worldCameraTransform;
uniform mat4 modelWorldTransform;

uniform mat3 worldCameraNormalTransform;
uniform mat3 modelWorldNormalTransform;

uniform vec3 lightPosition; // in camera coordinates
 
in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;
in vec3 in_Normal;
 
out vec4 pass_Color;
out vec2 pass_TextureCoord;
out vec3 normal;
out vec3 lightDir;
 
void main(void) {

    // transform vertex to camera coordinates
    vec3 vertex = vec3( worldCameraTransform * modelWorldTransform * in_Position );

    // Calculate direct and indirect light directions
    lightDir = normalize(lightPosition - vertex);

    // project the point into the camera
    gl_Position =  projectionMatrix * vec4( vertex, 1.0 );

    normal = worldCameraNormalTransform*modelWorldNormalTransform*in_Normal;

    pass_Color = in_Color;
    pass_TextureCoord = in_TextureCoord;
}