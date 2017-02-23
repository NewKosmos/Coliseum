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

uniform vec3 lightDirection;
uniform vec2 lightBias;

uniform float atlasRows;
uniform vec2 atlasOffset;

//---------OUT------------
out vec4 pass_positionRelativeToCam;
out vec2 pass_textureCoords;
out vec3 pass_surfaceNormal;
out vec4 pass_shadowCoords;
out float pass_brightness;

//---------MAIN------------
void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_position, 1.0);
	pass_positionRelativeToCam = viewMatrix * worldPosition;

	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	gl_Position = projectionMatrix * pass_positionRelativeToCam;

	pass_textureCoords = (in_textureCoords / atlasRows) + atlasOffset;
	pass_surfaceNormal = normalize(in_normal);

	vec3 surfaceNormal = normalize((modelMatrix * vec4(pass_surfaceNormal, 0.0)).xyz);
    pass_brightness = max(dot(-lightDirection, surfaceNormal), 0.0) * lightBias.x + lightBias.y;
}
