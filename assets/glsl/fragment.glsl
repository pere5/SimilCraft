#version 150 core
 
uniform sampler2D texture_diffuse;
uniform vec3 lightColorIntensity;

in vec4 pass_Color;
in vec2 pass_TextureCoord;
in vec3 normal;
in vec3 lightDir;

out vec4 out_Color;
 
void main(void) {
    out_Color = pass_Color;

    vec3 normalized_normal = normalize(normal);

    float dotLight = dot(normal,lightDir);

    if (dotLight < 0.0) dotLight = 0.0;
    
    // Override out_Color with our texture pixel
    out_Color = texture2D(texture_diffuse, pass_TextureCoord);
    out_Color = clamp(out_Color*dot(normalized_normal,vec3(0,0,1)),0.0,1.0);

    vec3 color = vec3(out_Color.x, out_Color.y, out_Color.z);

    color = lightColorIntensity * color * dotLight;

    vec4 finalcolor = vec4(color, 1.0);

    out_Color = finalcolor;
}