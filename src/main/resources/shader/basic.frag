#version 330 core

layout (location = 0) out vec4 FragColor;

in vec2 Texs;

uniform float iTime;

void main() {
    //FragColor = vec4((sin(iTime / 2.0)+1.0)/2.0, (sin(iTime * 2.0)+1.0)/2.0, (sin(iTime)+1.0)/2.0, 1.0);
    FragColor = vec4(Texs, Texs.x, 1.0);
}
