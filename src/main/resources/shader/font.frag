#version 330 core

layout (location = 0) out vec4 FragColor;

in vec2 Texs;

uniform sampler2D sampler;
uniform vec3 iTextColor;

void main() {
    FragColor = vec4(iTextColor, texture(sampler, Texs).a);
}
