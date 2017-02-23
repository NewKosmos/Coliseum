#version 130

//---------INCLUDES------------
#include "fog.glsl"

//---------IN------------
in vec4 pass_positionRelativeToCam;
in vec2 pass_textureCoords;
in vec3 pass_surfaceNormal;
in float pass_brightness;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D diffuseMap;

uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);

	if (diffuseColour.a < 0.4){
		out_colour = vec4(0.0);
		discard;
	}

	out_colour = vec4(diffuseColour.rgb * pass_brightness, diffuseColour.a);
	out_colour = mix(vec4(fogColour, 1.0), out_colour, visibility(pass_positionRelativeToCam, fogDensity, fogGradient));
}