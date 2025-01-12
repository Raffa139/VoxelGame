#version 330 core

layout (location = 0) out vec4 FragColor;

in vec2 Texs;

uniform sampler2D solidVoxels;
uniform sampler2D transparentVoxels;
uniform sampler2D solidDepthSampler;
uniform sampler2D transparentDepthSampler;

const float near = 0.01;
const float far = 1000.0;

void main() {
    vec3 solidColor = texture(solidVoxels, Texs).rgb;
    vec4 transparentColor = texture(transparentVoxels, Texs);

    float solidDepth = texture(solidDepthSampler, Texs).r;
    float transparentDepth = texture(transparentDepthSampler, Texs).r;

    bool hasTransparentFrags = transparentColor.a > 0.0;
    bool transparentAboveSolid = transparentDepth < solidDepth;

    vec3 color = solidColor;

    if (hasTransparentFrags && transparentAboveSolid) {
        float vd = (near * solidDepth) / (far - solidDepth * (far - near));
        float td = (near * transparentDepth) / (far - transparentDepth * (far - near));
        float d = clamp((vd - td) * 50.0, 1.0, 1.43);

        color = mix(solidColor, transparentColor.rgb, d * 0.7);
    }

    FragColor = vec4(color, 1.0);
}
