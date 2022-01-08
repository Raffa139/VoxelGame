#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;

uniform sampler2DArray sampler;

void main() {
    if (Texs.w != 6) {
        FragColor = vec4(0.0);
        return;
    }

    vec3 textureCoords = vec3(Texs.xyw);
    vec3 color = texture(sampler, textureCoords).rgb;
    FragColor = vec4(color, 1.0);
}
