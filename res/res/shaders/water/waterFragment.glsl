#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in vec3 pass_surfaceNormal;

//---------UNIFORM------------
uniform vec3 lightDirection;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	vec3 unitNormal = normalize(pass_surfaceNormal);

    const vec2 lightBias = vec2(0.7, 0.6);
	float shadeFactor = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
	out_colour = vec4(vec3(0.0, 0.0, 1.0) * shadeFactor, 1.0);
}