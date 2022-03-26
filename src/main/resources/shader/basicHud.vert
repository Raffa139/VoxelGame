#version 330 core

layout (location = 0) in vec3 iPos;

uniform mat4 iModel;

void main() {
    gl_Position = iModel * vec4(iPos.x, iPos.y, iPos.z, 1.0);
}
