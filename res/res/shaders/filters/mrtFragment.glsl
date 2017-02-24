#version 130

//---------INCLUDES------------
#include "fog.glsl"
#include "shadows.glsl"

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalAlbedo;
layout(binding = 1) uniform sampler2D originalPosition;
layout(binding = 2) uniform sampler2D originalNormals;
layout(binding = 3) uniform sampler2D originalDepth;
layout(binding = 4) uniform sampler2D shadowMap;

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

//---------CALCULATE DEPTH------------
float calculateDepth() {
	float depth = texture(originalDepth, pass_textureCoords).r;
	return 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));
}

//---------MAIN------------
void main(void) {
	vec4 albedo = texture(originalAlbedo, pass_textureCoords);
	vec4 position = texture(originalPosition, pass_textureCoords);
	vec4 normals = texture(originalNormals, pass_textureCoords);
	float depth = calculateDepth();

	if (albedo.a == 0.0) {
	    out_colour = vec4(fogColour, 1.0);
	    return;
	}

    vec4 shadowCoords = shadowSpaceMatrix * position;
    //  float distanceAway = length(toCamera.xyz);
    //  distanceAway = distanceAway - ((shadowDistance * 2.0) - shadowTransition);
    //  distanceAway = distanceAway / shadowTransition;
    //  shadowCoords.w = clamp(1.0 - distanceAway, 0.0, 1.0);

    vec3 colour = vec3(albedo);
    float brightness = max(dot(-lightDirection, normals.rgb), 0.0) * lightBias.x + lightBias.y;
    float shadow = shadow(shadowMap, shadowCoords, shadowMapSize);
	//  float shadow = texture(shadowMap, shadowCoords.xy).r;

    //out_colour = vec4(normals.rgb, 1.0);
    out_colour = vec4(colour * shadow * brightness, 1.0);
    out_colour = mix(vec4(fogColour, 1.0), out_colour, visibility(depth, fogDensity, fogGradient));
}
