#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;
in float Highlighted;

uniform vec3 iHighlightColor;
uniform sampler2DArray textureArray;

void main() {
    vec3 textureCoords = vec3(Texs.xyw);
    vec4 color = texture(textureArray, textureCoords);

    if (color.a == 0) {
        discard;
    }

    float lightLevel = Texs.z;
    vec3 highlight = iHighlightColor * Highlighted;
    vec3 combinedColor = color.rgb + highlight;

    FragColor = vec4(combinedColor * lightLevel, 1.0);
}
