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
    vec4 color = texture(sampler, textureCoords);

    if (color.a == 0) {
        discard;
    }

    if (Highlighted == 1.0) {
        color += vec4(iColor, 1.0);
    }

    FragColor = vec4(color) * Texs.z;
}
