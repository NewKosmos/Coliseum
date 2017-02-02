//---------CONSTANT------------
const int SHADOW_PCF = 2;
const float SHADOW_BIAS = 0.0002;
const float SHADOW_DARKNESS = 0.7;

const vec2 LIGHT_BIAS = vec2(0.7, 0.6);

//---------SHADOW------------
float shadow(sampler2D shadowMap, vec4 shadowCoords, float shadowMapSize) {
	float shadowTexelSize = 1.0 / shadowMapSize;
	float shadowHalfw = shadowTexelSize * 0.5;
	float shadowTotal = 0.0;
	float shadowValue = 0.0;
	float shadowShadeFactor;
	shadowValue = texture(shadowMap, shadowCoords.xy + vec2(0 + shadowHalfw, 0 + shadowHalfw)).r;

    if (shadowCoords.x > 0.0 && shadowCoords.x < 1.0 && shadowCoords.y > 0.0 && shadowCoords.y < 1.0 && shadowCoords.z > 0.0 && shadowCoords.z < 1.0) {
        if (shadowValue + SHADOW_BIAS < shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowValue = texture(shadowMap, shadowCoords.xy + vec2(shadowTexelSize + shadowHalfw, 0 + shadowHalfw)).r;

        if (shadowValue  + SHADOW_BIAS< shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowValue = texture(shadowMap, shadowCoords.xy + vec2(0 + shadowHalfw, shadowTexelSize + shadowHalfw)).r;

        if (shadowValue + SHADOW_BIAS < shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowValue = texture(shadowMap, shadowCoords.xy + vec2(shadowTexelSize + shadowHalfw, shadowTexelSize + shadowHalfw)).r;

        if (shadowValue + SHADOW_BIAS < shadowCoords.z) {
            shadowTotal += SHADOW_DARKNESS * shadowCoords.w;
        }

        shadowShadeFactor = 1.0 - (shadowTotal / 4.0);
    } else {
        shadowShadeFactor = 1.0;
    }

    return shadowShadeFactor;
}

/*
    float totalTextels = (SHADOW_PCF * 2.0 + 1) * (SHADOW_PCF * 2.0 + 1.0);
    float texelSize = 1.0 / shadowMapSize;
    float total = 0.0;

    for (int x = -SHADOW_PCF; x <= SHADOW_PCF; x++) {
        for (int y = -SHADOW_PCF; y <= SHADOW_PCF; y++) {
            float shadowValue = texture(shadowMap, pass_shadowCoords.xy + vec2(x, y) * texelSize).r;

            if (pass_shadowCoords.z > shadowValue + SHADOW_BIAS) {
                total += 1.0;
            }
        }
    }

    total /= totalTextels;
    return 1.0 - (total * SHADOW_DARKNESS * pass_shadowCoords.w);
*/