#version 130

//---------CONSTANT------------
const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

//---------IN------------
layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_textureCoords;
layout(location = 2) in vec3 in_normal;
layout(location = 3) in vec3 in_tangent;
layout(location = 4) in ivec3 in_jointIndices;
layout(location = 5) in vec3 in_weights;

//---------UNIFORM------------
layout(binding = 1) uniform sampler2D swayMap;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 clipPlane;
uniform mat4 modelMatrix;

uniform float atlasRows;
uniform vec2 atlasOffset;

uniform bool animated;
uniform mat4 jointTransforms[MAX_JOINTS];

uniform bool swaying;
uniform float systemTime;

//---------OUT------------
out vec4 pass_worldPosition;
out vec3 pass_surfaceNormal;
out vec2 pass_textureCoords;

//---------MAIN------------
void main(void) {
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

    if (animated) {
        for (int i = 0; i < MAX_WEIGHTS; i++){
            vec4 localPosition = jointTransforms[in_jointIndices[i]] * vec4(in_position, 1.0);
            totalLocalPos += localPosition * in_weights[i];

            vec4 worldNormal = jointTransforms[in_jointIndices[i]] * vec4(in_normal, 0.0);
            totalNormal += worldNormal * in_weights[i];
        }
	} else {
	    totalLocalPos = vec4(in_position, 1.0);
	    totalNormal = vec4(in_normal, 0.0);
	}

	pass_textureCoords = (in_textureCoords / atlasRows) + atlasOffset;

	if (swaying) {
	    vec4 swayColour = texture(swayMap, pass_textureCoords);
	    float swayPower = swayColour.r;

	    if (swayPower != 0.0) {
	        float offsetX = swayPower * 0.05 * (sin(systemTime)-0.5*cos(systemTime/2)) * length(totalLocalPos.xyz);
	        float offsetZ = swayPower * 0.05 * (cos(systemTime)-0.5*sin(systemTime/2)) * length(totalLocalPos.xyz);
	        totalLocalPos.x += offsetX;
	        totalLocalPos.z += offsetZ;
	    }
	}

	pass_worldPosition = modelMatrix * totalLocalPos;

	gl_ClipDistance[0] = dot(pass_worldPosition, clipPlane);
	gl_Position = projectionMatrix * viewMatrix * pass_worldPosition;

	pass_surfaceNormal = (modelMatrix * totalNormal).xyz;
}
