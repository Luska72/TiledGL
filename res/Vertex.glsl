#version 120

uniform vec2 newPosition;

void main(void)
{
    gl_Position.x = gl_Position.x + newPosition.x;
    gl_Position.y = gl_Position.y + newPosition.y;
}