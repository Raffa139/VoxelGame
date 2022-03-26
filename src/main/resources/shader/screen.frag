#version 330 core

layout (location = 0) out vec4 FragColor;

in vec2 Texs;

uniform sampler2D normalVoxelSampler;
uniform sampler2D transparentSampler;
uniform sampler2D voxelDepthSampler;
uniform sampler2D transparentDepthSampler;

const float near = 0.01;
const float far = 1000.0;

void main() {
    vec3 voxelColor = texture(normalVoxelSampler, Texs).rgb;
    vec4 transparentColor = texture(transparentSampler, Texs);

    vec3 color = voxelColor;
    if (transparentColor.a == 1.0) {
        float voxelDepth = texture(voxelDepthSampler, Texs).r;
        float transparentDepth = texture(transparentDepthSampler, Texs).r;

        float vd = (near * voxelDepth) / (far - voxelDepth * (far - near));
        float td = (near * transparentDepth) / (far - transparentDepth * (far - near));
        float d = clamp((vd - td) * 50.0, 1.0, 1.43);

        color = mix(voxelColor, transparentColor.rgb, d * 0.7);
    }

    FragColor = vec4(color, 1.0);
}
