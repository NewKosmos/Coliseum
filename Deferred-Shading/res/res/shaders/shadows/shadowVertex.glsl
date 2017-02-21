#version 130

//---------CONSTANT------------
const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

//---------IN------------
layout(location = 0) in vec3 in_position;
layout(location = 4) in ivec3 in_jointIndices;
layout(location = 5) in vec3 in_weights;

//---------UNIFORM------------
uniform mat4 mvpMatrix;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform bool animated;

//---------MAIN------------
void main(void) {
	vec4 totalLocalPos = vec4(0.0);

    if (animated) {
        for (int i = 0; i < MAX_WEIGHTS; i++){
            vec4 localPosition = jointTransforms[in_jointIndices[i]] * vec4(in_position, 1.0);
            totalLocalPos += localPosition * in_weights[i];
        }
	} else {
	    totalLocalPos = vec4(in_position, 1.0);
	}

	gl_Position = mvpMatrix * totalLocalPos;
}