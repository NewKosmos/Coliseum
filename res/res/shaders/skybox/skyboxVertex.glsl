#version 130

//---------IN------------
layout(location = 0) in vec3 in_position;

//---------UNIFORM------------
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 clipPlane;

//---------OUT------------
out vec3 pass_textureCoords;

//---------MAIN------------
void main(void) {
	gl_ClipDistance[0] = dot(vec4(in_position, 1.0), clipPlane);
	gl_Position = projectionMatrix * viewMatrix * vec4(in_position, 1.0);

	pass_textureCoords = in_position;
}
