package method;

import data.FileData;
import filestream.FileWrite;
import filestream.parser.SolveEquation;
import func.Func;

public class Method {

    private FileData fileData;

    private String filepath = "E:\\Programming\\Курс_3\\Numeric_Methods\\lab4\\trash\\output.txt";
    FileWrite fileWrite = new FileWrite(filepath);

    private double
            tau,
            h,
            c,
            a,
            b,
            T, N, M;

    Func[] f;


    public Method(FileData fileData, Func[] funcs) {
        this.fileData = fileData;
        this.f = funcs;
        setData();
    }

    private void setData(){
        c = fileData.getC();
        a = fileData.getA();
        b = fileData.getB();
        T = fileData.getT();
        N = fileData.getN();
        M = fileData.getM();

        SolveEquation u = new SolveEquation(fileData, f);
        u.prepare();
    }

    private boolean isSteady(){
        return ((c*tau <= h) && c > 0);
    }

    public void solve(){
        fileWrite.cleanFile();

        tau = T / M;
        h = (b - a) / N;

        if (true){
            if (!isSteady())
                System.out.println("Схема неустойчива");

            System.out.println("Схема устойчива");

            // инициализация сетки
            // x [ 0 1 2 3 4 5 ... N ]
            double[] x = new double[(int)M],
            // t [ 0 1 2 3 4 5 ... M ]
                    t = new double[(int)N];

            // инициализация значений на сетке

            // нулевой уровень по x
            double[] ux = new double[(int)M];
            // нулевой уровень по t
            double[] ut = new double[(int)N];

            // подготовка системы
            // подготовка решётки
            // просчёт x[i], t[i]

            x[0] = a;
            // x [ 1 ... N-1]
            for (int i = 1; i < M; i++)
            {
                x[i] = a + i*h;
                // начальное граничное условие
                ux[i] = f[2].func(x[i],0);
            }
            for (int j = 0; j < N; j++)
            {
                t[j] = j*tau;
                // конечное гран условие
                ut[j] = f[3].func(0, t[j]);
            }

            // расчётный алгоритм
            double r = (c * tau) / h;

            /** текущее значение уровня
             *
             *  left   o
             *       down
             */
            // значение слева от o
            double left = ux[0];
            // значение справа от o
            double down = ut[0];
            // предыдущий слой t c 0 индекса !
            double[] prevLayer = new double[(int)N];
            // текущий слой t c 1 индекса !
            double[] currLayer = new double[(int)N];

            // подготовка данных
            System.arraycopy(ut,0, prevLayer, 0, (int)N);

            fileWrite.write("Номер точки на tj    xi на временном слое      Приближенное решение        Точное решение  	    Погрешность");
            fileWrite.write("-----------------------------------------------------------------------------------------------------------------------------");
            double max_eps_layer = 0, eps_layer;
            double max_eps = 0;
            double exat_solve;

            for (int i = 1; i < M; i++) {

                left = ux[i];

                fileWrite.write("       " + i + "			          " + left);

                for (int j = 0; j < N-1; j++) {
                    currLayer[0] = ux[i];
                    down = prevLayer[j];
                    currLayer[j+1] = left - r*(left - down) + tau*f[2].func(x[i], t[j]);

                    left = currLayer[j];

                    exat_solve = f[0].func(x[i], t[j]);
                    eps_layer = Math.abs(exat_solve - left);

                    if (eps_layer > max_eps_layer)
                        max_eps_layer = eps_layer;

                    if(eps_layer < 1e-14)
                        eps_layer = 0;

                    fileWrite.write("			                                           " + left + "			           " + exat_solve + "			        " + eps_layer);
                    fileWrite.write("			   ");

                }

                fileWrite.write("\n");

                if(max_eps_layer < 1e-14)
                    max_eps_layer = 0;

                fileWrite.write("Максимальная погрешность на    " + i +"    слое:    " + max_eps_layer);
                fileWrite.write("--------------------------------------------------------------------------------------------------------");
                if (max_eps_layer > max_eps)
                    max_eps = max_eps_layer;
                max_eps_layer = 0;

                fileWrite.write("\n");

                if(max_eps < 1e-14)
                    max_eps = 0;

                fileWrite.write("Максимальная погрешность на сетке: " + max_eps);

                System.arraycopy(currLayer,0, prevLayer, 0, (int)N);
            }
        }
        else{
            fileWrite.write("Схема неустойчива");
            System.out.println("Схема неустойчива");
        }
    }

}
