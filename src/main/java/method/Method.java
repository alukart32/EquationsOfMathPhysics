package method;

import data.FileData;
import filestream.FileWrite;
import func.Func;

public class Method {

    private FileData fileData;

    FileWrite fileWrite = new FileWrite();

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

        tau = T / M;
        h = (b - a) / N;
    }

    private void setData(){
        c = fileData.getC();
        a = fileData.getA();
        b = fileData.getB();
        T = fileData.getT();
        N = fileData.getN();
        M = fileData.getM();
    }

    private boolean isSteady(){
        return ((Math.abs(c)*tau >= h) && c > 0);
    }

    public void solve(){
        fileWrite.cleanFile();

        if (isSteady()){
            if (isSteady())
                System.out.println("Схема устойчива");
            else
                System.out.println("Схема неустойчива");

            // инициализация сетки
            // x [ 0 1 2 3 4 5 ... N ] - точки

            double[] x = new double[(int)N],
            // px [ 0 1 2 3 4 5 ... N ] - предыдущий слой x-ов
                    px = new double[(int)N];

            double t;

            // инициализация значений на сетке

            // значения функции в слое i
            double[] ux = new double[(int)N];

            x[0] = a;
            for (int j = 1; j < N; j++)
            {
                x[j] = a + j*h;
                // начальное граничное условие
                ux[j] = f[2].func(x[j],0);
            }

            // расчётный алгоритм
            double r = (c * tau) / h;

            /** текущее значение уровня
             *
             *  left   o
             *       down
             */
            // значение слева от o
            double left;
            // значение справа от o
            double down;
            // текущий слой ti c 1 индекса !
            double[] currLayer = new double[(int)N];

            // подготовка данных
            System.arraycopy(ux,0, px, 0, (int)N);

            /**
             * Значения для определения погрешности
             */
            double max_eps_layer = 0, eps_layer;
            double max_eps = 0;
            double exat_solve;

            /**
             * Подсчёт в узлах сетки с 1 по M t-слой
             */
            for (int i = 1; i < M; i++) {

                /**
                 *  Зададим значение ti
                 */
                t = f[3].func(0, tau*i);
                left = t;

                fileWrite.write("*****************************************************************************************************************************");

                fileWrite.write("Временной слой: " + i);

                fileWrite.write("-----------------------------------------------------------------------------------------------------------------------------");


                fileWrite.write("\t\ti\t\t\tXi\t\t\t\t\tUij\t\t\t\t\t\tU(Xi,Tj)\t\t\t\t\t\tEPS");

                fileWrite.write("\n");

                /**
                 *  Проход по 1 .. N x, чтобы посчитать значение в узлах сетки на ti-слое
                 */
                for (int j = 1; j < N; j++) {
                    down = px[j];
                    currLayer[j] = (h*tau*f[1].func(x[j], t) + h*left + c*tau*down)/(h + c*tau);//tau*f[1].func(x[j], t) + left + r*down;

                    exat_solve = f[0].func(x[j], t);
                    eps_layer = Math.abs(exat_solve - currLayer[j]);

                    left = currLayer[j];


                    if (eps_layer > max_eps_layer)
                        max_eps_layer = eps_layer;

                    if(eps_layer < 1e-14)
                        eps_layer = 0;

                    fileWrite.write("\t\t" + j + "\t\t\t" + x[j] + "\t\t\t" + currLayer[j] + "\t\t\t" + exat_solve + "\t\t\t" + eps_layer);
                }

                fileWrite.write("\n");

                if(max_eps_layer < 1e-14)
                    max_eps_layer = 0;

                fileWrite.write("Максимальная погрешность на    " + i +"    слое:    " + max_eps_layer);
               if (max_eps_layer > max_eps)
                    max_eps = max_eps_layer;
                max_eps_layer = 0;

                if(max_eps < 1e-14)
                    max_eps = 0;

                System.arraycopy(currLayer,0, px, 0, (int)N);
            }
            fileWrite.write("*****************************************************************************************************************************");

            fileWrite.write("\n");

            fileWrite.write("Максимальная погрешность на сетке: " + max_eps);
        }
        else{
            fileWrite.write("Схема неустойчива");
            System.out.println("Схема неустойчива");
        }
    }
}