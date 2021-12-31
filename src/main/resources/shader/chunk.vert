#version 330 core

layout (location = 0) in uint iVertexData;

uniform mat4 iModel;
uniform mat4 iView;
uniform mat4 iProjection;

out vec4 Texs;
out float Highlighted;

vec2 texCoords[4] = vec2[4](
    vec2(1.0f, 0.0f),
    vec2(1.0f, 1.0f),
    vec2(0.0f, 1.0f),
    vec2(0.0f, 0.0f)
);

void main() {
    float x = float((iVertexData & 0x3Fu << 26) >> 26);
    float y = float((iVertexData & 0x3Fu << 20) >> 20);
    float z = float((iVertexData & 0x3Fu << 14) >> 14);

    gl_Position = iProjection * iView * iModel * vec4(x, y, z, 1.0);

    uint texIndex = uint((iVertexData & 0x3u << 12) >> 12);
    float texLayer = float((iVertexData & 0xFFu << 4) >> 4);
    float lightLevel = float((iVertexData & 0x7u << 1) >> 1) / 5.0f;
    float highlighted = float(iVertexData & 0x1u);

    Texs = vec4(texCoords[texIndex], lightLevel, texLayer);
    Highlighted = highlighted;
}
