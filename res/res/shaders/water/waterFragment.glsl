#version 130

//---------INCLUDES------------
#include "fog.glsl"
#include "shadows.glsl"

//---------IN------------
in vec4 pass_positionRelativeToCam;
in vec3 pass_surfaceNormal;
in vec4 pass_shadowCoords;

//---------UNIFORM------------
layout(binding = 1) uniform sampler2D shadowMap;
uniform vec3 diffuseColour;
uniform vec3 lightDirection;
uniform float shadowMapSize;
uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	vec3 unitNormal = normalize(pass_surfaceNormal);

	float fogFactor = visibility(pass_positionRelativeToCam, fogDensity, fogGradient);
	float shadeFactor = max(dot(-lightDirection, unitNormal), 0.0) * LIGHT_BIAS.x + LIGHT_BIAS.y;
	shadeFactor = shadeFactor * shadow(shadowMap, pass_shadowCoords, shadowMapSize);

	out_colour = vec4(diffuseColour.rgb * shadeFactor, 1.0f);
	out_colour = mix(vec4(fogColour, 1.0), out_colour, fogFactor);
}