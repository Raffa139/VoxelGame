#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;
in float Highlighted;

uniform vec3 iColor;
uniform sampler2DArray sampler;

void main() {
    if (Texs.w == 6) {
        discard;
    }

    vec3 textureCoords = vec3(Texs.xyw);
    vec3 color = texture(sampler, textureCoords).rgb;
    if (Highlighted == 1.0) {
        color += iColor;
    }

    FragColor = vec4(color, 1.0) * Texs.z;
}
