#version 150 core
 
uniform sampler2D texture_diffuse;
 
in vec4 pass_Color;
in vec2 pass_TextureCoord;
in vec3 normal;

out vec4 out_Color;
 
void main(void) {
    out_Color = pass_Color;

    vec3 normalized_normal = normalize(normal);

    // Override out_Color with our texture pixel
    out_Color = texture2D(texture_diffuse, pass_TextureCoord);

    out_Color = clamp(out_Color*dot(normalized_normal,vec3(0,0,1)),0.0,1.0);
}