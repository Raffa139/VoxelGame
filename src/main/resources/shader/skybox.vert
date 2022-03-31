#version 330 core

layout (location = 0) in vec3 iPos;

uniform mat4 iView;
uniform mat4 iProjection;

out vec3 Texs;

void main() {
    Texs = vec3(iPos.x, -iPos.y, iPos.z);
    gl_Position = (iProjection * iView * vec4(iPos, 1.0)).xyww;
}
