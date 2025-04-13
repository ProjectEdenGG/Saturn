#version 150
uniform sampler2D Sampler0;in vec3 Position;in vec4 Color;in vec2 UV0;uniform mat4 ModelViewMat;uniform mat4 ProjMat;out vec4 vertexColor;out vec2 uv;void main(){gl_Position=ProjMat*ModelViewMat*vec4(Position,1.);vertexColor=Color;uv=UV0;vec2 size=textureSize(Sampler0,0);if(size.y>=2*size.x&&size.x<256)uv.y/=2*size.y/size.x;}