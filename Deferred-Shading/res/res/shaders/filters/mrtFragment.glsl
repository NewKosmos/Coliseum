#version 130

//---------INCLUDES------------
#include "fog.glsl"

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalPosition;
layout(binding = 1) uniform sampler2D originalNormals;
layout(binding = 2) uniform sampler2D originalAlbedo;
layout(binding = 3) uniform sampler2D originalSpecular;

uniform mat4 viewMatrix;

uniform vec3 lightDirection;
uniform vec2 lightBias;

uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	vec4 position = texture(originalPosition, pass_textureCoords);
	vec3 normals = texture(originalNormals, pass_textureCoords).rgb;
	vec3 albedo = texture(originalAlbedo, pass_textureCoords).rgb;
	vec3 specular = texture(originalSpecular, pass_textureCoords).rgb;

	if (specular != 0.0) {
	//    vec4 positionRelativeToCam = viewMatrix * position;

        vec3 colour = vec3(albedo);
        float brightness = max(dot(-lightDirection, normals), 0.0) * lightBias.x + lightBias.y;

        out_colour = vec4(colour * brightness, 1.0);
    //    out_colour = mix(vec4(fogColour, 1.0), out_colour, visibility(positionRelativeToCam, fogDensity, fogGradient));
	} else {
	    out_colour = vec4(fogColour, 1.0);
	}
}
