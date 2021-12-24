#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;

uniform float iTime;
uniform vec3 iColor;
uniform sampler2D sampler;

void main() {
    vec3 col = vec3(1.0);
    if (Texs.w == 0) col = vec3(0.0, 0.0, 1.0);
    if (Texs.w == 1) col = vec3(1.0, 1.0, 1.0);
    if (Texs.w == 2) col = texture(sampler, Texs.xy).rgb;
    if (Texs.w == 3) col = vec3(1.0, 0.0, 0.0);
    if (Texs.w == 4) col = vec3(0.5, 0.5, 0.5);

    FragColor = vec4(col, 1.0) * Texs.z;
}
