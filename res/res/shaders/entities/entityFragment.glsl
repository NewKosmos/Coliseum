#version 130

//---------INCLUDES------------
#include "fog.glsl"
#include "shadows.glsl"

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
	    shadeFactor = shadow(shadowMap, pass_shadowCoords, shadowMapSize);
	} else {
	    shadeFactor = max(dot(-lightDirection, unitNormal), 0.0) * LIGHT_BIAS.x + LIGHT_BIAS.y;
	}

	if (!ignoreFog) {
	    fogFactor = visibility(pass_positionRelativeToCam, fogDensity, fogGradient);
	}

	out_colour = vec4(diffuseColour.rgb * shadeFactor * (-(darkness - 0.5) + 0.5), diffuseColour.a);
	out_colour = mix(vec4(fogColour, 1.0), out_colour, fogFactor);
}