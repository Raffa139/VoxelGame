#version 330 core

layout (location = 0) out vec4 FragColor;

in vec3 Texs;

uniform samplerCube cubeSampler;

void main() {
    FragColor = texture(cubeSampler, Texs);
}
