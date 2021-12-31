#version 330 core

layout (location = 0) in vec3 iPos;

uniform mat4 iModel;
uniform mat4 iView;
uniform mat4 iProjection;

void main() {
    gl_Position = iProjection * iView * iModel * vec4(iPos, 1.0);
}
