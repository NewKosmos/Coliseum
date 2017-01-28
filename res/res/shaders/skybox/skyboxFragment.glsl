#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in float pass_height;

//---------UNIFORM------------
uniform vec4 colour1;
uniform vec4 colour2;
uniform float dayFactor;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
    // Calculates the star colours.
	vec2 x = gl_FragCoord.xy;
	vec3 a = vec3(max((fract(dot(sin(x), x)) - 0.99) * 90.0, 0.0));

	// Calculates the skybox fade factor.
	float fadeFactor = 1.0 - smoothlyStep(-256.0, 72.0, pass_height);

	// Creates the output image.
	out_colour = mix(colour1, vec4(a, 1.0), dayFactor);
	out_colour = mix(out_colour, colour2, fadeFactor);
}