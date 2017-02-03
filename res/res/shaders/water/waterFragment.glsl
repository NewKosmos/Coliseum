#version 130

//---------INCLUDES------------
#include "fog.glsl"
#include "shadows.glsl"

//---------IN------------
in vec4 pass_positionRelativeToCam;
in vec3 pass_surfaceNormal;
in vec4 pass_shadowCoords;
in vec4 pass_clipSpace;
in float pass_brightness;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D reflectionMap;
layout(binding = 1) uniform sampler2D shadowMap;
uniform vec4 diffuseColour;

uniform float shadowMapSize;
uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

uniform bool ignoreShadows;
uniform bool ignoreReflections;

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
	float fogFactor = visibility(pass_positionRelativeToCam, fogDensity, fogGradient);
	float shadeFactor = pass_brightness;

	if (!ignoreShadows) {
	    shadeFactor = shadeFactor * shadow(shadowMap, pass_shadowCoords, shadowMapSize);
	}

    if (!ignoreReflections) {
        vec2 normalizedDeviceCoords = (pass_clipSpace.xy / pass_clipSpace.w) / 2.0 + 0.5;
        vec2 reflectionTextureCoords = getReflectionTexCoords(normalizedDeviceCoords);
        vec3 reflectionColour = texture(reflectionMap, reflectionTextureCoords).rgb;
        out_colour = vec4(mix(reflectionColour, diffuseColour.rgb, diffuseColour.a), 1.0f);
	} else {
        out_colour = vec4(diffuseColour.rgb, 1.0f);
	}

	out_colour = vec4(out_colour.rgb * shadeFactor, 1.0f);
	out_colour = mix(vec4(fogColour, 1.0), out_colour, fogFactor);
}