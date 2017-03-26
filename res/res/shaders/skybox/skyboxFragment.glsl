#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------

//---------UNIFORM------------
//layout(binding = 0) uniform samplerCube cubeMap;
uniform vec3 skyColour;
uniform float blendFactor;

//---------OUT------------
layout(location = 0) out vec4 out_albedo;
layout(location = 1) out vec4 out_normals;
layout(location = 2) out vec4 out_extras;

//---------MAIN------------
void main(void) {
	vec2 x = gl_FragCoord.xy;
	// texture(cubeMap, gl_FragCoord).rgb;
	vec3 starColour = vec3(max((fract(dot(sin(x), x)) - 0.99) * 90.0, 0.0));

	out_albedo = vec4(skyColour + mix(vec3(0.0), starColour, blendFactor), 1.0);
	out_normals = vec4(0.0, 1.0, 0.0, 1.0);
	out_extras = vec4(1.0, 0.0, 1.0, 1.0); // Ignores lighting.
}