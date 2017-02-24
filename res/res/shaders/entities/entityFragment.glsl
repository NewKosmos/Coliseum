#version 130

//---------IN------------
in vec2 pass_textureCoords;
in vec4 pass_worldPosition;
in vec4 pass_positionRelativeToCam;
in vec3 pass_surfaceNormal;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D diffuseMap;

//---------OUT------------
layout(location = 0) out vec4 out_albedo;
layout(location = 1) out vec4 out_position;
layout(location = 2) out vec4 out_toCamera;
layout(location = 3) out vec4 out_normals;

//---------MAIN------------
void main(void) {
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);

	if (diffuseColour.a < 0.2){
		out_albedo = vec4(0.0);
		discard;
	}

	out_albedo = vec4(diffuseColour);
	out_position = vec4(pass_worldPosition);
	out_toCamera = vec4(pass_positionRelativeToCam);
	out_normals = vec4(pass_surfaceNormal, 1.0);
}