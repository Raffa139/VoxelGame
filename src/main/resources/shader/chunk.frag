#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;
in float Highlighted;

uniform float iTime;
uniform vec3 iColor;
uniform sampler2DArray sampler;

void main() {
    vec3 textureCoords = vec3(Texs.xyw);

    vec3 color;
    if (Texs.w == 6) {
        color = vec3(0.1, 0.2, 0.7);
    } else {
        color = texture(sampler, textureCoords).rgb;
    }

    if (Highlighted == 1.0) {
        color += iColor;
    }

    FragColor = vec4(color, 1.0) * Texs.z;
}
