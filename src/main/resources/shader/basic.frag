#version 330 core

layout (location = 0) out vec4 FragColor;

in vec3 Texs;

uniform float iTime;
uniform vec3 iColor;
uniform sampler2D sampler;

void main() {
    //FragColor = vec4((sin(iTime / 2.0)+1.0)/2.0, (sin(iTime * 2.0)+1.0)/2.0, (sin(iTime)+1.0)/2.0, 1.0);
    //FragColor = vec4(texture(sampler, Texs).g * iColor, 1.0);
    FragColor = texture(sampler, Texs.xy) * Texs.z;
}
