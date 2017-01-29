#version 130

//---------IN------------
layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_textureCoords;

//---------UNIFORM------------
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 clipPlane;
uniform mat4 modelMatrix;
uniform mat4 shadowSpaceMatrix;
uniform float shadowDistance;

//---------OUT------------
out vec4 pass_positionRelativeToCam;
out vec2 pass_textureCoords;
out vec4 pass_shadowCoords;

//---------MAIN------------
void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_position, 1.0);
	mat4 modelViewMatrix = viewMatrix * modelMatrix;
	pass_positionRelativeToCam = viewMatrix * worldPosition;

	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	gl_Position = projectionMatrix * pass_positionRelativeToCam;

	pass_textureCoords = in_textureCoords;

	pass_shadowCoords = shadowSpaceMatrix * worldPosition;
	float distanceAway = length(pass_positionRelativeToCam.xyz);
    distanceAway = distanceAway - ((shadowDistance * 2.0) - (shadowDistance));
    distanceAway = distanceAway / shadowDistance;
    pass_shadowCoords.w = clamp(1.0 - distanceAway, 0.0, 1.0);
}
