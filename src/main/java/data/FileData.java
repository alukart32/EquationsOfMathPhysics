package data;

import filestream.FileRead;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileData {
    private double
                    // коэф. перед ux
                    c,
                    // отрезок
                    a, b,
                    T,
                    // кол-во шагов
                    N,
                    M;

    public FileData() {
        setData();
    }

    public void setData(){
        FileRead fileRead = new FileRead();
        double[] arr = fileRead.readData();

        c = arr[0];
        a = arr[1];
        b = arr[2];
        T = arr[3];
        N = arr[4];
        M = arr[5];
   }
}
