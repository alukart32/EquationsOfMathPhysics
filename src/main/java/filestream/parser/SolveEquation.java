package filestream.parser;

import data.FileData;
import filestream.FileWrite;
import func.Func;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolveEquation {

    private FileData fileData;

    FileWrite fileWrite = new FileWrite();

    private double
                    tau,
                    h,
                    c,
                    a,
                    b;

    private int T, N, M;

    Func[] f;

    public SolveEquation(FileData fileData, Func[] funcs) {
        this.fileData = fileData;
        this.f = funcs;
        setData();
    }

    private void setData(){
        c = fileData.getC();
        a = fileData.getA();
        b = fileData.getB();
        T = (int)fileData.getT();
        N = (int)fileData.getN();
        M = (int)fileData.getM();

        tau = (double)T / N;
        h = (b - a) / M;
    }

    public void prepare(){
        fileWrite.cleanFile();

        if (true){
            // инициализация сетки

            double[] x = new double[(int)M],
                    t = new double[(int)N];

            // инициализация значений на сетке

            double[][] u = new double[M][];
            for (int i = 0; i < M; i++)
                u[i] = new double[N];

            x[0] = a;
            for (int i = 1; i < M; i++)
            {
                x[i] = a + i*h;
                u[i][0] = f[2].func(x[i], 0);
            }
            // по t
            for (int i = 0; i < N; i++)
            {
                t[i] = i*tau;
                u[0][i] = f[3].func(0, t[i]);
            }

            double r = (c * tau) / h;
            for (int i = 1; i < M; i++)
            {
                for (int j = 0; j < N - 1; j++)
                {
                    u[i][j + 1] = u[i][j] - r*(u[i][j] - u[i - 1][j]) + tau*f[1].func(x[i], t[j]);
                }
            }

            // после полученного решения
            // вывод по слоям с точным и приближ. решением и погрешностью

            fileWrite.write("Номер точки на временном слое    Координата на временном слое      Приближенное решение        Точное решение  	    Погрешность");
            fileWrite.write("-----------------------------------------------------------------------------------------------------------------------------");
            double max_eps_layer = 0, eps_layer;
            double max_eps = 0;

            for (int i = 0; i < M; i++)
            {
                fileWrite.write("           " + (i+1) + "			                " + x[i]);
                for (int j = 0; j < N; j++)
                {
                    double exat_solve = f[0].func(x[i], t[j]);
                    eps_layer = Math.abs(exat_solve - u[i][j]);
                    if (eps_layer > max_eps_layer)
                        max_eps_layer = eps_layer;

                    if(eps_layer < 1e-14)
                        eps_layer = 0;

                    fileWrite.write("			                                                              " + u[i][j] + "			           " + exat_solve + "			        " + eps_layer);
                    fileWrite.write("			   ");
                }
                fileWrite.write("\n");

                if(max_eps_layer < 1e-14)
                    max_eps_layer = 0;

                fileWrite.write("Максимальная погрешность на    " + (i+1)  +"    слое:    " + max_eps_layer);

                fileWrite.write("--------------------------------------------------------------------------------------------------------");

                if(max_eps < 1e-14)
                    max_eps = 0;

                if (max_eps_layer > max_eps)
                    max_eps = max_eps_layer;
                max_eps_layer = 0;
            }
            fileWrite.write("\n");

            if(max_eps < 1e-14)
                max_eps = 0;

            fileWrite.write("Максимальная погрешность на сетке: " + max_eps);
        }
        else{
            fileWrite.cleanFile();
            fileWrite.write("Схема неустойчива");
        }
    }

    private boolean isSteady(){
        return ((c*tau <= h) && c > 0);
    }
}
