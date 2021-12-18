#version 330 core

layout (location = 0) in vec3 iPos;
layout (location = 1) in vec3 iTexs;

uniform mat4 iModel;
uniform mat4 iView;
uniform mat4 iProjection;

out vec3 Texs;

void main() {
    gl_Position = iProjection * iView * iModel * vec4(iPos, 1.0);
    Texs = iTexs;
}
