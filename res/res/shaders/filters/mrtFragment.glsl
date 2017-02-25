#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------CONSTANT------------
const int SHADOW_PCF = 1;
const float SHADOW_BIAS = 0.001;
const float SHADOW_DARKNESS = 0.6;

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalAlbedo;
layout(binding = 1) uniform sampler2D originalNormals;
layout(binding = 2) uniform sampler2D originalDepth;
layout(binding = 3) uniform sampler2D shadowMap;

uniform mat4 viewInverseMatrix;
uniform mat4 projectionInverseMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightDirection;
uniform vec2 lightBias;

uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

uniform mat4 shadowSpaceMatrix;
uniform float shadowDistance;
uniform float shadowTransition;
uniform float shadowMapSize;

uniform float nearPlane;
uniform float farPlane;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------CALCULATE LOCATION------------
vec3 decodeLocation() {
    float depth = texture(originalDepth, pass_textureCoords).x;
    vec4 p = projectionInverseMatrix * (vec4(pass_textureCoords, depth, 1.0) * 2.0 - 1.0);
    return vec3(viewInverseMatrix * vec4(p.xyz / p.w, 1.0));
}

//---------SHADOW------------
float shadow(sampler2D shadowMap, vec4 shadowCoords, float shadowMapSize) {
	float shadowTexelSize = 1.0 / shadowMapSize;
	float shadowHalfw = shadowTexelSize * 0.5;
	float shadowTotal = 0.0;
	float shadowValue = 0.0;
	float shadowShadeFactor;
	shadowValue = texture(shadowMap, shadowCoords.xy + vec2(0.0 + shadowHalfw, 0.0 + shadowHalfw)).r;

    if (shadowCoords.x > 0.0 && shadowCoords.x < 1.0 && shadowCoords.y > 0.0 && shadowCoords.y < 1.0 && shadowCoords.z > 0.0 && shadowCoords.z < 1.0) {
        if (shadowValue + SHADOW_BIAS < shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowValue = texture(shadowMap, shadowCoords.xy + vec2(shadowTexelSize + shadowHalfw, 0.0 + shadowHalfw)).r;

        if (shadowValue + SHADOW_BIAS< shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowValue = texture(shadowMap, shadowCoords.xy + vec2(0.0 + shadowHalfw, shadowTexelSize + shadowHalfw)).r;

        if (shadowValue + SHADOW_BIAS < shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowValue = texture(shadowMap, shadowCoords.xy + vec2(shadowTexelSize + shadowHalfw, shadowTexelSize + shadowHalfw)).r;

        if (shadowValue + SHADOW_BIAS < shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowShadeFactor = 1.0 - (shadowTotal / 4.0);
    } else {
        shadowShadeFactor = 1.0;
    }

    return shadowShadeFactor;

    /*float totalTextels = (SHADOW_PCF * 2.0 + 1.0) * (SHADOW_PCF * 2.0 + 1.0);
    float texelSize = 1.0 / shadowMapSize;
    float total = 0.0;

    for (int x = -SHADOW_PCF; x <= SHADOW_PCF; x++) {
        for (int y = -SHADOW_PCF; y <= SHADOW_PCF; y++) {
            float shadowValue = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;

            if (shadowCoords.z > shadowValue + SHADOW_BIAS) {
                total += 1.0;
            }
        }
    }

    total /= totalTextels;
    return 1.0 - (total * SHADOW_DARKNESS * shadowCoords.w);*/
}

//---------FOG VISIBILITY------------
float visibility(vec4 positionRelativeToCam, float fogDensity, float fogGradient) {
	return clamp(exp(-pow((length(positionRelativeToCam.xyz) * fogDensity), fogGradient)), 0.0, 1.0);
}

//---------MAIN------------
void main(void) {
	vec4 albedo = texture(originalAlbedo, pass_textureCoords);

	// Ignores anything this is not a rendered object, so mostly the cleared colour.
	if (albedo.a == 0.0) {
	    out_colour = vec4(fogColour, 1.0);
	    return;
	}

	vec4 normals = texture(originalNormals, pass_textureCoords);
	vec4 worldPosition = vec4(decodeLocation(), 1.0);

    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    vec4 shadowCoords = shadowSpaceMatrix * worldPosition;
    float distanceAway = length(positionRelativeToCam.xyz);
    distanceAway = distanceAway - ((shadowDistance * 2.0) - shadowTransition);
    distanceAway = distanceAway / shadowTransition;
    shadowCoords.w = clamp(1.0 - distanceAway, 0.0, 1.0);

    vec3 colour = vec3(albedo);
    float brightness = max(dot(-lightDirection, normals.rgb), 0.0) * lightBias.x + lightBias.y;
    float shadow = shadow(shadowMap, shadowCoords, shadowMapSize);

    out_colour = vec4(colour * shadow * brightness, 1.0);
    out_colour = mix(vec4(fogColour, 1.0), out_colour, visibility(positionRelativeToCam, fogDensity, fogGradient));
}
