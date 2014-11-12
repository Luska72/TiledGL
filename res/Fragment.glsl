#version 120

uniform vec2 light;


void main(void)
{    
    vec2 pixelPosition = gl_FragCoord.xy; // pos
    float dist = length(light-pixelPosition);
    float color = 1/dist;
    gl_FragColor = vec4(color, color, color, 1.0f);
}