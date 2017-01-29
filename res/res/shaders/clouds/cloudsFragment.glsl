#version 130

//---------IN------------
in vec4 pass_positionRelativeToCam;
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D diffuseMap;
uniform float darkness;
uniform vec3 fogColour;
uniform float fogDensity;
uniform float fogGradient;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------VISIBILITY------------
float visibility(void) {
	return clamp(exp(-pow((length(pass_positionRelativeToCam.xyz) * fogDensity), fogGradient)), 0.0, 1.0);
}

//---------MAIN------------
void main(void) {
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);

	// Creates the output image.
	out_colour = vec4(1, 0, 0, 1);
	out_colour = mix(vec4(fogColour, 1.0), diffuseColour * (-(darkness - 0.5) + 0.5), visibility());
}