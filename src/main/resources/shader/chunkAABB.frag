#version 330 core

uniform vec3 iColor;

void main() {
    vec3 color = vec3(1.0);
    color *= iColor;

    gl_FragColor = vec4(color, 1.0);
}
