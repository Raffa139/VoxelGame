#version 330 core

layout (location = 0) out vec4 FragColor;

in vec2 Texs;

uniform sampler2D normalVoxelSampler;
uniform sampler2D transparentSampler;

void main() {
    vec3 voxelColor = texture(normalVoxelSampler, Texs).rgb;
    vec4 transparentColor = texture(transparentSampler, Texs);

    vec3 color = voxelColor;
    if (transparentColor.a == 1.0) {
        color = mix(transparentColor.rgb, voxelColor, 0.3);
    }
    FragColor = vec4(color, 1.0);
}
