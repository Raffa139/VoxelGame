#version 330 core

layout (location = 0) out vec4 FragColor;

in vec4 Texs;
in vec3 VertPos;

uniform sampler2D normalMapSampler;

uniform vec3 iCameraPosition;
uniform vec3 iLightDirection;
uniform float iTime;

vec3 calculateSpecular();

void main() {
    if (Texs.w != 6) {
        FragColor = vec4(0.0);
        return;
    }

    vec3 color = vec3(0.1, 0.2, 0.7);
    vec3 specular = calculateSpecular();

    FragColor = vec4(color + specular, 1.0);
}

vec3 calculateSpecular() {
    float t = iTime / 4.0;
    vec3 normalColor = texture(normalMapSampler, vec2(Texs.x + t, Texs.y)).rgb;
    vec3 normal = vec3(normalColor.r * 2.0 - 1.0, normalColor.b, normalColor.g * 2.0 - 1.0);
    vec3 reflectDirection = reflect(-normalize(iLightDirection), normal); // Requires direction from light to us (we just have the direction from us to the light)

    // Specular
    vec3 viewDirection = normalize(iCameraPosition - VertPos);
    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0), 16.0);
    vec3 specular = vec3(0.5) * spec;

    return specular;
}