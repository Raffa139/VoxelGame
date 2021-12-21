#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;

uniform float iTime;
uniform vec3 iColor;
uniform sampler2D sampler;

void main() {
    FragColor = texture(sampler, Texs.xy) * Texs.z;
}
