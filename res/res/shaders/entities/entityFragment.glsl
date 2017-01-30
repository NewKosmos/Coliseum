#version 130

//---------CONSTANT------------
const int SHADOW_PCF = 2;
const float SHADOW_BIAS = 0.0002;
const float SHADOW_DARKNESS = 0.7;

//---------IN------------
in vec4 pass_positionRelativeToCam;
in vec2 pass_textureCoords;
in vec3 pass_surfaceNormal;
in vec4 pass_shadowCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D diffuseMap;
layout(binding = 1) uniform sampler2D shadowMap;
uniform bool useNormalMap;
uniform float transparency;
uniform vec3 lightDirection;
uniform float shadowMapSize;
uniform float darkness;
uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

uniform bool ignoreShadows;
uniform bool ignoreFog;

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
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);
	vec3 unitNormal = normalize(pass_surfaceNormal);
	float shadeFactor = 1.0;
	float fogFactor = 1.0;

	if (diffuseColour.a < 0.4){
		out_colour = vec4(0.0);
		discard;
	}

	if (!ignoreShadows) {
	    shadeFactor = shadow();
	}

	if (!ignoreFog) {
	    fogFactor = visibility();
	}

	out_colour = vec4(diffuseColour.rgb * shadow() * (-(darkness - 0.5) + 0.5), diffuseColour.a);
	out_colour = mix(vec4(fogColour, 1.0), out_colour, fogFactor);
}