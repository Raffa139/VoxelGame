#version 330 core

layout (location = 0) in vec4 iVertex;

uniform mat4 iProjection;

out vec2 Texs;

void main() {
    //gl_Position = iProjection * vec4(iVertex.xy, 0.0, 1.0);
    gl_Position = vec4(iVertex.xy, 0.0, 1.0);
    Texs = iVertex.zw;
}
