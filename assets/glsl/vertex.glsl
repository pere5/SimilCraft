#version 150 core
 
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform mat3 viewNormalTransform;
uniform mat3 modelNormalTransform;
 
in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;
in vec3 in_Normal;
 
out vec4 pass_Color;
out vec2 pass_TextureCoord;
out vec3 normal;
 
void main(void) {
    gl_Position = in_Position;
    // Override gl_Position with our new calculated position
    gl_Position =  projectionMatrix * viewMatrix * modelMatrix * in_Position;

    normal = viewNormalTransform*modelNormalTransform*in_Normal;

    pass_Color = in_Color;
    pass_TextureCoord = in_TextureCoord;
}