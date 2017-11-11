import java.nio.*;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;

public class Main extends JFrame implements GLEventListener
{
    private GLCanvas myCanvas;
    private int rendering_program;
    private int vao[] = new int[1];

    public Main()
    {
        setTitle("Chapter 2 - program 1");
        setSize(600, 400);
        setLocation(200, 200);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        setVisible(true);
    }

    public void display (GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(rendering_program);
        gl.glDrawArrays(GL_POINTS, 0, 1 );
    }

    public static void main(String[] args)
    {
        new Main();
    }

    public void init(GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        rendering_program = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    private int createShaderProgram()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        String vshaderSource[] =
        { "#version 430\n",
          "void main(void) \n",
          "{ gl_Position = vec4(0.0, 0.0, 0.0, 1.0); } \n",
        };

        String fshaderSource[] =
        { "#version 430 \n",
          "out vec4 color; \n",
          "void main(void) \n",
          "{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n",
        };

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 3, vshaderSource, null, 0);
        gl.glCompileShader(vShader);

        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 4, fshaderSource, null,  0);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    public void dispose(GLAutoDrawable drawable) {}
}
