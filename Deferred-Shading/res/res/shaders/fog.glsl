//---------INCLUDES------------
#include "maths.glsl"

//---------FOG VISIBILITY------------
float visibility(vec4 positionRelativeToCam, float fogDensity, float fogGradient) {
	return clamp(exp(-pow((length(positionRelativeToCam.xyz) * fogDensity), fogGradient)), 0.0, 1.0);
}
