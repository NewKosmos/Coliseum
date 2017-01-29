#version 130

//---------IN------------
layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_textureCoords;

//---------UNIFORM------------
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 clipPlane;
uniform mat4 modelMatrix;

//---------OUT------------
out vec4 pass_positionRelativeToCam;
out vec2 pass_textureCoords;

//---------MAIN------------
void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_position, 1.0);
	mat4 modelViewMatrix = viewMatrix * modelMatrix;
	pass_positionRelativeToCam = viewMatrix * worldPosition;

	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	gl_Position = projectionMatrix * pass_positionRelativeToCam;

	pass_textureCoords = in_textureCoords;
}
