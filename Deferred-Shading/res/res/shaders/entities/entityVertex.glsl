#version 130

//---------IN------------
layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_textureCoords;
layout(location = 2) in vec3 in_normal;
layout(location = 3) in vec3 in_tangent;

//---------UNIFORM------------
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 clipPlane;
uniform mat4 modelMatrix;

uniform float atlasRows;
uniform vec2 atlasOffset;

//---------OUT------------
out vec2 pass_textureCoords;
out vec4 pass_worldPosition;
out vec3 pass_surfaceNormal;

//---------MAIN------------
void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_position, 1.0);

	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	gl_Position = projectionMatrix * viewMatrix * worldPosition;

	pass_textureCoords = (in_textureCoords / atlasRows) + atlasOffset;
    pass_worldPosition = worldPosition;
	pass_surfaceNormal = normalize((modelMatrix * vec4(normalize(in_normal), 0.0)).xyz);
}
