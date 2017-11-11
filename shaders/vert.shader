#version 430
uniform float offset;

mat4 buildRotateY(float rad)
{
    mat4 yrot = mat4(cos(rad), 0.0, sin(rad), 0.0,
                     0.0, 1.0, 0.0, 0.0,
                     -sin(rad), 0.0, cos(rad), 0.0,
                     0.0, 0.0, 0.0, 1.0 );

    return yrot;
}

void main(void)
{
  if (gl_VertexID == 0)
  {
        vec4 newPt = vec4(0.25 + offset, -0.25 , 0.0, 1.0) * buildRotateY(90.0);
        gl_Position = newPt;
  }
  else if (gl_VertexID == 1)
  {
        vec4 newPt = vec4(-0.25 + offset, -0.25, 0.0, 1.0) * buildRotateY(90.0);
        //vec4 newPt = vec4(-0.25 + offset, -0.25, 0.0, 1.0);
        gl_Position = newPt;
  }
  else
  {
        vec4 newPt = vec4(0.25 + offset, 0.25, 0.0, 1.0) * buildRotateY(90.0);
        gl_Position = newPt;
  }
}
