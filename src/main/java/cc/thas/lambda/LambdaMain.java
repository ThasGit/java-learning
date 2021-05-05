package cc.thas.lambda;

import java.lang.invoke.*;

public class LambdaMain {

    public static void main(String[] args) throws Throwable {
        // 非捕获的 lambda
        JvmLambda1.jvmLambda();
        JvmLambda1.myLambda();
        // 捕获了的 lambda
        JvmLambda2.jvmLambda2(1);
        JvmLambda2.myLambda2();
        // 引用了实例变量的的lambda表达式, 把实例方法看做是第一个参数是this指针的静态, 与捕获式lambda是相同的
        JvmLambda3 jvmLambda3 = new JvmLambda3();
        jvmLambda3.jvmLambda3();
        jvmLambda3.myLambda3();
    }


}

