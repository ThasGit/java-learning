package cc.thas.lambda;

import java.lang.invoke.*;

public class JvmLambda2 {

    public static void jvmLambda2(int ii) {
        // 不能直接定义常量, 会被逃逸分析优化
        final int i2 = ii;
        MyFunctionInterface myFunctionInterface = (a) -> {
            System.out.println("jvmLambda2");
            return String.valueOf(a + i2);
        };
        // invokeDynamic 的作用就是生成函数式接口的匿名类的一个对象, 这个对象会调用 lambda 方法
        myFunctionInterface.testMethod(1);
    }


    /**
     * 捕获式的 lambda 的 java 代码实现
     *
     * @throws Throwable
     */
    public static void myLambda2() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // 用来生成匿名类的方法(get$lambda)的methodType, 返回结果是匿名类的类型, 参数是非捕获的参数
        MethodType makeAnonymousClassMethodType = MethodType.methodType(MyFunctionInterface.class, Integer.TYPE);

        String functionalInterfaceName = "testMethod";
        // 生成的匿名类中的方法的methodType/函数式接口的methodType
        MethodType functionalInterfaceMethodType = MethodType.methodType(String.class, Integer.TYPE);

        // 实际调用该匿名类方法时使用的methodType, 一般来说跟方法定义的methodType相同
        MethodType instantiatedMethodType = functionalInterfaceMethodType;

        MethodType lambdaMethodType = MethodType.methodType(String.class, Integer.TYPE, Integer.TYPE);
        // lambda方法的方法句柄
        MethodHandle lambdaMethod = lookup.findStatic(JvmLambda2.class, "lambda", lambdaMethodType);
        // callsite 对象只会生成一次
        CallSite callSite = LambdaMetafactory.metafactory(lookup,
                functionalInterfaceName, makeAnonymousClassMethodType,
                functionalInterfaceMethodType, lambdaMethod, instantiatedMethodType);
        // 生成匿名类也只会生成一次, 有缓存

        // 生成匿名类的对象, 需要继续将非捕获的参数作为构造参数传递进代理对象
        MyFunctionInterface targetAnonymousClassObject = (MyFunctionInterface) callSite.getTarget().invoke(11);
        targetAnonymousClassObject.testMethod(1222);
        // 生成匿名类的对象
        Object target2 = callSite.getTarget().invoke(2);

        // 类是相同的
        System.out.println("class equals:" + targetAnonymousClassObject.getClass().equals(target2.getClass()));
        // 代理对象是不同的, 每次都需要把非捕获的参数传递进构造方法
        System.out.println("object equals:" + (targetAnonymousClassObject == target2));
    }

    /**
     * 比起原lambda , 需要多出一个非捕获的参数
     *
     * @param i  原lambda的参数
     * @param i2 非捕获的参数
     * @return
     */
    public static String lambda(int i, int i2) {
        System.out.println("myLambda2");
        return String.valueOf(i + i2);
    }
}
