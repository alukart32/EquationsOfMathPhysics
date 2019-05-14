import data.FileData;
import func.Func;
import method.Method;

public class MainApp {
    public static void main(String ...args){
        FileData fileData = new FileData();

        Func[] funcs = new Func[4];

        // F - точное решение
        funcs[0] = (x, t) -> x + t;
        // f - приближённое решение (ξ)
        funcs[1] = (x, t) -> 2;
        // начальные условия для x
        funcs[2] = (x, t) -> x;
        // начальные условия для t
        funcs[3] = (x, t) -> t;

        Method solveEquation = new Method(fileData, funcs);
        solveEquation.solve();

    }
}
