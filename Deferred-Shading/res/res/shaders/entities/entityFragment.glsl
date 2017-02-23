#version 130

//---------IN------------
in vec2 pass_textureCoords;
in vec4 pass_worldPosition;
in vec3 pass_surfaceNormal;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D diffuseMap;

//---------OUT------------
layout(location = 0) out vec4 out_position;
layout(location = 1) out vec4 out_normals;
layout(location = 2) out vec4 out_albedo;
layout(location = 3) out vec4 out_specular;

//---------MAIN------------
void main(void) {
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);

	if (diffuseColour.a < 0.4){
		out_albedo = vec4(0.0);
		discard;
	}

	//out_colour = vec4(diffuseColour.rgb * pass_brightness, diffuseColour.a);
	//out_colour = mix(vec4(fogColour, 1.0), out_colour, visibility(pass_positionRelativeToCam, fogDensity, fogGradient));
	out_position = vec4(pass_worldPosition);
	out_normals = vec4(pass_surfaceNormal, 1.0);
	out_albedo = vec4(diffuseColour.rgb, 1.0);
	out_specular = vec4(0.0, 1.0, 0.0, 1.0);
}