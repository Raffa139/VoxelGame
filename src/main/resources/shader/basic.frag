#version 330 core

layout (location = 0) out vec4 FragColor;

uniform float iTime;

void main() {
    FragColor = vec4((sin(iTime / 2.0)+1.0)/2.0, (sin(iTime * 2.0)+1.0)/2.0, (sin(iTime)+1.0)/2.0, 1.0);
}
