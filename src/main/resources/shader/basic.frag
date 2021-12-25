#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;

uniform float iTime;
uniform vec3 iColor;
uniform sampler2D sampler;

void main() {
    int texIndex = int(Texs.w);

    int col = texIndex % 4;
    float offX = float(col) / 4.0;

    int row = texIndex / 4;
    float offY = float(row) / 4.0;

    vec2 coords = Texs.xy / 4.0;
    coords.x += offX;
    coords.y += offY;

    vec3 color;
    if (Texs.w == 6) {
        color = vec3(0.1, 0.2, 0.7);
    } else {
        color = texture(sampler, coords).rgb;
    }

    FragColor = vec4(color, 1.0) * Texs.z;
}
