#version 330 core

layout (location = 0) out vec4 FragColor;

in vec2 Texs;

uniform sampler2D sampler;
uniform vec3 iTextColor;

void main() {
    vec4 sampledText = vec4(1.0, 1.0, 1.0, texture(sampler, Texs).r);
    FragColor = vec4(iTextColor, 1.0) * sampledText;
}
