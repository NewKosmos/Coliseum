#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in vec3 pass_surfaceNormal;
in vec4 pass_shadowCoords;

//---------UNIFORM------------
layout(binding = 1) uniform sampler2D shadowMap;
uniform vec3 lightDirection;
uniform float shadowMapSize;
uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------SHADOW------------
float shadow(void) {
	float shadowTexelSize = 1.0 / shadowMapSize;
	float shadowHalfw = shadowTexelSize * 0.5;
	float shadowTotal = 0.0;
	float shadowValue = 0.0;
	float shadowShadeFactor;
	shadowValue = texture(shadowMap, pass_shadowCoords.xy + vec2(0 + shadowHalfw, 0 + shadowHalfw)).r;

    if (pass_shadowCoords.x > 0.0 && pass_shadowCoords.x < 1.0 && pass_shadowCoords.y > 0.0 && pass_shadowCoords.y < 1.0 && pass_shadowCoords.z > 0.0 && pass_shadowCoords.z < 1.0) {
        if (shadowValue + SHADOW_BIAS < pass_shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * pass_shadowCoords.w;
        }

        shadowValue = texture(shadowMap, pass_shadowCoords.xy + vec2(shadowTexelSize + shadowHalfw, 0 + shadowHalfw)).r;

        if (shadowValue  + SHADOW_BIAS< pass_shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * pass_shadowCoords.w;
        }

        shadowValue = texture(shadowMap, pass_shadowCoords.xy + vec2(0 + shadowHalfw, shadowTexelSize + shadowHalfw)).r;

        if (shadowValue + SHADOW_BIAS < pass_shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * pass_shadowCoords.w;
        }

        shadowValue = texture(shadowMap, pass_shadowCoords.xy + vec2(shadowTexelSize + shadowHalfw, shadowTexelSize + shadowHalfw)).r;

        if (shadowValue + SHADOW_BIAS < pass_shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * pass_shadowCoords.w;
        }

        shadowShadeFactor = 1.0 - (shadowTotal / 4.0);
    } else {
        shadowShadeFactor = 1.0;
    }

    return shadowShadeFactor;
}

//---------VISIBILITY------------
float visibility(void) {
	return clamp(exp(-pow((length(pass_positionRelativeToCam.xyz) * fogDensity), fogGradient)), 0.0, 1.0);
}

//---------MAIN------------
void main(void) {
	vec3 unitNormal = normalize(pass_surfaceNormal);

   // const vec2 lightBias = vec2(0.7, 0.6);
	//float shadeFactor = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
	out_colour = vec4(vec3(0.0, 0.0, 1.0) * shadeFactor, 1.0);
}