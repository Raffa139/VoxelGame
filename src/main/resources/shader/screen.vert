#version 330 core

layout (location = 0) in vec2 iPos;
layout (location = 1) in vec2 iTexs;

out vec2 Texs;

void main() {
    gl_Position = vec4(iPos, 0.0, 1.0);
    Texs = iTexs;
}
