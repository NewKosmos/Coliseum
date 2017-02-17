#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in vec3 pass_position;

//---------UNIFORM------------
uniform vec4 colour1;
uniform vec4 colour2;
uniform float dayFactor;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
    // Calculates the star colours.
	//float w = snoise(pass_position);
	vec2 x = gl_FragCoord.xy;
	vec3 starColour = colour2.rgb;
	starColour = starColour + vec3(max((fract(dot(sin(x), x)) - 0.99) * 90.0, 0.0));

	// Calculates the skybox fade factor.
	float fadeFactor = 1.0 - smoothlyStep(-256.0, 72.0, pass_position.y);

	// Creates the output image.
	//out_colour = mix(colour1, vec4(starColour, 1.0), dayFactor);
	//out_colour = mix(out_colour, colour2, fadeFactor);

	out_colour = vec4(colour1.rgb, 1.0);
}