#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in vec3 pass_position;

//---------UNIFORM------------

//---------OUT------------
layout(location = 0) out vec4 out_albedo;
layout(location = 1) out vec4 out_normals;
layout(location = 2) out vec4 out_extras;

//---------MAIN------------
void main(void) {
    // Calculates the star colours.
	//float w = snoise(pass_position);
	vec2 x = gl_FragCoord.xy;
	vec3 starColour = vec3(max((fract(dot(sin(x), x)) - 0.99) * 90.0, 0.0));

	//if (starColour.r < 0.1) {
	//	out_albedo = vec4(0.0);
	//	discard;
	//}

	out_albedo = vec4(starColour, 1.0);
	out_normals = vec4(0.0, 1.0, 0.0, 1.0);
	out_extras = vec4(1.0, 0.0, (2.0 / 3.0), 1.0); // Ignores lighting.
}