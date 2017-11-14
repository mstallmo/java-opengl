import graphicslib3D.*;
import graphicslib3D.GLSLUtils.*;
import java.nio.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.*;


public class Main extends JFrame implements GLEventListener
{
    private GLCanvas myCanvas;
    private int rendering_program;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    private float cameraX, cameraY, cameraZ;
    private float cubeLocX, cubeLocY, cubeLocZ;
    private float pyrLocX, pyrLocY, pyrLocZ;
    private GLSLUtils util = new GLSLUtils();

    private MatrixStack mvStack = new MatrixStack(20);

    public Main()
    {
        setTitle("Chapter 4 - program 1a");
        setSize(600, 600);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        getContentPane().add(myCanvas);
        this.setVisible(true);

        FPSAnimator animator = new FPSAnimator(myCanvas, 50);
        animator.start();
    }

    public void display (GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

        gl.glClear(GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(rendering_program);

        int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

        float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        Matrix3D pMat = perspective(60.0f, aspect, 0.1f,  1000.0f);

        mvStack.pushMatrix();
        mvStack.translate(-cameraX, -cameraY, - cameraZ);
        double amt = (double)(System.currentTimeMillis())/1000.0;

        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);

        //Pyramid "Sun"
        mvStack.pushMatrix();
        mvStack.translate(pyrLocX, pyrLocY, pyrLocZ);
        mvStack.pushMatrix();
        mvStack.rotate((System.currentTimeMillis())/10.0, 1.0, 0.0, 0.0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDrawArrays(GL_TRIANGLES, 0, 18);
        mvStack.popMatrix();

        //Cube planet
        mvStack.pushMatrix();
        mvStack.translate(Math.sin(amt)*4.0f, 0.0f, Math.cos(amt)*4.0f);
        mvStack.pushMatrix();
        mvStack.rotate((System.currentTimeMillis())/10.0, 0.0, 1.0, 0.0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();

        //Smaller cube, moon
        mvStack.pushMatrix();
        mvStack.translate(0.0f, Math.sin(amt)*2.0f, Math.cos(amt)*2.0f);
        mvStack.rotate((System.currentTimeMillis())/10.0, 0.0, 0.0, 1.0);
        mvStack.scale(0.25, 0.25, 0.25);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();


    }

    public void init(GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) drawable.getGL();
        rendering_program = createShaderProgram();
        setupVertices();
        cameraX = 0.0f; cameraY = 0.0f; cameraZ = 12.0f;
        cubeLocX = 0.0f; cubeLocY = -2.0f; cubeLocZ = 0.0f;
        pyrLocX = 2.0f; pyrLocY = 2.0f; pyrLocZ = 0.0f;
    }


    private void setupVertices()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float[] cube_positions =
        {-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
         1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
         1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
         1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
         1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
         -1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
         -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
         -1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
         -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
         1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
         -1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
         1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
        };

        float[] pyramid_positions =
        {	-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,    //front
            1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,    //right
            1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,  //back
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,  //left
            -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
            1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f  //RR
        };

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cube_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pyramid_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);
    }

    private Matrix3D perspective(float fovy, float aspect, float n, float f)
    {
        float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);
        Matrix3D r = new Matrix3D();
        r.setElementAt(0,0,A);
        r.setElementAt(1,1,q);
        r.setElementAt(2,2,B);
        r.setElementAt(3,2,-1.0f);
        r.setElementAt(2,3,C);
        r.setElementAt(3,3,0.0f);
        return r;
    }

    public static void main(String[] args)
    {
        new Main();
    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    public void dispose(GLAutoDrawable drawable) {}

    private int createShaderProgram()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        int[] linked = new int[1];

        String vshaderSource[] = readShaderSource("vert.shader");
        String fshaderSource[] = readShaderSource("frag.shader");

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glCompileShader(vShader);
        checkOpenGLError();
        gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
        if (vertCompiled[0] == 1)
        {
            System.out.println(". . . vertex compilation success.");
        }
        else
        {
            System.out.println(". . . vertex compilation failed.");
            printShaderLog(vShader);
        }

        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null,  0);
        gl.glCompileShader(fShader);
        checkOpenGLError();
        gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
        if (fragCompiled[0] == 1)
        {
            System.out.println(". . . fragment compilation success");
        }
        else
        {
            System.out.println(". . . fragment compilation failed");
            printShaderLog(fShader);
        }

        if ((vertCompiled[0] != 1) || (fragCompiled[0] != 1))
        {
            System.out.println("\nCompilation error; return-flags:");
            System.out.println(" vertCompiled = " + vertCompiled[0] + " ; fragCompiled = " + fragCompiled[0]);
        }
        else
        {
            System.out.println("Successful Compilation");
        }

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);
        checkOpenGLError();
        gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked, 0);
        if (linked[0] == 1)
        {
            System.out.println(". . . linking succeeded.");
        }
        else
        {
            System.out.println(". . . linking failed");
            printProgramLog(vfprogram);
        }

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    private String[] readShaderSource(String filename)
    {
        Vector<String> lines = new Vector<>();
        Scanner sc;

        try
        {
            sc = new Scanner(new File("shaders/"+filename));
        }
        catch (IOException e)
        {
            System.err.println("IOException reading file: " + e);
            return null;
        }

        while (sc.hasNext())
        {
            lines.addElement(sc.nextLine());
        }

        String[] program = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++)
        {
            program[i] = (String) lines.elementAt(i) + "\n";
        }
        return program;
    }

    private void printShaderLog(int shader)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0)
        {
            log = new byte[len[0]];
            gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
            System.out.println("Shader Info Log: ");
            for (int i = 0; i < log.length; i++)
            {
                System.out.print((char) log[i]);
            }
        }
    }

    void printProgramLog(int prog)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0)
        {
            log = new byte[len[0]];
            gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
            System.out.println("Program Info Log: ");
            for (int i = 0; i < log.length; i++)
            {
                System.out.print((char) log[i]);
            }
        }
    }

    boolean checkOpenGLError()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        boolean foundError = false;
        GLU glu = new GLU();
        int glErr = gl.glGetError();
        while (glErr != GL_NO_ERROR)
        {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl.glGetError();
        }
        return foundError;
    }



}
