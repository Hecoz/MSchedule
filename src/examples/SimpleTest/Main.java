package examples.SimpleTest;

import static tju.MSchedule.MSchedule.fireEvent;

public class Main {

    public static void main(String[] args){

        final HasSelfPrivateNum hspn = new HasSelfPrivateNum();

        Thread thread1 = new Thread() {

            int a = 0 , b = 0;
            public void run() {

                fireEvent("Thread1assigna");
                a = hspn.x;
                fireEvent("Thread1changex");
                hspn.x = a - 1;
                fireEvent("Thread1assignb");
                b = hspn.y;
                fireEvent("Thread1changey");
                hspn.y = b + 1;
            }
        };

        Thread thread2 = new Thread() {
            int c = 0;
            public void run() {
                fireEvent("Thread2assignc");
               c = hspn.x;
                fireEvent("Thread2changey");
               hspn.y = c - 1;
               if(hspn.y <= 0 ){
                   System.out.println(hspn.y);
                   assert hspn.y <= 0 : (" wrong detected " + hspn.y);
               }
            }
        };
        thread1.setName("thread1");
        thread2.setName("thread2");

        thread1.start();
        thread2.start();

    }
}
