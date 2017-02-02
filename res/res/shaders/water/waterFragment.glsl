#version 130

//---------INCLUDES------------
#include "fog.glsl"
#include "shadows.glsl"

//---------IN------------
in vec4 pass_positionRelativeToCam;
in vec3 pass_surfaceNormal;
in vec4 pass_shadowCoords;
in vec4 pass_clipSpace;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D reflectionMap;
layout(binding = 1) uniform sampler2D shadowMap;

uniform vec4 diffuseColour;
uniform vec3 lightDirection;

uniform float shadowMapSize;
uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------REFRACTION------------
vec2 getReflectionTexCoords(vec2 normalizedDeviceCoords){
	vec2 reflectionTextureCoords = vec2(normalizedDeviceCoords.x, -normalizedDeviceCoords.y);
	reflectionTextureCoords.x = clamp(reflectionTextureCoords.x, 0.001, 0.999);
	reflectionTextureCoords.y = clamp(reflectionTextureCoords.y, -0.999, -0.001);
	return reflectionTextureCoords;
}

//---------MAIN------------
void main(void) {
	vec3 unitNormal = normalize(pass_surfaceNormal);

	vec2 normalizedDeviceCoords = (pass_clipSpace.xy / pass_clipSpace.w) / 2.0 + 0.5;
	vec2 reflectionTextureCoords = getReflectionTexCoords(normalizedDeviceCoords);
	vec3 reflectionColour = texture(reflectionMap, reflectionTextureCoords).rgb;

	float fogFactor = visibility(pass_positionRelativeToCam, fogDensity, fogGradient);
	float shadeFactor = max(dot(-lightDirection, unitNormal), 0.0) * LIGHT_BIAS.x + LIGHT_BIAS.y;
	shadeFactor = shadeFactor * shadow(shadowMap, pass_shadowCoords, shadowMapSize);

	out_colour = vec4(mix(reflectionColour, diffuseColour.rgb, diffuseColour.a), 1.0f);
	out_colour = vec4(out_colour.rgb * shadeFactor, 1.0f);
	out_colour = mix(vec4(fogColour, 1.0), out_colour, fogFactor);
}